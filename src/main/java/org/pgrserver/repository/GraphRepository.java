/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：GraphRepository.java
 * 
 * @author mbasa
 * @since May 6, 2020
 */
package org.pgrserver.repository;

import org.pgrserver.entity.PgrServer;
import org.springframework.data.repository.CrudRepository;

/**
 * 説明：
 *
 */
public interface GraphRepository extends CrudRepository<PgrServer, Long> {
}
