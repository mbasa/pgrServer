/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：VrpController.java
 * 
 * @author mbasa
 * @since Oct 25, 2020
 */
package org.pgrserver.controller;

import java.util.Map;

import org.pgrserver.bean.VrpServiceParamBean;
import org.pgrserver.bean.VrpShipmentParamBean;
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

    /**
     * 
     * Generate a Service Trip Route
     * 
     * @param vrpServiceParamBean
     * @return
     */
    @PostMapping(value = "/generateServiceRoute", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generateServiceRoute(
            @RequestBody @ApiParam(required = true, 
            value = "Generate a Service Trip Route") 
            VrpServiceParamBean vrpServiceParamBean) {

        return mainVrp.createServiceRoute(vrpServiceParamBean);

    }

    /**
     * 
     * Generate a Service Trip Plan
     * 
     * 
     * @param vrpServiceParamBean
     * @return JSON Object
     */
    @PostMapping(value = "/generateServicePlan", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generateServicePlan(
            @RequestBody @ApiParam(required = true, 
            value = "Generate a Service Trip Plan") 
            VrpServiceParamBean vrpServiceParamBean) {

        Map<String, Object> retVal;

        try {
            retVal = mainVrp.createServicePlan(vrpServiceParamBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(
                    "{\"status\":\"error with parameter data\"}",
                    HttpStatus.BAD_REQUEST);
        }

        return (retVal);
    }

    @PostMapping(value = "/generateShipmentRoute", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generateShipmentRoute(
            @RequestBody @ApiParam(required = true, 
            value = "Generate a Shipment Trip Route") 
            VrpShipmentParamBean vrpShipmentParamBean) {

        return mainVrp.createShipmentRoute(vrpShipmentParamBean);

    }
    
    /**
     * 
     * Generate a Shipment Plan
     * 
     * @param vrpShipmentParamBean
     * @return JSON Object
     */
    @PostMapping(value = "/generateShipmentPlan", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object generateShipmentPlan(
            @RequestBody @ApiParam(required = true, 
            value = "Generate a Service Trip Plan") 
            VrpShipmentParamBean vrpShipmentParamBean) {

        Map<String, Object> retVal;

        try {
            retVal = mainVrp.createShipmentPlan(vrpShipmentParamBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(
                    "{\"status\":\"error with parameter data\"}",
                    HttpStatus.BAD_REQUEST);
        }

        return (retVal);
    }
}
