
import Nodes.BillboardNode;
import Nodes.Car;
import Nodes.GUINode;
import com.jme3.app.SimpleApplication;
import com.jme3.app.SimpleBulletApplication;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import de.lessvoid.nifty.Nifty;


import jme3tools.converters.ImageToAwt;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.nodes.PhysicsCharacterNode;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
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
import java.util.ArrayList;
import java.util.List;


public class T5Racing extends SimpleApplication implements ActionListener {
    private BulletAppState bulletAppState;
    private T5RCamera camera;

    private float steeringValue=0;
    private float accelerationValue=0;

    private Car car;
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


    public static void main(String[] args) {
        T5Racing app = new T5Racing();
        app.start();
    }

    private void setupKeys() {
         flyCam.setMoveSpeed(50);

        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("PrintScreen", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this,"Lefts");
        inputManager.addListener(this,"Rights");
        inputManager.addListener(this,"Ups");
        inputManager.addListener(this,"Downs");
        inputManager.addListener(this,"Space");
        inputManager.addListener(this,"Reset");
        inputManager.addListener(this,"PrintScreen");
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

        setupKeys();
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("src/T5RGUI.xml", "start");

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        Node cameraAnchor = new Node();
        buildTerrain();
        car = new Car(assetManager);
        rootNode.attachChild(car);
        bulletAppState.getPhysicsSpace().add(car);
        car.setLocalTranslation(new Vector3f(150,50,-20));

        car.attachChild(cameraAnchor);
        cameraAnchor.setLocalTranslation(new Vector3f(0, 2f, 8));

        //Skybox
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        //Camera
        cam.setFrustumFar(150f);
        camera = new T5RCamera(cam, car, cameraAnchor, 5);

        GUINode gui = new GUINode(assetManager, "Materials/GUI.j3m", new Vector2f(1, 10));
        gui.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(gui);

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
        inputManager.setCursorVisible(true);
        capturer = new ScreenCapturer(renderManager, renderer, rootNode, settings.getWidth(), settings.getHeight());
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
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("m_Tex1", grass);
        matRock.setFloat("m_Tex1Scale", 64f);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("m_Tex2", dirt);
        matRock.setFloat("m_Tex2Scale", 32f);

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
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

    public void setupFloor() {
        Material mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
        //mat.getTextureParam("m_DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        //mat.getTextureParam("m_NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        //mat.getTextureParam("m_ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);

        Box floor = new Box(Vector3f.ZERO, 400, 0f, 400);
        floor.scaleTextureCoordinates(new Vector2f(12.0f, 12.0f));
        Geometry floorGeom = new Geometry("Floor", floor);
        floorGeom.setShadowMode(ShadowMode.Receive);
        floorGeom.setMaterial(mat);

        PhysicsNode tb=new PhysicsNode(floorGeom,new MeshCollisionShape(floorGeom.getMesh()),0);
        tb.setLocalTranslation(new Vector3f(0f,-6,0f));
        tb.updateGeometricState();
        rootNode.attachChild(tb);
        bulletAppState.getPhysicsSpace().add(tb);
    }

    private Geometry findGeom(Spatial spatial, String name){
        if (spatial instanceof Node){
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++){
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null)
                    return result;
            }
        }else if (spatial instanceof Geometry){
            if (spatial.getName().startsWith(name))
                return (Geometry) spatial;
        }
        return null;
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
        } else if (binding.equals("PrintScreen")) {
            if (value) {
                capturer.capture(cam);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
        camera.update(tpf);
    }
}