/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：VrpShipmentBean.java
 * 
 * @author mbasa
 * @since Dec 2, 2020
 */
package org.pgrserver.bean;

/**
 * 説明：
 *
 */
public class VrpShipmentBean {

    private int weightIndex     = 0;
    private int capacity        = 0;
    private LatLngBean pickup   = new LatLngBean();
    private LatLngBean delivery = new LatLngBean();
    
    /**
     * コンストラクタ
     *
     */
    public VrpShipmentBean() {
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
     * @return pickup を取得する
     */
    public LatLngBean getPickup() {
        return pickup;
    }

    /**
     * @param pickup pickup を設定する
     */
    public void setPickup(LatLngBean pickup) {
        this.pickup = pickup;
    }

    /**
     * @return delivery を取得する
     */
    public LatLngBean getDelivery() {
        return delivery;
    }

    /**
     * @param delivery delivery を設定する
     */
    public void setDelivery(LatLngBean delivery) {
        this.delivery = delivery;
    }

}
