package Nodes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class Checkpoint extends Node {
    int order;
    float extraTime;

    public Checkpoint(int order, float extraTime) {
        this.order = order;
        this.extraTime = extraTime;
    }

    public float getExtraTime() {
        return extraTime;
    }

    public float getOrder() {
        return order;
    }
}
