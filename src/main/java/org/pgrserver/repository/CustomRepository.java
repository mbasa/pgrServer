/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：CustomRepository.java
 * 
 * @author mbasa
 * @since May 10, 2020
 */
package org.pgrserver.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.pgrserver.entity.PgrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public PgrServer findNearestNode(double lng,double lat) {
        String sql = "select id,source,target,cost from pgrserver "
                + "order by geom <-> st_setsrid(st_point("
                + lng 
                + ","
                + lat 
                + "),4326) limit 1;";

        return (PgrServer) entityManager.createNativeQuery(
                sql,PgrServer.class).getSingleResult();
    }

    public Object getGraphBnd() {
        String sql = "select st_asgeojson(st_extent(geom)) from pgrserver ;";
        
        return entityManager.createNativeQuery( sql )
                .getSingleResult();
    }
    
    public Object createJsonDriveDistPoly(Set<Integer> list) {
        String listStr = list.toString();
        listStr = listStr.replace("[", "(");
        listStr = listStr.replace("]", ")");

        String sql = "select CAST(json_build_object('type','Feature',"
                + "'properties',json_build_object('feat_area',"
                + "st_area(t.geom,true)),"
                + "'geometry',CAST(st_asgeojson(t.geom) as json)"
                + ") as TEXT) as st_json "
                + "from (select st_concavehull(st_collect("
                + "st_startpoint(ST_GeometryN(geom,1))),0.8) as geom "
                + "from pgrserver "
                + "where source in " 
                + listStr
                + ") t;";

        return entityManager.createNativeQuery( sql )
                .getSingleResult();
    }

    public Object createJsonRouteResponse(List<Integer> list,int gid,
            Map<String,Object> additionalAtrib ) {

        String listStr = list.toString();
        listStr = listStr.replace("[", "(");
        listStr = listStr.replace("]", ")");

        String attrib = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(additionalAtrib);

            attrib = ","+ s.replace("{", "")
            .replace("}", "")
            .replace("\"", "'")
            .replace(":",",");
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String sql = "select CAST(json_build_object('type','Feature',"
                + "'id','"+ gid + "',"
                + "'properties',json_build_object('feat_length',"
                + "st_length(t.geom,true),"
                + "'fid',"+gid+attrib+"),"
                + "'geometry',CAST(st_asgeojson(t.geom) as json)"
                + ") as TEXT) as st_json "
                + "from (select st_union(geom) as geom from pgrserver"
                + " where id in " 
                + listStr
                + ") t;";

        return entityManager.createNativeQuery( sql )
                .getSingleResult();
    }

    public Object createJsonRouteResponse(List<Integer> list,int gid) {

        String listStr = list.toString();
        listStr = listStr.replace("[", "(");
        listStr = listStr.replace("]", ")");

        String sql = "select CAST(json_build_object('type','Feature',"
                + "'id','"+ gid + "',"
                + "'properties',json_build_object('feat_length',"
                + "st_length(t.geom,true),"
                + "'fid',"+gid+"),"
                + "'geometry',CAST(st_asgeojson(t.geom) as json)"
                + ") as TEXT) as st_json "
                + "from (select st_union(geom) as geom from pgrserver"
                + " where id in " 
                + listStr
                + ") t;";

        return entityManager.createNativeQuery( sql )
                .getSingleResult();
    }    

    public String createJsonCollectionResponse(List<List<Integer>> list) {

        StringBuffer retVal = new StringBuffer();
        retVal.append("{\"type\":\"FeatureCollection\",");
        retVal.append("\"features\":[");

        if( !list.isEmpty() ) {
            for(int i=0;i<list.size()-1;i++) {
                if( !list.get(i).isEmpty()) {
                    
                    if(retVal.length() > 0 )
                        retVal.append(",");
                    
                    retVal.append((String)createJsonRouteResponse(
                            list.get(i),i+1));                    
                }
            }
            if( !list.get(list.size()-1).isEmpty() ) {
                
                if(retVal.length() > 0 )
                    retVal.append(",");
                
                retVal.append((String)createJsonRouteResponse(list.get(
                        list.size()-1),list.size()));
            }
        }
        retVal.append("]}");

        return retVal.toString();        
    }
    
    public String createJsonCollectionResponse(List<List<Integer>> list,
            List<Map<String,Object>> additionalAttrib, boolean withHeader,
            int firstFid) {

        StringBuffer retVal = new StringBuffer();
        int fidCounter = 0;
        
        if( firstFid > 0 ) {
            fidCounter = firstFid;
        }
        
        if( withHeader ) {
            retVal.append("{\"type\":\"FeatureCollection\",");
            retVal.append("\"features\":[");
        }

        if( !list.isEmpty() ) {
            for(int i=0;i<list.size()-1;i++) {
                if( !list.get(i).isEmpty()) {
                    fidCounter++;
                    
                    if(retVal.length() > 0 )
                        retVal.append(",");
                        
                    retVal.append((String)createJsonRouteResponse(
                            list.get(i),fidCounter,additionalAttrib.get(i)));                    
                }
            }
            if( !list.get(list.size()-1).isEmpty() ) {
                fidCounter++;
                
                if(retVal.length() > 0 )
                    retVal.append(",");
                
                retVal.append((String)createJsonRouteResponse(list.get(
                    list.size()-1),fidCounter,
                    additionalAttrib.get(list.size()-1)));
            }
        }
        if( withHeader ) {
            retVal.append("]}");
        }
        
        return retVal.toString();        
    }
}
