/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author ArkanRow
 */
public class CreditsController implements ScreenController{
    Nifty nifty;
    Screen screen;

    public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void back() {
        System.out.println("tits");
        nifty.gotoScreen("start");
    }
}
