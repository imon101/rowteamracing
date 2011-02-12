
import Controllers.CarController;
import Controllers.GameController;
import Nodes.CarNode;
import Controllers.HUDController;
import Helpers.CheckpointCreator;
import Helpers.TreeCreator;
import de.lessvoid.nifty.Nifty;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import Nodes.TerrainNode;
import Nodes.TrackNode;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class T5Racing extends SimpleApplication {
    private BulletAppState bulletAppState;
    private T5RCamera camera;

    private CarNode car;
    private CarController carController;

    private TrackNode track;

    private Nifty nifty;
    private ScreenCapturer capturer;

     TerrainQuad terrain;
    Node terrainPhysicsNode;
    boolean wireframe = false;
    protected BitmapText hintText;
    PointLight pl;
    Geometry lightMdl;
    Geometry collisionMarker;
    GameController gameController;



    public static void main(String[] args) {
        T5Racing app = new T5Racing();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //Set up the renderer
        if (settings.getRenderer().startsWith("LWJGL")){
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }

        //Set up the car
        car = new CarNode(assetManager);
        rootNode.attachChild(car);
        bulletAppState.getPhysicsSpace().add(car);

        //Set up a node to mount the camera on
        Node cameraAnchor = new Node();
        car.attachChild(cameraAnchor);
        cameraAnchor.setLocalTranslation(new Vector3f(0, 3f, 8));

        //Set up a node to use as the camera target
        Node cameraTarget = new Node();
        car.attachChild(cameraTarget);
        cameraTarget.setLocalTranslation(new Vector3f(0, 1.5f, -16));

        //Set up Camera
        cam.setFrustumFar(150f);
        camera = new T5RCamera(cam, cameraTarget, cameraAnchor, 5);

        //Set up the track
        track = new TrackNode(assetManager);
        rootNode.attachChild(track);
        bulletAppState.getPhysicsSpace().add(track);

        //Set up the terrain
        rootNode.attachChild(new TerrainNode(assetManager, cam, false));

        //Skybox
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        //Trees
        TreeCreator.CreateTreesFromFile("assets/Text/Trees.txt", rootNode, cam, assetManager);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);

        //Screen capturer setup
        inputManager.setCursorVisible(true);
        capturer = new ScreenCapturer(inputManager, renderManager, renderer,
                rootNode, settings.getWidth(), settings.getHeight(), cam);
        capturer.setup();

        //Set up the GUI engine
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("src/T5RGUI.xml", "HUD");

        //Attach the nifty display to the GUI view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        //HUD setup
        HUDController hud = new HUDController(nifty);
        carController = new CarController(inputManager, car, hud);
        gameController = new GameController(5, 2, 2, 2, 30, 3, hud,
                carController, inputManager);

        //Checkpoints
        try {
            CheckpointCreator.createCheckpoints(
                    "assets/Text/Checkpoints.txt",
                    "assets/Text/TimeExtensions.txt",
                    car, gameController, rootNode,
                    bulletAppState.getPhysicsSpace(),
                    assetManager);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(T5Racing.class.getName()).log(Level.SEVERE, null, ex);
        }
        camera.init();
    }

    @Override
    public void simpleUpdate(float tpf) {
        camera.update(tpf);
        gameController.update(tpf);
        carController.update(tpf);
    }
}