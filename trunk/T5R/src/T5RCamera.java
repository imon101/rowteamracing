/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author ArkanRow
 */
public class T5RCamera implements ActionListener {
    Camera cam;
    Node look;
    Node follow1;
    Node follow2;
    Node follow;
    float speed;

    Vector3f vec = new Vector3f(0, 200, 0);

    public T5RCamera(Camera cam, Node look, Node follow1, Node follow2, float speed,
            InputManager inputManager) {
        this.cam = cam;
        this.look = look;
        this.follow1 = follow1;
        this.follow2 = follow2;
        this.follow = follow1;
        this.speed = speed;

        cam.setFrustumFar(1000);

        inputManager.addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Enter");
    }

    public void init() {
        cam.lookAt(look.getWorldTranslation(), vec);
        cam.setLocation(follow.getWorldTranslation());
    }

    public void update(float time) {
        cam.lookAt(look.getWorldTranslation(), Vector3f.UNIT_Y);
        Vector3f cameraPos = cam.getLocation();
        Vector3f nodePos = follow.getWorldTranslation();
        if (follow == follow1)
            cameraPos.interpolate(nodePos, time * speed);
        else
            cameraPos = nodePos;
        cam.setLocation(cameraPos);

        //cam.setLocation(vec);
        //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Enter") && isPressed) {
            if (follow == follow1)
                follow = follow2;
            else
                follow = follow1;

        }
    }
}
