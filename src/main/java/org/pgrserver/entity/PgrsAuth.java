/**
 * パッケージ名：org.pgrserver.entity
 * ファイル名  ：PgrsAuth.java
 * 
 * @author mbasa
 * @since May 7, 2020
 */
package org.pgrserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 説明：
 *
 */
@Entity
@Table(name = "pgrs_auth")
public class PgrsAuth {
    @Id
    private String authcode;
    /**
     * コンストラクタ
     *
     */
    public PgrsAuth() {
    }
    /**
     * @return authcode を取得する
     */
    public String getAuthcode() {
        return authcode;
    }
    /**
     * @param authcode authcode を設定する
     */
    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

}
