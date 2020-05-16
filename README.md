# pgrServer

![Alt text](Route.png?raw=true)

Introduction
------------
pgrServer is a routing service that is able to use pgRouting topologies 
to load data into a JGraphT graph for very fast searches even with dense networks.

The graph is created at startup when the topology is read from a PostgreSQL database. This graph though can be re-created at regular intervals by making a service request, for networks that have dynamic costs. 
 
As of this version, the following search algorithms are included as a service:

* Dijkstra ( for dense networks )
* A-Star ( for dense networks )
* Bellman-Ford ( for sparse networks )
* BFS ( for sparse networks )
* Johnson ( for sparse networks )
* Floyd-Warshall ( for sparse networks )

Requirements
------------
* PostgreSQL > 9.4
* PostGIS
* PgRouting (to create a topology)
* Maven



