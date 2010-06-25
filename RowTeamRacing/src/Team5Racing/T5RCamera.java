/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Team5Racing;

import com.jme3.app.SimpleBulletApplication;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Arkan Row
 */
public class T5RCamera extends Node {
    private RenderManager rm;
    private Node scene;
    private Node gui;
    public ViewPort vp;
    public ViewPort gvp;
    private Camera cam;




    public T5RCamera(String name, int width, int height, float fov, RenderManager rm, Node scene, Node gui) {
        super(name);
        this.rm = rm;
        this.scene = scene;
        this.gui = gui;

        cam  = new Camera(width, height);
        setFov(fov);
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        vp = rm.createMainView("Default", cam);
        gvp  = rm.createPostView("Gui Default", cam);
    }

    public void deactivate() {
        vp.clearScenes();
        gvp.clearScenes();
    }

    public void activate() {
        gvp.setClearEnabled (false);
        gvp.attachScene(gui);
        vp.attachScene(scene);
    }
    
    public void setFov(float fov) {
        cam.setFrustumPerspective (fov, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
    }

    public Camera getJMonkeyCamera() {
        return cam;
    }

    public ViewPort getJMonkeySceneViewPort() {
        return vp;
    }

    public ViewPort getJMonkeyGUIViewPort() {
        return gvp;
    }
}
