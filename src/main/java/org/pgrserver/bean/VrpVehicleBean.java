/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：VrpVehicleBean.java
 * 
 * @author mbasa
 * @since Nov 25, 2020
 */
package org.pgrserver.bean;

/**
 * 
 * 説明： VrpVehicle Bean for the VrpParameter
 *
 */
public class VrpVehicleBean {

    private int weightIndex = 0;
    private int capacity = 0;
    private LatLngBean startLocation = new LatLngBean();

    /**
     * コンストラクタ
     *
     */
    public VrpVehicleBean() {
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
     * @return startLocation を取得する
     */
    public LatLngBean getStartLocation() {
        return startLocation;
    }
    /**
     * @param startLocation startLocation を設定する
     */
    public void setStartLocation(LatLngBean startLocation) {
        this.startLocation = startLocation;
    }  
}
