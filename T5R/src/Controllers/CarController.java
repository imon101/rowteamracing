/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controllers;

import Nodes.Car;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author ArkanRow
 */

public class CarController implements ActionListener {
    private Car car;
    private float steeringValue=0;
    private float accelerationValue=0;
    private InputManager inputManager;

    public CarController(InputManager inputManager, Car car) {
        this.car = car;
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

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if(value)
                steeringValue+=.5f;
            else
                steeringValue+=-.5f;
            car.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if(value)
                steeringValue+=-.5f;
            else
                steeringValue+=.5f;
            car.steer(steeringValue);
        }
        //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if(value)
                accelerationValue-=800;
            else
                accelerationValue+=800;
            car.accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if(value)
                car.brake(40f);
            else
                car.brake(0f);
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                car.setLocalTranslation(0, 0, 0);
                car.setLocalRotation(new Quaternion());
                car.setLinearVelocity(Vector3f.ZERO);
                car.setAngularVelocity(Vector3f.ZERO);
                car.resetSuspension();
            } else {
            }
        }
    }
}
