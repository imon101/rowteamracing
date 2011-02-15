
import Controllers.HighScoreTable;
import Controllers.CarController;
import Controllers.GameController;
import Nodes.CarNode;
import Controllers.HUDController;
import Controllers.Menu;
import Controllers.PlayerNameController;
import Helpers.CheckpointCreator;
import Helpers.TreeCreator;
import Nodes.TerrainNode;
import de.lessvoid.nifty.Nifty;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import Nodes.TrackNode;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.controls.button.controller.ButtonControl;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class T5Racing extends SimpleApplication implements ScreenController,
        Menu {
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
    PlayerNameController playerNameController;
    HighScoreTable highScoreTable;

    boolean raceStarted = false;

    Screen screen;
    boolean sound = true;

    Properties config;

    String sky1 = "Textures/Sky/Bright/BrightSky.dds";
    String sky2 = "Textures/Sky/Clean/CleanSky.png";
    String skyPath = sky1;
    boolean cloudy = true;

    public void bind(Nifty nifty, Screen screen) {
        this.screen = screen;
    }

    public void onStartScreen() {}
    public void onEndScreen() {}

    public void sound() {
        ButtonControl control = screen.findControl("sound", ButtonControl.class);
        sound = !sound;
        control.setText("Sound: " + (sound? "ON" : "OFF"));
    }

    public void sky() {
        ButtonControl control = screen.findControl("sky", ButtonControl.class);
        cloudy = !cloudy;
        control.setText("Sky: " + (cloudy? "Cloudy" : "Clean"));
        skyPath = cloudy? sky1 : sky2;
    }

    public void highScores() {
        nifty.gotoScreen("highscores");
    }

    public void credits() {
        nifty.gotoScreen("credits");
    }

    public void exit() {
        this.stop();
    }

    public static void main(String[] args) {
        T5Racing app = new T5Racing();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //Get the game configuration file
        config = new Properties();

        //try retrieve data from file
         try {
             config.load(new FileInputStream("Assets/Text/configuration.properties"));
         } catch(IOException e)
         {
             e.printStackTrace();
         }

        //Set up the renderer
        if (settings.getRenderer().startsWith("LWJGL")){
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            Vector3f direction = new Vector3f(
                    Float.parseFloat(config.getProperty("ShadowX")),
                    Float.parseFloat(config.getProperty("ShadowY")),
                    Float.parseFloat(config.getProperty("ShadowZ")));
            bsr.setDirection(direction.normalizeLocal());
            viewPort.addProcessor(bsr);
        }

        inputManager.setCursorVisible(true);

        highScoreTable = new HighScoreTable();

        //Set up the GUI engine
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        //Attach the nifty display to the GUI view port as a processor
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        setupGUI();
    }

    private void setupGUI() {
        nifty.fromXml("assets/GUI/T5RMenu.xml", "start", this, highScoreTable);

        TextRenderer nameRenderer = nifty
                .getScreen("start")
                .findElementByName("name")
                .getRenderer(TextRenderer.class);

        playerNameController = new PlayerNameController(nameRenderer, inputManager);
        playerNameController.listenKeys();
    }

    public void race() {
        playerNameController.unlistenKeys();

        //Set up the car
        car = new CarNode(
                Float.parseFloat(config.getProperty("carStiffness")),
                Float.parseFloat(config.getProperty("carCompValue")),
                Float.parseFloat(config.getProperty("carDampValue")),
                Float.parseFloat(config.getProperty("carMass")),
                assetManager);
        rootNode.attachChild(car);
        bulletAppState.getPhysicsSpace().add(car);

        //Set up a node to mount the camera on
        Node cameraAnchorThird = new Node();
        car.attachChild(cameraAnchorThird);
        cameraAnchorThird.setLocalTranslation(new Vector3f(
                    Float.parseFloat(config.getProperty("CameraAnchor3X")),
                    Float.parseFloat(config.getProperty("CameraAnchor3Y")),
                    Float.parseFloat(config.getProperty("CameraAnchor3Z"))));

        Node cameraAnchorFirst = new Node();
        car.attachChild(cameraAnchorFirst);
        cameraAnchorFirst.setLocalTranslation(new Vector3f(
                    Float.parseFloat(config.getProperty("CameraAnchor1X")),
                    Float.parseFloat(config.getProperty("CameraAnchor1Y")),
                    Float.parseFloat(config.getProperty("CameraAnchor1Z"))));

        //Set up a node to use as the camera target
        Node cameraTarget = new Node();
        car.attachChild(cameraTarget);
        cameraTarget.setLocalTranslation(new Vector3f(
                    Float.parseFloat(config.getProperty("CameraTargetX")),
                    Float.parseFloat(config.getProperty("CameraTargetY")),
                    Float.parseFloat(config.getProperty("CameraTargetZ"))));

        float followSpeed = Float.parseFloat(config.getProperty("CameraFollowSpeed"));

        //Set up Camera
        cam.setFrustumFar(150f);
        camera = new T5RCamera(cam, cameraTarget, cameraAnchorThird,
                cameraAnchorFirst, followSpeed, inputManager);

        //Set up ambient light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(
                    Float.parseFloat(config.getProperty("SunX")),
                    Float.parseFloat(config.getProperty("SunY")),
                    Float.parseFloat(config.getProperty("SunZ")))
                    .normalizeLocal());
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        rootNode.addLight(sun);

        //Set up the track
        track = new TrackNode(assetManager);
        rootNode.attachChild(track);
        bulletAppState.getPhysicsSpace().add(track);

        //Set up the terrain
        rootNode.attachChild(new TerrainNode(assetManager, cam,
                Boolean.parseBoolean(config.getProperty("regenerateTextures"))));

        //Skybox
        rootNode.attachChild(SkyFactory.createSky(assetManager, skyPath, !cloudy));

        //Trees
        TreeCreator.CreateTreesFromFile("assets/Text/Trees.txt", rootNode, cam, assetManager);

        //Screen capturer setup
        inputManager.setCursorVisible(true);
        capturer = new ScreenCapturer(inputManager, renderManager, renderer,
                rootNode, settings.getWidth(), settings.getHeight(), cam);
        capturer.setup();

        //HUD setup
        nifty.fromXml("assets/GUI/T5RHUD.xml", "HUD");
        HUDController hud = new HUDController(nifty);
        carController = new CarController(inputManager, car, hud);

        gameController = new GameController(
                Float.parseFloat(config.getProperty("readyTime")),
                Float.parseFloat(config.getProperty("setTime")),
                Float.parseFloat(config.getProperty("goTime")),
                Float.parseFloat(config.getProperty("messageTime")),
                Float.parseFloat(config.getProperty("raceTime")),
                Integer.parseInt(config.getProperty("lapCount")),
                hud,
                carController, inputManager, playerNameController.getName(),
                highScoreTable, this);

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
        raceStarted = true;
    }

    public void backToMenu() {
        raceStarted = false;
        rootNode.detachAllChildren();
        setupGUI();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (raceStarted == true) {
            camera.update(tpf);
            gameController.update(tpf);
            carController.update(tpf);
        } else {
            playerNameController.update(tpf);
        }
    }
}
