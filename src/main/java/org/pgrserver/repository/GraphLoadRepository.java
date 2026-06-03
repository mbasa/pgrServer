/**
 * パッケージ名：org.pgrserver.repository
 * ファイル名  ：GraphLoadRepository.java
 *
 * @author mbasa
 * @since Jun 3, 2026
 */
package org.pgrserver.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

/**
 * 説明：
 *
 */
@Repository
public class GraphLoadRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void streamEdges(RowCallbackHandler handler) {
        jdbcTemplate.query(conn -> {
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "select id, source, target, cost, reverse_cost, length" +
                " from pgrserver",
                java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(1000);
            return ps;
        }, handler);
    }
}
