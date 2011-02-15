package Controllers;

import Nodes.CheckpointNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class GameController {
    private enum State { None, Ready, Set, Go, Racing, EndRace };
    float time = 0;

    float readyTime;
    float setTime;
    float goTime;
    float messageTime;
    float totalMessageTime;
    float raceTime;
    int checkpointCount;
    int lapCount;
    boolean messageFaded = false;

    HUDController hud;
    State state;
    InputManager inputManager;
    CarController car;
    Vector3f initialPosition;
    Quaternion initialRotation;
    String playerName;
    HighScoreTable highScoreTable;
    Menu menu;

    int currentCheckpoint = 0;
    int currentLap = 1;

    public GameController(float readyTime, float setTime, float goTime,
            float messageTime, float raceTime, int lapCount, HUDController hud,
            CarController car, InputManager inputManager, String playerName,
            HighScoreTable highScoreTable, Menu menu) {
        this.state = State.None;
        this.readyTime = readyTime;
        this.setTime = setTime;
        this.goTime = goTime;
        this.messageTime = 0f;
        this.totalMessageTime = messageTime;
        this.raceTime = raceTime;
        this.lapCount = lapCount;
        this.hud = hud;
        this.inputManager = inputManager;
        this.car = car;
        this.playerName = playerName;
        this.highScoreTable = highScoreTable;
        this.menu = menu;
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_RETURN));
    }

    public void setup(int checkpointCount, Vector3f initialPosition,
            Quaternion initialRotation) {
        this.checkpointCount = checkpointCount;
        this.initialPosition = initialPosition;
        this.initialRotation = initialRotation;
        startRace();
    }
    
    void startRace() {
        hud.setTimeLeft(raceTime);
        hud.setTotalLaps(lapCount);
        state = State.Ready;
        currentCheckpoint = 0;
        time = 0;
        car.setup(initialPosition, initialRotation);
        car.setBrakeLock(true);
        car.setSteerLock(false);
    }

    public void update(float tick) {
        this.time += tick;
        switch(state) {
            case Ready: Ready(tick); break;
            case Set: Set(tick); break;
            case Go: Go(tick); break;
            case Racing: Racing(tick); break;
            case EndRace: EndRace(tick); break;
        }
    }

    public void grabCheckpoint(CheckpointNode checkpoint) {
        if (currentCheckpoint != checkpoint.getIndex())
            return;

        raceTime += checkpoint.getExtraTime();
        hud.showBigMessage("TIME EXTENSION! +" + checkpoint.getExtraTime());
        messageTime = 0;
        messageFaded = false;

        if (++currentCheckpoint >= checkpointCount) {
            if (++currentLap <= lapCount) {
                hud.setLaps(currentLap);
                currentCheckpoint = 0;
            } else {
                state = State.EndRace;
                hud.showBigMessage("FINISHED!");
                try {
                    highScoreTable.addScore(playerName, (int) FastMath.floor(raceTime - time));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void Ready(float tick) {
        if (readyTime < time) {
            state = State.Set;
            hud.showBigMessage("READY");
            time = 0;
        }
    }

    private void Set(float tick) {
        if (setTime < time) {
            state = State.Go;
            hud.showBigMessage("SET");
            time = 0;
        }
    }

    private void Go(float tick) {
        if (goTime < time) {
            state = State.Racing;
            hud.showBigMessage("GO!");
            car.setBrakeLock(false);
            time = 0;
        }
    }

    private void Racing(float tick) {
        this.messageTime += tick;

        if (!messageFaded &&  messageTime > totalMessageTime) {
            messageFaded = true;
            messageTime = 0;
            hud.hideBigMessage();
        }

        if (raceTime < time) {
            state = State.EndRace;
            hud.showBigMessage("OUT OF TIME!");
            time = 0;
        } else {
            hud.setTimeLeft(raceTime - time);
        }
    }

    private void EndRace(float tick) {
        messageTime += tick;
        car.setSteerLock(true);
        car.setBrakeLock(true);

        if (messageTime > totalMessageTime)
            menu.backToMenu();
    }
}
