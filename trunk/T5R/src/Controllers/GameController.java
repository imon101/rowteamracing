package Controllers;

import Nodes.Checkpoint;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class GameController implements ActionListener {
    private enum State { None, Ready, Set, Go, Racing, EndRace };
    float time = 0;

    float readyTime;
    float setTime;
    float goTime;
    float raceTime;
    int checkpointCount;
    int lapCount;
    HUDController hud;
    State state;
    InputManager inputManager;
    CarController car;
    Vector3f initialPosition;
    Quaternion initialRotation;

    int currentCheckpoint = 0;
    int currentLap = 1;

    public GameController(float readyTime, float setTime, float goTime,
            float raceTime, int checkpointCount, int lapCount, HUDController hud,
            Vector3f initialPosition, Quaternion initialRotation, CarController car,
            InputManager inputManager) {
        this.state = State.None;
        this.readyTime = readyTime;
        this.setTime = setTime;
        this.goTime = goTime;
        this.raceTime = raceTime;
        this.checkpointCount = checkpointCount;
        this.lapCount = lapCount;
        this.hud = hud;
        this.inputManager = inputManager;
        this.car = car;
        this.initialPosition = initialPosition;
        this.initialRotation = initialRotation;
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_RETURN));
    }

    public void setup() {
        inputManager.addListener(this,"Restart");
        startRace();
    }
    
    void startRace() {
        hud.setTimeLeft(raceTime);
        state = State.Ready;
        time = 0;
        car.setup(initialPosition, initialRotation);
        car.setBrakeLock(true);
        car.setSteerLock(false);
    }

    public void update(float time) {
        this.time += time;
        switch(state) {
            case Ready: Ready(); break;
            case Set: Set(); break;
            case Go: Go(); break;
            case Racing: Racing(); break;
            case EndRace: EndRace(); break;
        }
    }

    public void GrabCheckpoint(Checkpoint checkpoint) {
        if (currentCheckpoint != checkpoint.getOrder())
            return;

        raceTime += checkpoint.getExtraTime();
        hud.showBigMessage("TIME EXTENSION!");

        if (++currentCheckpoint >= checkpointCount) {
            if (++currentLap <= lapCount) {
                hud.setLaps(currentLap);
            } else {
                state = State.EndRace;
                hud.showBigMessage("FINISHED!");
            }
        }
    }

    private void Ready() {
        if (readyTime < time) {
            state = State.Set;
            hud.showBigMessage("READY");
            time = 0;
        }
    }

    private void Set() {
        if (setTime < time) {
            state = State.Go;
            hud.showBigMessage("SET");
            time = 0;
        }
    }

    private void Go() {
        if (goTime < time) {
            state = State.Racing;
            hud.showBigMessage("GO!");
            car.setBrakeLock(false);
            time = 0;
        }
    }

    private void Racing() {
        if (raceTime < time) {
            state = State.EndRace;
            hud.showBigMessage("OUT OF TIME!");
            time = 0;
        } else {
            hud.setTimeLeft(raceTime - time);
        }
    }

    private void EndRace() {
        car.setSteerLock(true);
        car.setBrakeLock(true);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Restart")) {
            if(value && state == State.EndRace) {
                startRace();
            }
        }
    }
}
