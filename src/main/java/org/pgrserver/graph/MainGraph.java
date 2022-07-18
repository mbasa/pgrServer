/**
 * パッケージ名：org.pgrserver.graph
 * ファイル名  ：MainGraoh.java
 * 
 * @author mbasa
 * @since May 5, 2020
 */
package org.pgrserver.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.shortestpath.JohnsonShortestPaths;
import org.jgrapht.alg.tour.NearestNeighborHeuristicTSP;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.pgrserver.entity.PgrServer;
import org.pgrserver.repository.CustomRepository;
import org.pgrserver.repository.GraphRepository;
import org.pgrserver.util.DistanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 説明：
 *
 */
@Service
@Configurable
public class MainGraph {
        
    private static AbstractBaseGraph<Integer, LabeledWeightedEdge> 
        defaultGraph;
        
    private static ContractionHierarchyBidirectionalDijkstra<Integer, 
        LabeledWeightedEdge> chbd = null;
    
    private static Map<LabeledWeightedEdge,Double> lengthCost = 
            new HashMap<LabeledWeightedEdge,Double>();
    
    private final Logger logger = LoggerFactory.getLogger(MainGraph.class);
    
    @Autowired
    GraphRepository graphRepository;

    @Autowired
    CustomRepository custRepository;
    

    /**
     * コンストラクタ
     *
     */
    public MainGraph() {
    }
    
   
    @Value(value = "${graph.directed:true}")
    private boolean useDirectedGraph;
    
    public void createDefaultGraph() {
        logger.info("Creating Graph");
        
        List<PgrServer> pgrData = graphRepository.getGraph();
        
        chbd = null;
        
        if( useDirectedGraph ) {
            logger.info("Creating Directed Graph");
            defaultGraph = new DefaultDirectedWeightedGraph<Integer, 
                    LabeledWeightedEdge>(LabeledWeightedEdge.class);            
        }
        else {
            logger.info("Creating Undirected Graph");
            defaultGraph = new DefaultUndirectedWeightedGraph<Integer, 
                LabeledWeightedEdge>(LabeledWeightedEdge.class);
        }
        
        lengthCost.clear();
        
        for(PgrServer p : pgrData) {
            defaultGraph.addVertex((int)p.getSource());
            defaultGraph.addVertex((int)p.getTarget());

            LabeledWeightedEdge lwe = new LabeledWeightedEdge();
            lwe.setEdgeId(p.getId());                      
             
            defaultGraph.addEdge(
                    (int)p.getSource(),(int)p.getTarget(),lwe);      
            
            defaultGraph.setEdgeWeight(lwe, p.getCost());
            lengthCost.put(lwe,p.getLength());
            
            /**
             * setting up one-way streets by creating reverse route for 
             * normal roads. Applies only when set to use Directed Graph.
             */
            if( p.getReverse_cost() <= p.getCost() && useDirectedGraph ) { 
                LabeledWeightedEdge lweRc = new LabeledWeightedEdge();
                lweRc.setEdgeId(p.getId()); 
                
                defaultGraph.addEdge(
                        (int)p.getTarget(),(int)p.getSource(),lweRc);      
                
                defaultGraph.setEdgeWeight(lweRc, p.getCost());
                lengthCost.put(lweRc,p.getLength());
            }
        } 
        
        logger.info("Data received: "+pgrData.size());
        this.showUsedMem();                
    }

    public void showUsedMem() {
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("Memory Usage: "+memory);        
    }
    
    public List<List<Integer>> tsp(List<List<Double>> inPoints) {        
        List<List<Integer>> retVal = new ArrayList<List<Integer>>();
        
        DefaultUndirectedWeightedGraph<Integer, LabeledWeightedEdge> tspGraph = 
                new DefaultUndirectedWeightedGraph<Integer, 
                LabeledWeightedEdge>(LabeledWeightedEdge.class);
        
        List<PgrServer> pgrServer = new ArrayList<PgrServer>();
        
        /**
         * Populating the TSP Graph
         */
        int i = 0;
        for(List<Double> pts : inPoints ) {
            tspGraph.addVertex(i);
            i++;
            
            pgrServer.add( custRepository.findNearestNode(
                    pts.get(0), pts.get(1)) );            
        }
        
        i = 0;
        double fromLat,fromLng,toLat,toLng;
        
        for(int j=0;j<inPoints.size();j++) {
            List<Double> pts = inPoints.get(j);
            fromLng = pts.get(0);
            fromLat = pts.get(1);
            
            for(int k=0;k<inPoints.size();k++) {
                if( k == j )
                    continue;
                
                List<Double> pts2 = inPoints.get(k);
                toLng = pts2.get(0);
                toLat = pts2.get(1);
                
                LabeledWeightedEdge lwe = new LabeledWeightedEdge();
                lwe.setEdgeId(i);
                i++;
                
                double dist = DistanceUtil.euclidean(
                        fromLat, fromLng, toLat, toLng);
                
                tspGraph.addEdge(j, k, lwe);
                tspGraph.setEdgeWeight(j, k, dist);
            }
        }
                     
        /**
         * Running the TSP search. First point will be visited first.
         */
        NearestNeighborHeuristicTSP<Integer, LabeledWeightedEdge> nnhTsp = 
                new NearestNeighborHeuristicTSP<Integer, LabeledWeightedEdge>(
                        Integer.valueOf(0));
        
        GraphPath<Integer,LabeledWeightedEdge> path = nnhTsp.getTour(tspGraph);
        List<Integer> vertexList = path.getVertexList();
        
        for(i=0; i<vertexList.size()-2; i++) {
            int v1 = vertexList.get(i);
            int v2 = vertexList.get(i+1);
            int source = pgrServer.get(v1).getSource();
            int target = pgrServer.get(v2).getTarget();
            
            List<Integer> dPath = dijkstraSearch(source, target, null);
            
            if( dPath != null && !dPath.isEmpty()) {
                retVal.add( dPath  );
            }
            else {
                logger.error("*** PATH Not Found ("+source+","+target+") ***");
            }
        }
        
        return retVal;
    }
 
    public List<List<Integer>> allDirectedPaths(int start,int end,
            int maxEdges) {
        List<List<Integer>> arrList = new ArrayList<>();

        AllDirectedPaths<Integer, LabeledWeightedEdge> adp = 
                new AllDirectedPaths<Integer,LabeledWeightedEdge>(defaultGraph);                

        for( GraphPath<Integer,LabeledWeightedEdge> p : 
            adp.getAllPaths(start, end, true, maxEdges) ) {           
            List<Integer> list = p.getVertexList();
            arrList.add(convertVerticesToEdges(list));
        }     
        return arrList;
    }
    
    public List<Integer> astarSearch(int start,int end) {
        AStarAdmissibleHeuristic<Integer> heuristic = 
                new AStarAdmissibleHeuristic<Integer>() {
            @Override
            public double getCostEstimate(Integer o, Integer v1) {
                return 5d;
            }
        };
        
        AStarShortestPath<Integer,LabeledWeightedEdge> astarShortestPath = 
           new AStarShortestPath<Integer,LabeledWeightedEdge>(defaultGraph, 
                   heuristic);

        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            List<Integer> list =  astarShortestPath.getPath(
                    Integer.valueOf(start),
                    Integer.valueOf(end)).getVertexList();
            retVal = convertVerticesToEdges(list);
        }
        catch(Exception e) {
            ;
        }
        return retVal;                
    }
    
    public List<Integer> bellmanFordSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            List<Integer> list = BellmanFordShortestPath.findPathBetween(
                    defaultGraph,start, end).getVertexList();
            retVal = convertVerticesToEdges(list);
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
        
    @Value(value = "${chbDijkstra.thread.pool:7}")
    private int chbThreadPool;
    
    public List<Integer> chbDijkstraSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            if( chbd == null ) {
                logger.info("chbDijkstra Thread Pool Size: "+chbThreadPool);
                this.showUsedMem();
                
                ThreadPoolExecutor executor = 
                        (ThreadPoolExecutor) Executors.newFixedThreadPool(
                                chbThreadPool);
                
                chbd = new ContractionHierarchyBidirectionalDijkstra<Integer, 
                    LabeledWeightedEdge>(defaultGraph,executor);
                
                this.showUsedMem();
            }
            
            List<Integer> list =  
                    chbd.getPath(start, end).getVertexList();
            
            retVal = convertVerticesToEdges(list);                        
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
    
    public List<Integer> dijkstraSearch(int start,int end, String tableName) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            if( tableName == null || tableName.isBlank() ) {
                List<Integer> list =  
                    BidirectionalDijkstraShortestPath.findPathBetween(
                            defaultGraph,start, end).getVertexList();
                
                retVal = convertVerticesToEdges(list);
            }
            else {
                AsWeightedGraph<Integer, LabeledWeightedEdge> costGraph = 
                        new  AsWeightedGraph<Integer, LabeledWeightedEdge>
                (defaultGraph, lengthCost);
                        //custRepository.getCostTable(tableName));
                
                for(int i=1;i<10;i++) {
                    LabeledWeightedEdge lwe = new LabeledWeightedEdge();
                    lwe.setEdgeId(i);    

                    logger.info("Cost EdgeWeight for ID "+lwe.getEdgeId()+": "+costGraph.getEdgeSource(lwe)+","+costGraph.getEdgeTarget(lwe) );
                    logger.info("Default EdgeWeight for ID "+lwe.getEdgeId()+": "+defaultGraph.getEdgeWeight(lwe) );
                }
                
                List<Integer> list =  
                        BidirectionalDijkstraShortestPath.findPathBetween(
                                costGraph,start, end).getVertexList();            
                    
                retVal = convertVerticesToEdges(list);
            }
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
    
    public List<Integer> bfsSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            List<Integer> list =  
                    BFSShortestPath.findPathBetween(defaultGraph,
                    start, end).getVertexList();            
            retVal = convertVerticesToEdges(list);                        
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
    
    public List<Integer> floydWarshallSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            FloydWarshallShortestPaths<Integer, LabeledWeightedEdge> fwSp = 
                    new FloydWarshallShortestPaths<Integer, 
                    LabeledWeightedEdge>(defaultGraph);
            
            List<Integer> list =  
                    fwSp.getPath(start, end).getVertexList();            
            retVal = convertVerticesToEdges(list);                        
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
    
    public List<Integer> johnsonSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            JohnsonShortestPaths<Integer, LabeledWeightedEdge> jsp = 
                    new JohnsonShortestPaths<Integer, 
                    LabeledWeightedEdge>(defaultGraph);
            
            List<Integer> list =  
                    jsp.getPath(start, end).getVertexList();            
            retVal = convertVerticesToEdges(list);                        
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }
    
    public Set<Integer> drivingDistance(int source,double radius) {
        final Set<Integer> visited = new HashSet<Integer>();
        
        if( defaultGraph == null ) {
            return visited;
        }

        try {
             /**
              * Creating new graph with Length as cost
              */
            AsWeightedGraph<Integer, LabeledWeightedEdge> costGraph = 
                    new  AsWeightedGraph<Integer, LabeledWeightedEdge>
            (defaultGraph,lengthCost);
            
            ClosestFirstIterator<Integer, LabeledWeightedEdge> driveDist = 
                    new ClosestFirstIterator<Integer, LabeledWeightedEdge>(
                            costGraph, source, radius);
            
            while( driveDist.hasNext() ) {
                visited.add(driveDist.next());
            }
        }
        catch(Exception e) {
            ;
        }
        
        return visited;
    }
    
    private List<Integer> convertVerticesToEdges(List<Integer> list) {
        List<Integer> retVal = new ArrayList<Integer>();
        
        for(int x=0;x<list.size()-1;x++ ) {
            LabeledWeightedEdge lw = 
                    defaultGraph.getEdge(
                    list.get(x),list.get(x+1) );
            retVal.add(lw.getEdgeId());
        }   
        return retVal;
    }

}
