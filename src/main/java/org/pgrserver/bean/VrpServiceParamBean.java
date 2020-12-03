/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：VrpServiceParamBean.java
 * 
 * @author mbasa
 * @since Nov 24, 2020
 */
package org.pgrserver.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 説明： Vrp Input Parameters
 *
 */
public class VrpServiceParamBean {

    private List<VrpVehicleBean> vehicles = new ArrayList<VrpVehicleBean>();
    private List<VrpServiceBean> services = new ArrayList<VrpServiceBean>();
    /**
     * コンストラクタ
     *
     */
    public VrpServiceParamBean() {
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
     * @return services を取得する
     */
    public List<VrpServiceBean> getServices() {
        return services;
    }
    /**
     * @param services services を設定する
     */
    public void setServices(List<VrpServiceBean> services) {
        this.services = services;
    }

}

