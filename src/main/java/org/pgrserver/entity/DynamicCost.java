/**
 * パッケージ名：org.pgrserver.entity
 * ファイル名  ：DynamicCost.java
 * 
 * @author mbasa
 * @since Jul 11, 2022
 */
package org.pgrserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 説明：
 *
 */
@Entity
public class DynamicCost {

    @Id
    private int id;
    private double cost;
    
    /**
     * コンストラクタ
     *
     */
    public DynamicCost() {
    }

    /**
     * @return id を取得する
     */
    public int getId() {
        return id;
    }

    /**
     * @param id id を設定する
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return cost を取得する
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost cost を設定する
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

}
