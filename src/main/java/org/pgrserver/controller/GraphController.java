/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：GraphController.java
 * 
 * @author mbasa
 * @since May 5, 2020
 */
package org.pgrserver.controller;

import java.util.List;

import org.pgrserver.entity.PgrServer;
import org.pgrserver.entity.PgrsAuth;
import org.pgrserver.graph.MainGraph;
import org.pgrserver.repository.AuthRepository;
import org.pgrserver.repository.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

/**
 * 説明：
 *
 */
@RestController
@RequestMapping("/api")
public class GraphController {

    /**
     * コンストラクタ
     *
     */
    public GraphController() {
    }
    
    @Autowired
    CustomRepository customRepo;
    
    @Autowired
    MainGraph mainGraph;
    
    @Autowired
    AuthRepository authRepository;
    
    private final String noRouteMsg = "{\"type\" : \"Feature\", "
            + "\"properties\" : {\"feat_length\" : 0}, "
            + "\"geometry\" : {}}";
    
    /**
     * 
     * Dijkstra with node parameters
     * (for dense networks)
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/dijkstra",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteDijkstra(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) { 
        
        List<Integer> retVal = mainGraph.dijkstraSearch(source, target);
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * Dijkstra with Latitude,Longitude parameters
     * (for dense networks)
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/dijkstra", 
            produces = MediaType.APPLICATION_JSON_VALUE )
    public String getRouteXYDijkstra(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {
        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }
        
        List<Integer> retVal = mainGraph.dijkstraSearch(source, target);
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * A-Star with node parameters
     * (for dense networks)
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/astar",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteAstar(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) {         
        List<Integer> retVal = mainGraph.astarSearch(source, target);
        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * A-Star with Latitude,Longitude parameters
     * (for dense networks)
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/astar",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteXYAstar(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {
        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }
        
        List<Integer> retVal = mainGraph.astarSearch(source, target);
        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * Bellman-Ford with node parameters
     * (for sparse network)
     * 
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/bellmanford",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteBellmanFord(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) 
    {        
        List<Integer> retVal = mainGraph.bellmanFordSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * Bellman-Ford with Latitude,Longitude parameters
     * (for sparse network) 
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/bellmanford",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteXYBellmanFord(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }
                
        List<Integer> retVal = mainGraph.bellmanFordSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * BFS with node parameters
     * (for sparse network)
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/bfs",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteBfs(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) 
    {        
        List<Integer> retVal = mainGraph.bfsSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * BFS with Latitude,Longitude parameters
     * (for sparse network)
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/bfs",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteXYBfs(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {
        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }
                
        List<Integer> retVal = mainGraph.bfsSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    /**
     * 
     * Johnson with node parameters
     * (for sparse network)
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/johnson",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteJohnson(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) 
    {        
        List<Integer> retVal = mainGraph.johnsonSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }

    /**
     * 
     * Johnson with Latitude,Longitude parameters
     * (for sparse network)
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/johnson",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteXYJohnson(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }
                
        List<Integer> retVal = mainGraph.johnsonSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }

    /**
     * 
     * Floyd-Wharshall with node parameters
     * (for sparse network)
     * 
     * @param source
     * @param target
     * @return GeoJson
     */
    @GetMapping(value="/node/floydWarshall",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteFloydWarshall(
            @RequestParam @ApiParam(required=true,value="Source Node ID") int source,
            @RequestParam @ApiParam(required=true,value="Target Node ID") int target) 
    {        
        List<Integer> retVal = mainGraph.floydWarshallSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }

    /**
     * 
     * Floyd-Warshall with Latitude,Longitude parameters
     * (for sparse network)
     * 
     * @param source_x
     * @param source_y
     * @param target_x
     * @param target_y
     * @return GeoJson
     */
    @GetMapping(value="/latlng/floydWarshall",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRouteXYFloydWarshall(
            @RequestParam @ApiParam(required=true,value="Source Longitude") double source_x,
            @RequestParam @ApiParam(required=true,value="Source Latitude" ) double source_y,
            @RequestParam @ApiParam(required=true,value="Target Longitude") double target_x,
            @RequestParam @ApiParam(required=true,value="Target Latitude" ) double target_y) 
    {
        
        int source = 0,target = 0;
        PgrServer pgrs;
        
        pgrs = customRepo.findNearestNode(source_x, source_y);
        if( pgrs != null ) {
            source = pgrs.getSource();
        }
        
        pgrs = customRepo.findNearestNode(target_x, target_y);
        if( pgrs != null ) {
            target = pgrs.getTarget();
        }        
           
        List<Integer> retVal = mainGraph.floydWarshallSearch(source, target);        
        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }

    /**
     * 
     * Reload the Graph
     * 
     * @param authcode
     * @return String
     */
    @PostMapping("/graphreload")
    String graphReload(@RequestParam(required=true) String authcode) {

        if( authcode != null && !authcode.isEmpty()) {
            List<PgrsAuth> p = authRepository.findByAuthcode(authcode);
            
            if( p != null && !p.isEmpty()) {
                mainGraph.createDirectedGraph();                
                return "graph has been reloded";
            }
        }
        return "graph  was not reloaded ";
    }
}
