/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：VrpController.java
 * 
 * @author mbasa
 * @since Oct 25, 2020
 */
package org.pgrserver.controller;

import java.util.Map;

import org.pgrserver.bean.VrpParamBean;
import org.pgrserver.vrp.MainVrp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

/**
 * 説明：
 *
 */
@RestController
@RequestMapping("/vrp")
public class VrpController {

    /**
     * コンストラクタ
     *
     */
    public VrpController() {
    }

    @Autowired
    MainVrp mainVrp;


    @PostMapping(value="/generateRoute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generateRoute(
            @RequestBody @ApiParam(required=true,value="Generate a Trip Route")
                VrpParamBean vrpParamBean) { 
                        
        return mainVrp.createRoute(vrpParamBean);

    }
    
    /**
     * 
     * Generate a Trip Plan
     * 
     * 
     * @param vrpParamBean
     * @return JSON Object
     */
    @PostMapping(value="/generatePlan",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generatePlan(
            @RequestBody @ApiParam(required=true,value="Generate a Trip Plan")
                VrpParamBean vrpParamBean) { 

        Map<String,Object> retVal;
        
        try {
            retVal = mainVrp.createPlan(vrpParamBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("{\"status\":\"error with parameter data\"}",
                    HttpStatus.BAD_REQUEST);
        }
        
        return( retVal ); 
    }    

}
