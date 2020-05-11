/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：AuthRepository.java
 * 
 * @author mbasa
 * @since May 7, 2020
 */
package org.pgrserver.repository;

import java.util.List;

import org.pgrserver.entity.PgrsAuth;
import org.springframework.data.repository.CrudRepository;

/**
 * 説明：
 *
 */
public interface AuthRepository extends CrudRepository<PgrsAuth, Long> {    
    List<PgrsAuth> findByAuthcode(String authcode);
}
