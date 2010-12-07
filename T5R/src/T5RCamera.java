/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author ArkanRow
 */
public class T5RCamera {
    Camera cam;
    Node look;
    Node follow;
    float speed;
    Vector3f up;

    public T5RCamera(Camera cam, Node look, Node follow, float speed) {
        this.cam = cam;
        this.look = look;
        this.follow = follow;
        this.speed = speed;
        this.up = new Vector3f(0, 1, 0);
    }

    public void update(float time) {
        cam.lookAt(look.getWorldTranslation(), Vector3f.UNIT_Y);
        Vector3f cameraPos = cam.getLocation();
        Vector3f nodePos = follow.getWorldTranslation();
        cameraPos.interpolate(nodePos, time * speed);
        cam.setLocation(cameraPos);
    }
}
