/**
 * パッケージ名：org.pgrserver.graph
 * ファイル名  ：LabeledWeightedEdge.java
 * 
 * @author mbasa
 * @since May 8, 2020
 */
package org.pgrserver.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * 説明：
 *
 */
public class LabeledWeightedEdge extends DefaultWeightedEdge {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int edgeId;
    private double length;
    
    /**
     * コンストラクタ
     *
     */
    public LabeledWeightedEdge() {
    }

    /**
     * @return edgeId を取得する
     */
    public int getEdgeId() {
        return edgeId;
    }

    /**
     * @param edgeId edgeId を設定する
     */
    public void setEdgeId(int edgeId) {
        this.edgeId = edgeId;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

}
