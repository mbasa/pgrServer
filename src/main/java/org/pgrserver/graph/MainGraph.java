/**
 * パッケージ名：org.pgrserver.graph
 * ファイル名  ：MainGraoh.java
 * 
 * @author mbasa
 * @since May 5, 2020
 */
package org.pgrserver.graph;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.pgrserver.entity.PgrServer;
import org.pgrserver.repository.GraphRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

/**
 * 説明：
 *
 */
@Service
@Configurable
public class MainGraph {
        
    private static DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> 
        defaultGraph;
    
    private final Logger logger = LoggerFactory.getLogger(MainGraph.class);
    
    @Autowired
    GraphRepository graphRepository;

    /**
     * コンストラクタ
     *
     */
    public MainGraph() {
    }
    
    public void createDirectedGraph() {
        logger.info("Creating Graph");
        
        List<PgrServer> pgrData = graphRepository.getGraph();
        
        defaultGraph = new DefaultDirectedWeightedGraph<Integer, 
                DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for(PgrServer p : pgrData) {
            defaultGraph.addVertex((int)p.getSource());
            defaultGraph.addVertex((int)p.getTarget());
        }

        for(PgrServer p : pgrData) {      
            LabeledWeightedEdge lwe = new LabeledWeightedEdge();
            lwe.setEdgeId(p.getId());
                        
            defaultGraph.addEdge(
                    (int)p.getSource(),(int)p.getTarget(),lwe);
            
            defaultGraph.setEdgeWeight(
                    defaultGraph.getEdge((int)p.getSource(),
                            (int)p.getTarget()), p.getCost());
        } 
        logger.info("Data received: "+pgrData.size());
    }
    
    
    public List<Integer> astarSearch(int start,int end) {
        AStarAdmissibleHeuristic<Integer> heuristic = 
                new AStarAdmissibleHeuristic<Integer>() {
            @Override
            public double getCostEstimate(Integer o, Integer v1) {
                return 5d;
            }
        };
        
        AStarShortestPath<Integer,DefaultWeightedEdge> astarShortestPath = 
           new AStarShortestPath<Integer,DefaultWeightedEdge>(defaultGraph, 
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

    public List<Integer> dijkstraSearch(int start,int end) {
        List<Integer> retVal = new ArrayList<Integer>();
        if( defaultGraph == null ) 
            return  retVal;
                
        try {
            List<Integer> list =  
                    DijkstraShortestPath.findPathBetween(defaultGraph,
                    start, end).getVertexList();            
            retVal = convertVerticesToEdges(list);                        
        }
        catch(Exception e) {
            ;
        }
        return retVal;        
    }    
    
    private List<Integer> convertVerticesToEdges(List<Integer> list) {
        List<Integer> retVal = new ArrayList<Integer>();
        
        for(int x=0;x<list.size()-1;x++ ) {
            LabeledWeightedEdge lw = 
                    (LabeledWeightedEdge) defaultGraph.getEdge(
                            list.get(x),list.get(x+1) );
            retVal.add(lw.getEdgeId());
        }   
        return retVal;
    }

}
