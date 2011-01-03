
import Controllers.CarController;
import Controllers.GameController;
import Nodes.BillboardNode;
import Nodes.Car;
import Controllers.HUDController;
import de.lessvoid.nifty.Nifty;
import jme3tools.converters.ImageToAwt;
import com.jme3.app.SimpleApplication;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.nodes.PhysicsCharacterNode;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.jbullet.TerrainPhysicsShapeFactory;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Image;
import java.util.ArrayList;
import java.util.List;
import Helpers.OrganicTexture;
import Helpers.RoadTexture;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;



public class T5Racing extends SimpleApplication {
    private BulletAppState bulletAppState;
    private T5RCamera camera;

    private Car car;
    private CarController carController;

    private Nifty nifty;
    private ScreenCapturer capturer;

     TerrainQuad terrain;
    Node terrainPhysicsNode;
    Material matRock;
    Material matWire;
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

        if (settings.getRenderer().startsWith("LWJGL")){
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("src/T5RGUI.xml", "HUD");

        //Attach the nifty display to the GUI view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        Node cameraAnchor = new Node();
        buildTerrain();

        //Set up the car
        car = new Car(assetManager);
        rootNode.attachChild(car);
        bulletAppState.getPhysicsSpace().add(car);
        car.attachChild(cameraAnchor);
        cameraAnchor.setLocalTranslation(new Vector3f(0, 2f, 8));
        carController = new CarController(inputManager, car);

        //Skybox
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        //Camera
        cam.setFrustumFar(150f);
        camera = new T5RCamera(cam, car, cameraAnchor, 5);

        //Billboard trees
        Material mat = assetManager.loadMaterial("Materials/Billboard.j3m");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // show back side too
        mat.getAdditionalRenderState().setAlphaTest(true); // alpha on each face

        for (int i = 0; i < 80; i++) {
            BillboardNode billboard = new BillboardNode(cam, assetManager, mat, new Vector2f(3, 3));
            billboard.setLocalTranslation(-5 + 10 * (i % 2), 4f, -5 * (i / 2));
            rootNode.attachChild(billboard);
        }

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);

        
       /* inputManager.setCursorVisible(true);
        capturer = new ScreenCapturer(inputManager, renderManager, renderer,
                rootNode, settings.getWidth(), settings.getHeight(), cam);
        capturer.setup();*/
        

        HUDController hud = new HUDController(nifty);
        gameController = new GameController(5, 2, 2, 60, 10, 3,
                hud, new Vector3f(150,1,-20),
                Quaternion.IDENTITY, carController, inputManager);
        gameController.setup();
    }


    public void buildTerrain(){
     // First, we load up our textures and the heightmap texture for the terrain

        // TERRAIN TEXTURE material
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");

        // ALPHA map (for splat textures)

        matRock.setTexture("m_Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");



        // GRASS texture
        OrganicTexture grassTexture = new OrganicTexture();
        Texture grass = assetManager.loadTexture(grassTexture.texturePath());
      


        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("m_Tex1", grass);
        matRock.setFloat("m_Tex1Scale", 64f);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("m_Tex2", dirt);
        matRock.setFloat("m_Tex2Scale", 32f);

        // ROCK texture
        RoadTexture road = new RoadTexture();
        Texture rock = assetManager.loadTexture(road.texturePath());
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("m_Tex3", rock);
        matRock.setFloat("m_Tex3Scale", 128f);

        // WIREFRAME material
        matWire = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
        matWire.setColor("m_Color", ColorRGBA.Green);


        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(ImageToAwt.convert(heightMapImage.getImage(), false, true, 0), 0.25f);
            heightmap.load();

        } catch (Exception e) {
        }

        /*
         * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
         * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setShadowMode(ShadowMode.Receive);
        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(getCamera());
        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
        terrain.addControl(control);
        terrain.setMaterial(matRock);
       // terrain.setLocalScale(new Vector3f(2,2,2));
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        rootNode.attachChild(terrain);


        /**
         * Now we use the TerrainPhysicsShapeFactory to generate a heightfield
         * collision shape for us, and then add it to the physics node.
         */
        TerrainPhysicsShapeFactory factory = new TerrainPhysicsShapeFactory();
        terrainPhysicsNode = factory.createPhysicsMesh(terrain);
        rootNode.attachChild(terrainPhysicsNode);
        bulletAppState.getPhysicsSpace().addAll(terrainPhysicsNode);



        // Add 5 physics spheres to the world, with random sizes and positions
        // let them drop from the sky
        for (int i = 0; i < 5; i++) {
            float r = (float) (5 * Math.random());
            PhysicsNode physicsSphere = new PhysicsNode(new SphereCollisionShape(1 + r), 1);
            float x = (float) (120 * Math.random()) - 20;
            float y = (float) (120 * Math.random()) - 10;
            float z = (float) (120 * Math.random()) - 20;
            physicsSphere.setLocalTranslation(new Vector3f(x, 100 + y, z));
            physicsSphere.attachDebugShape(getAssetManager());
            rootNode.attachChild(physicsSphere);
            bulletAppState.getPhysicsSpace().add(physicsSphere);
        }

        PhysicsCharacterNode character = new PhysicsCharacterNode(new SphereCollisionShape(1), 0.1f);
        character.setLocalTranslation(new Vector3f(0, 100, 0));
        character.attachDebugShape(assetManager);
        rootNode.attachChild(character);
        bulletAppState.getPhysicsSpace().add(character);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, -0.5f, -0.1f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.50f, 0.40f, 0.50f, 1.0f));
        rootNode.addLight(dl);


        getCamera().getLocation().y = 25;
        getCamera().setDirection(new Vector3f(-1, 0, -1));
    }

    @Override
    public void simpleUpdate(float tpf) {
        camera.update(tpf);
        gameController.update(tpf);
        carController.update(tpf);
    }
}