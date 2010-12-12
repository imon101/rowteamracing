
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class ScreenCapturer implements SceneProcessor, ActionListener {
    private int width, height;
    private FrameBuffer offBuffer;
    private ViewPort offView;
    private Camera offCamera;
    private Renderer renderer;

    private ByteBuffer cpuBuf;
    private byte[] cpuArray;
    private final BufferedImage image;
    private boolean capture = false;
    InputManager inputManager;
    private Camera targetCam;

    public ScreenCapturer(InputManager im, RenderManager rm, Renderer renderer, Node scene,
            int width, int height, Camera targetCam) {
        this.width = width;
        this.height = height;
        this.renderer = renderer;
        this.inputManager = im;
        this.targetCam = targetCam;

        cpuBuf = BufferUtils.createByteBuffer(width * height * 4);
        cpuArray = new byte[width * height * 4];
        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        offCamera = new Camera(width, height);

        // create a pre-view. a view that is rendered before the main view
        offView = rm.createPreView("Offscreen View", offCamera);
        offView.setBackgroundColor(ColorRGBA.Black);
        offView.setClearEnabled(true);

        // this will let us know when the scene has been rendered to the
        // frame buffer
        offView.addProcessor(this);

        // create offscreen framebuffer
        offBuffer = new FrameBuffer(width, height, 0);

        //setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        //setup framebuffer to use renderbuffer
        // this is faster for gpu -> cpu copies
        offBuffer.setDepthBuffer(Format.Depth);
        offBuffer.setColorBuffer(Format.RGBA8);

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // attach the scene to the viewport to be rendered
        offView.attachScene(scene);

        //Setup the print screen key and event
        inputManager = im;
        im.addMapping("PrintScreen", new KeyTrigger(KeyInput.KEY_P));
        im.addListener(this, "PrintScreen");
    }

    public void capture(Camera cam) {
        offCamera.setLocation(cam.getLocation());
        offCamera.setRotation(cam.getRotation());
        offCamera.setFrustum(cam.getFrustumNear(), cam.getFrustumFar(),
                cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(),
                cam.getFrustumBottom());

        capture = true;
    }

    public void initialize(RenderManager rm, ViewPort vp) {}

    public void reshape(ViewPort vp, int w, int h) {}

    public boolean isInitialized() {
        return true;
    }

    public void preFrame(float tpf) {}

    public void postQueue(RenderQueue rq) {}

    public void postFrame(FrameBuffer out) {
        if (!capture)
            return;
        
        capture = false;
        cpuBuf.clear();
        renderer.readFrameBuffer(offBuffer, cpuBuf);

        // copy native memory to java memory
        cpuBuf.clear();
        cpuBuf.get(cpuArray);
        cpuBuf.clear();

        for (int i = 0; i < width * height * 4; i+=4){
            byte b = cpuArray[i+0];
            byte g = cpuArray[i+1];
            byte r = cpuArray[i+2];
            byte a = cpuArray[i+3];

            cpuArray[i+0] = a;
            cpuArray[i+1] = b;
            cpuArray[i+2] = g;
            cpuArray[i+3] = r;
        }

        for (int i=0; i < height / 2; i++) {
            for (int j=0; j < width * 4; j++) {
                byte aux = cpuArray[i * width * 4 + j];
                cpuArray[i * width * 4 + j] = cpuArray[(height -1 - i) * width * 4 + j];
                cpuArray[(height -1 - i) * width * 4 + j] = aux;
            }
        }

        synchronized (image) {
            WritableRaster wr = image.getRaster();
            DataBufferByte db = (DataBufferByte) wr.getDataBuffer();
            System.arraycopy(cpuArray, 0, db.getData(), 0, cpuArray.length);
            File file = new File("capture.png");
            try {
                ImageIO.write(image, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(ScreenCapturer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cleanup() {}

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("PrintScreen")) {
            if (value) {
                capture(targetCam);
            }
        }
    }
}
