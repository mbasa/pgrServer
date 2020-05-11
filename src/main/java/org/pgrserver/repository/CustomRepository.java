/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：CustomRepository.java
 * 
 * @author mbasa
 * @since May 10, 2020
 */
package org.pgrserver.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 説明：
 *
 */
@Repository
public class CustomRepository {

    /**
     * コンストラクタ
     *
     */
    public CustomRepository() {
    }

    @Autowired
    private EntityManager entityManager;
    
    public Object createJsonRouteResponse(List<Integer> list) {
        
        String listStr = list.toString();
        listStr = listStr.replace("[", "(");
        listStr = listStr.replace("]", ")");
        
        String sql = "select CAST(json_build_object('type','Feature',"
                + "'properties',json_build_object('feat_length',"
                + "st_length(t.geom,true)),"
                + "'geometry',CAST(st_asgeojson(t.geom) as json)"
                + ") as TEXT) as st_json "
                + "from (select st_union(geom) as geom from pgrserver"
                + " where id in " 
                + listStr
                + ") t;";
        
        return entityManager.createNativeQuery( sql )
                .getSingleResult();
    }    
}
