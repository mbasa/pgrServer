/**
 * パッケージ名：org.pgrserver.bean
 * ファイル名  ：LatLngBean.java
 * 
 * @author mbasa
 * @since Nov 25, 2020
 */
package org.pgrserver.bean;

/**
 * 説明：
 *
 */
public class LatLngBean {

    double lng;
    double lat;
    
    /**
     * コンストラクタ
     *
     */
    public LatLngBean() {
    }

    /**
     * @return lat を取得する
     */
    public double getLat() {
        return lat;
    }
    /**
     * @param lat lat を設定する
     */
    public void setLat(double lat) {
        this.lat = lat;
    }
    /**
     * @return lng を取得する
     */
    public double getLng() {
        return lng;
    }
    /**
     * @param lng lng を設定する
     */
    public void setLng(double lng) {
        this.lng = lng;
    }  
}
