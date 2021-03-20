/**
 * パッケージ名：org.pgrserver.controller
 * ファイル名  ：UtilsController.java
 * 
 * @author mbasa
 * @since Mar 21, 2021
 */
package org.pgrserver.controller;

import org.pgrserver.repository.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 説明：
 *
 */
@RestController
@RequestMapping("/utils")
public class UtilsController {

    @Autowired
    CustomRepository customRepo;
    
    /**
     * コンストラクタ
     *
     */
    public UtilsController() {
    }

    @GetMapping(value="/graphBnd", 
            produces = MediaType.APPLICATION_JSON_VALUE )
    public String getGraphBnd() {
        
        
        return ((String)customRepo.getGraphBnd());
    }
}
