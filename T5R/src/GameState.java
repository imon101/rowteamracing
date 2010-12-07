/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class GameState {
    private enum State { ReadySetGo, Racing, Lost, Win };
    float time = 0;
    State state;

    public GameState() {
        state = State.ReadySetGo;
    }


    void update(float time) {
    }
}
