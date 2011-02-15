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
    TextRenderer laps;
    TextRenderer score;
    TextRenderer speed;
    TextRenderer bigMessage;
    Element bigMessagePanel;
    int totalLaps;

    public HUDController(Nifty nifty) {
        this.nifty = nifty;
        screen = nifty.getScreen("HUD");

        timeLeft = screen.findElementByName("timeLeft")
                .getRenderer(TextRenderer.class);

        laps = screen.findElementByName("laps")
                .getRenderer(TextRenderer.class);

        speed = screen.findElementByName("speed")
                .getRenderer(TextRenderer.class);
        
        bigMessage = screen.findElementByName("bigMessage")
                .getRenderer(TextRenderer.class);
    
        bigMessagePanel = screen.findElementByName("bigMessagePanel");
        bigMessagePanel.setVisible(false);
    }

    public void show() {
        nifty.gotoScreen("HUD");

    }

    public void showBigMessage(String message) {
        bigMessage.setText(message);
        bigMessagePanel.setVisible(true);
        System.out.println(message);
    }

    public void hideBigMessage() {
        bigMessagePanel.setVisible(false);
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
        this.laps.setText("" + laps + "/" + totalLaps + " Laps");
    }

    public void setTotalLaps(int laps) {
        totalLaps = laps;
        this.laps.setText("1/" + totalLaps + " Laps");
    }

    public void setSpeed(int speedVal)
    {
        speed.setText("" + speedVal + " Km/h");
    }
}
