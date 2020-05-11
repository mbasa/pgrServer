/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：GraphController.java
 * 
 * @author mbasa
 * @since May 5, 2020
 */
package org.pgrserver.controller;

import java.util.List;

import org.pgrserver.entity.PgrsAuth;
import org.pgrserver.graph.MainGraph;
import org.pgrserver.repository.AuthRepository;
import org.pgrserver.repository.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    
    
    @GetMapping("/dijkstra")
    public String getRouteDijkstra(@RequestParam int source,int target) {        
        List<Integer> retVal = mainGraph.dijkstraSearch(source, target);
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    @GetMapping("/astar")
    public String getRouteAstar(@RequestParam int source,int target) {         
        List<Integer> retVal = mainGraph.astarSearch(source, target);
        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
    @GetMapping("/bellmanford")
    public String getRouteBellmanFord(@RequestParam int source,
            int target) {        
        List<Integer> retVal = mainGraph.bellmanFordSearch(source, target);        
        if( retVal == null || retVal.isEmpty() ) {
            return this.noRouteMsg;
        }
        return (String)customRepo.createJsonRouteResponse(retVal);
    }
    
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
