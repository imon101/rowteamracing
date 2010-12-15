/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controllers;

import com.jme3.math.FastMath;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.tools.Color;
/**
 *
 * @author ArkanRow
 */
public class HUDController {
    public int showTimeLeft = 0;
    Nifty nifty;
    Screen screen;
    TextRenderer timeLeft;

    public HUDController(Nifty nifty) {
        this.nifty = nifty;
        screen = nifty.getScreen("HUD");
        Element timeLeftElement = screen.findElementByName("timeLeft");
        timeLeft = timeLeftElement.getRenderer(TextRenderer.class);
    }

    public void show() {
        nifty.gotoScreen("HUD");
    }

    public void showBigMessage(String message) {
        //TODO Implement
        System.out.println(message);
    }

    public void setTimeLeft(float value) {
        if (timeLeft==null) {
            return;}
        timeLeft.setText((value < 10? "0" : "") + (int)FastMath.floor(value));

        if (value <= 10)
            timeLeft.setColor(new Color(0.7f, 0.0f, 0.0f, 1.0f));
        else
            timeLeft.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
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
