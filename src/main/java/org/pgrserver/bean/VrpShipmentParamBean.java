/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：VrpShipmentParamBean.java
 * 
 * @author mbasa
 * @since Dec 2, 2020
 */
package org.pgrserver.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 説明：
 *
 */
public class VrpShipmentParamBean {

    private List<VrpVehicleBean> vehicles  = new ArrayList<VrpVehicleBean>();
    private List<VrpShipmentBean> shipment = new ArrayList<VrpShipmentBean>();

    /**
     * コンストラクタ
     *
     */
    public VrpShipmentParamBean() {
    }

    /**
     * @return vehicles を取得する
     */
    public List<VrpVehicleBean> getVehicles() {
        return vehicles;
    }

    /**
     * @param vehicles vehicles を設定する
     */
    public void setVehicles(List<VrpVehicleBean> vehicles) {
        this.vehicles = vehicles;
    }

    /**
     * @return shipment を取得する
     */
    public List<VrpShipmentBean> getShipment() {
        return shipment;
    }

    /**
     * @param shipment shipment を設定する
     */
    public void setShipment(List<VrpShipmentBean> shipment) {
        this.shipment = shipment;
    }

}
