/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：GraphRepository.java
 * 
 * @author mbasa
 * @since May 6, 2020
 */
package org.pgrserver.repository;

import java.util.stream.Stream;

import org.pgrserver.entity.PgrServer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.QueryHint;

/**
 * 説明：
 *
 */
public interface GraphRepository extends CrudRepository<PgrServer, Long> {

    @Query(value="select id,source,target,cost,reverse_cost,length from pgrserver", nativeQuery=true)
    @QueryHints(value = @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, value = "1000"))
    Stream<PgrServer> streamGraph();
}
