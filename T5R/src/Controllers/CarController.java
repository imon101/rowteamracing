/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controllers;

import Nodes.CarNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author ArkanRow
 */

public class CarController implements ActionListener {
    private CarNode car;
    private float steeringValue=0;
    final float steerSpeed = 1f;
    final float accelValue = 800;
    final float brakeValue = 40;

    private InputManager inputManager;
    private HUDController hud;
    private boolean brakeLock = true;
    private boolean steerLock = false;

    private boolean accel;
    private boolean steerLeft;
    private boolean steerRight;
    private boolean brake;

    private float brakeTime = 0;
    private float brakeTimeBeforeReverse = 1.5f;

    public CarController(InputManager inputManager, CarNode car,
            HUDController hud) {
        this.car = car;
        this.hud = hud;
        this.inputManager = inputManager;
        setupKeys();
    }

    private void setupKeys() {
        //flyCam.setMoveSpeed(50);
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this,"Lefts");
        inputManager.addListener(this,"Rights");
        inputManager.addListener(this,"Ups");
        inputManager.addListener(this,"Downs");
        inputManager.addListener(this,"Space");
        inputManager.addListener(this,"Reset");
    }
 
    public void update(float time) {
        if (steerLeft && !steerLock)
            steeringValue += time * steerSpeed;

        if (steerRight && !steerLock)
            steeringValue -= time * steerSpeed;

        if (!steerRight && !steerLeft)
            steeringValue += time * FastMath.sign(-steeringValue) * steerSpeed;

        steeringValue = FastMath.clamp(steeringValue, -1.0f, 1.0f);
        car.steer(steeringValue);

        car.accelerate(accel && !brakeLock? -accelValue : 0);

        if  ((brake && brakeTime < brakeTimeBeforeReverse) || brakeLock) {
            car.brake(brakeValue);
            brakeTime += time;
        } else if (brake && brakeTime >= brakeTimeBeforeReverse) {
            car.brake(0);
            car.accelerate(accelValue);
        } else {
            car.brake(0);
            brakeTime = 0;
        }

        hud.setSpeed((int)Math.floor(10 * car.getLinearVelocity().length()));
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            steerLeft = value;
        } else if (binding.equals("Rights")) {
            steerRight = value;
        } else if (binding.equals("Ups")) {
            accel = value;
        } else if (binding.equals("Downs")) {
            brake = value;
        }
    }

    public void setup(Vector3f initialPosition, Quaternion initialRotation) {
        car.setLocalTranslation(initialPosition);
        car.setLocalRotation(initialRotation);
        car.setLinearVelocity(Vector3f.ZERO);
        car.setAngularVelocity(Vector3f.ZERO);
        car.resetSuspension();
    }

    void setBrakeLock(boolean value) {
        brakeLock = value;
    }

    void setSteerLock(boolean value) {
        steerLock = value;
    }
}
