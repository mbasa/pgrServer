/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：VrpServiceBean.java
 * 
 * @author mbasa
 * @since Nov 25, 2020
 */
package org.pgrserver.bean;

/**
 * 説明：Service Bean for the VrpPrameter
 *
 */
public class VrpServiceBean {

    private int weightIndex     = 0;
    private int capacity        = 0;
    private LatLngBean location = new LatLngBean();

    /**
     * コンストラクタ
     *
     */
    public VrpServiceBean() {
    }

    /**
     * @return weightIndex を取得する
     */
    public int getWeightIndex() {
        return weightIndex;
    }
    /**
     * @param weightIndex weightIndex を設定する
     */
    public void setWeightIndex(int weightIndex) {
        this.weightIndex = weightIndex;
    }
    /**
     * @return capacity を取得する
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * @param capacity capacity を設定する
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    /**
     * @return location を取得する
     */
    public LatLngBean getLocation() {
        return location;
    }
    /**
     * @param location location を設定する
     */
    public void setLocation(LatLngBean location) {
        this.location = location;
    }

}
