
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class Screenshot {
    Camera offscreenCam;
    ViewPort offscreenView;
    FrameBuffer offscreenBuffer;
    RenderManager rm;

    public Screenshot(RenderManager rm, Node scene, int width, int height) {
        this.rm = rm;
        offscreenCam = new Camera(width, height);
                // create a pre-view. a view that is rendered before the main view
        offscreenView = rm.createPreView("Offscreen View", offscreenCam);
        offscreenView.setClearEnabled(true);
        offscreenView.setBackgroundColor(ColorRGBA.White);
        // create offscreen framebuffer
        offscreenBuffer = new FrameBuffer(width, height, 0);
        // setup framebuffer to use texture
        offscreenBuffer.setDepthBuffer(Format.Depth);

        // set viewport to render to offscreen framebuffer
        offscreenView.setOutputFrameBuffer(offscreenBuffer);

        // attach the scene to the viewport to be rendered
        offscreenView.attachScene(scene);
    }

    public void capture(Camera cam) {
        offscreenCam.setLocation(cam.getLocation());
        offscreenCam.setRotation(cam.getRotation());
        offscreenCam.setFrustum(cam.getFrustumNear(), cam.getFrustumFar(),
                cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(),
                cam.getFrustumBottom());

        rm.renderViewPortRaw(offscreenView);

    }
}
