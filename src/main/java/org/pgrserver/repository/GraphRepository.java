/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：GraphRepository.java
 * 
 * @author mbasa
 * @since May 6, 2020
 */
package org.pgrserver.repository;

import java.util.List;

import org.pgrserver.entity.PgrServer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 説明：
 *
 */
public interface GraphRepository extends CrudRepository<PgrServer, Long> {

    @Query(value="select id,source,target,cost,reverse_cost,length from pgrserver", nativeQuery=true)
    List<PgrServer>getGraph();
}
