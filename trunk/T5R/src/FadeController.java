
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class FadeController implements ScreenController{
    Nifty nifty;
    Screen screen;

    public void clickThing() {
        System.out.println("quitting");
//        nifty.fromXml("src/hellojme.xml", "other");
    }

    public FadeController() {
        System.out.println("Nifty is instantiating me");
    }

    public void bind(Nifty nifty, Screen screen) {
        System.out.println("binding");
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
        System.out.println("ahrreloco");
    }

    public void onEndScreen() {
    }
}
