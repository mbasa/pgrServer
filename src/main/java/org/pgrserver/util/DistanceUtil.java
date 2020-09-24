/**
 * パッケージ名：org.pgrserver.util
 * ファイル名  ：DistanceUtil.java
 * 
 * @author mbasa
 * @since Sep 24, 2020
 */
package org.pgrserver.util;

/**
 * 説明：
 *
 */
public class DistanceUtil {

    /**
     * コンストラクタ
     *
     */
    public DistanceUtil() {
    }

    public static double euclidean(double lat1, double lng1, 
            double lat2, double lng2) {
      //double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double earthRadius = 6371.0;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;

    }
}
