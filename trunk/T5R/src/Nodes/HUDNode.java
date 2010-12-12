/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;

import com.jme3.scene.Node;

/**
 *
 * @author ArkanRow
 */
public class HUDNode extends Node {
    public int showTimeLeft = 0;

    public void showBigMessage(String message) {
        //TODO Implement
        System.out.println(message);
    }

    public void setTimeLeft(float timeLeft) {
        //TODO Implement
        if (showTimeLeft++ % 60 == 0)
            System.out.println("Time left: " + timeLeft);
    }

    public void setLaps(int laps) {
        //TODO Implement
        System.out.println("Lap: " + laps);
    }

    public void setTotalLaps(int laps) {
        //TODO Implement
        System.out.println("Total Laps: " + laps);
    }
}
