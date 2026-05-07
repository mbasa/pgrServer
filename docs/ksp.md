# K-Shortest Paths in pgrServer

## Background

The original intent was to implement K-Shortest Paths using **Yen's algorithm**
(`YenKShortestPath` from JGraphT). While Yen's is the canonical algorithm for
finding the true K loopless shortest paths, it is fundamentally unsuitable for
dense networks such as full-city OpenStreetMap data. This document explains why,
and describes the edge-penalization approach that replaced it.

---

## Why Yen's Algorithm Fails on Dense Networks

### How Yen's algorithm works

Yen's algorithm finds K shortest paths by iterating the following steps:

1. Find the 1st shortest path using a standard Dijkstra search.
2. For each subsequent path (2nd, 3rd, … K-th):
   - Walk along every node of the previous best path. Each such node is called a **spur node**.
   - For each spur node, temporarily remove certain edges and nodes from the graph, then run a **full Dijkstra search** from that spur node to the destination on the modified subgraph.
   - Each resulting spur path is combined with the portion of the previous path leading up to the spur node (the **root path**) to form a candidate path.
   - All candidates are inserted into a priority queue; the cheapest becomes the next shortest path.

### Computational cost

For a path of length L edges and K desired paths, Yen's algorithm runs up to
**O(K × L)** Dijkstra searches. Its overall time complexity is:

```
O(K × V × (E + V log V))
```

where V is the number of vertices and E the number of edges. On a **dense
network** where E ≈ V², this expands to:

```
O(K × V³ log V)
```

For a full metropolitan OSM graph (V ≈ 500,000, E ≈ 2,000,000), finding just
K=3 paths via Yen's requires on the order of **tens of billions of operations**
per request.

### Memory exhaustion

Beyond raw CPU cost, Yen's algorithm accumulates memory in two ways:

1. **Candidate priority queue.** Every spur Dijkstra run deposits new candidate
   paths into a shared queue. On a dense graph, each run can produce hundreds of
   candidates before the target is reached. With L spur nodes per path and K
   paths, the queue can hold thousands of full `GraphPath` objects simultaneously,
   each storing a complete vertex and edge list.

2. **Spur Dijkstra exploration depth.** Dijkstra orders nodes by *cost*, not by
   hop count. A hop-count `PathValidator` can limit the length of any individual
   path, but cannot prevent Dijkstra from exploring millions of low-cost, short-hop
   nodes on a dense graph before it either reaches the destination or hits the
   limit. Each explored node is tracked in the Dijkstra distance map, consuming
   heap proportional to the number of nodes visited.

In practice, pgrServer threw `java.lang.OutOfMemoryError: Java heap space` when
requesting as few as K=2 paths on a dense Tokyo OSM dataset, even with a 50-hop
limit applied via `PathValidator`.

---

## The Present Approach: Edge Penalization

Instead of Yen's algorithm, pgrServer uses an **edge-penalization** strategy
that runs exactly K Dijkstra searches — one per path — with no candidate queue.

### Algorithm

```
penalizedWeights ← empty map

for i = 1 to K:
    graph ← AsWeightedGraph(defaultGraph, penalizedWeights)
    path  ← BidirectionalDijkstra(graph, source, target)

    if path is empty: stop

    record path

    for each edge in path:
        penalizedWeights[edge] ← currentWeight(edge) × ksp.penalty.factor
```

`AsWeightedGraph` is a lightweight **view** over `defaultGraph` — it does not
copy the graph. For any edge not present in `penalizedWeights`, it falls through
to the underlying graph weight. The map therefore stays small: at most
`K × path_length` entries (e.g. 3 × 30 = 90 entries for typical urban routes).

### Why this works

After path 1 is found, each of its edges is multiplied by `ksp.penalty.factor`
(default `10.0`). Dijkstra for path 2 now sees those edges as 10× more expensive
and routes around them, producing a meaningfully different alternative. The same
penalty is accumulated for path 2's edges before searching for path 3.

### Memory profile

| Property | Yen's Algorithm | Edge Penalization |
|---|---|---|
| Dijkstra runs per request | O(K × L) | K |
| Candidate queue size | O(K × L × path_length) | none |
| Extra heap per request | gigabytes (dense graphs) | kilobytes |
| Graph copies created | none (masked views) | none (weighted view) |

### Trade-offs

The edge-penalization approach does **not** guarantee mathematically optimal
K-shortest paths. Specifically:

- The 1st path is always the true shortest path.
- Subsequent paths are the shortest paths given the accumulated penalties from
  prior paths. They are genuine alternatives, but a slightly shorter alternative
  that shares some edges with a previous path may be skipped.
- Higher `ksp.penalty.factor` values produce more divergent routes. Lower values
  may allow paths that share many edges with earlier ones.

For routing server use cases — presenting a user with a set of meaningfully
different route options — this behaviour is preferable to Yen's, which on
real-world dense graphs either exhausts memory or takes impractically long to
compute.

---

## Configuration

```properties
# application.properties

# Multiplier applied to each edge used in a found path before the next search.
# Higher values produce more divergent alternative routes.
# Default: 10.0
ksp.penalty.factor=10.0
```

---

## API Endpoints

| Endpoint | Parameters |
|---|---|
| `GET /api/node/kShortestPath` | `source` (int), `target` (int), `k` (optional, default 3, max 10) |
| `GET /api/latlng/kShortestPath` | `source_x`, `source_y`, `target_x`, `target_y` (double), `k` (optional, default 3, max 10) |

The response is a GeoJSON `FeatureCollection` with one `Feature` per path. Each
feature includes a `feat_length` property with the route distance in metres.

### Example

```shell
curl "http://localhost:8080/pgrServer/api/latlng/kShortestPath?\
source_x=139.620139631&source_y=35.710788822&\
target_x=139.620928086&target_y=35.699183061&k=3"
```
