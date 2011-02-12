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

    Vector3f vec = new Vector3f(0, 200, 0);

    public T5RCamera(Camera cam, Node look, Node follow, float speed) {
        this.cam = cam;
        this.look = look;
        this.follow = follow;
        this.speed = speed;

        cam.setFrustumFar(1000);
    }

    public void init() {
        cam.lookAt(look.getWorldTranslation(), vec);
        cam.setLocation(follow.getWorldTranslation());
    }

    public void update(float time) {
        cam.lookAt(look.getWorldTranslation(), Vector3f.UNIT_Y);
        Vector3f cameraPos = cam.getLocation();
        Vector3f nodePos = follow.getWorldTranslation();
        cameraPos.interpolate(nodePos, time * speed);
        cam.setLocation(cameraPos);

        //cam.setLocation(vec);
        //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
}
