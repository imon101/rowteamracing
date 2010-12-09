
import Nodes.BillboardNode;
import Nodes.GUINode;
import com.jme3.app.SimpleBulletApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.bullet.nodes.PhysicsVehicleNode;
import com.jme3.bullet.nodes.PhysicsVehicleWheel;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.texture.FrameBuffer;
import com.sun.java.swing.plaf.motif.MotifBorders.FrameBorder;
import de.lessvoid.nifty.Nifty;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.List;


import jme3tools.converters.ImageToAwt;
import com.jme3.app.SimpleBulletApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.nodes.PhysicsCharacterNode;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.bullet.nodes.PhysicsVehicleNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.jbullet.TerrainPhysicsShapeFactory;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;


public class T5Racing extends SimpleBulletApplication implements ActionListener {
    private T5RCamera camera;

    private PhysicsVehicleNode player;
    private PhysicsVehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue=0;
    private float accelerationValue=0;
    private Nifty nifty;

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

/*
    private void setupKeys2() {
        flyCam.setMoveSpeed(50);
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "wireframe");
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(actionListener, "Lefts");
        inputManager.addListener(actionListener, "Rights");
        inputManager.addListener(actionListener, "Ups");
        inputManager.addListener(actionListener, "Downs");
        inputManager.addListener(actionListener, "Space");
        inputManager.addListener(actionListener, "Reset");
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
    }*/


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
        buildPlayer();
        //setupFloor();

        player.attachChild(cameraAnchor);
        cameraAnchor.setLocalTranslation(new Vector3f(0, 2f, 8));

        //Skybox
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        //Camera
        cam.setFrustumFar(150f);
        camera = new T5RCamera(cam, player, cameraAnchor, 5);

     /*   //Billboard trees
        GUINode gui = new GUINode(assetManager, "Materials/GUI.j3m", new Vector2f(1, 10));
        gui.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(gui);

        for (int i = 0; i < 8; i++) {
            BillboardNode billboard = new BillboardNode(cam, assetManager, "Materials/Billboard.j3m", new Vector2f(3, 3));
            billboard.setLocalTranslation(-5 + 10 * (i % 2), -2f, -5 * (i / 2));
            rootNode.attachChild(billboard);
        }*/

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
        inputManager.setCursorVisible(true);
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
            e.printStackTrace();
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
        getPhysicsSpace().addAll(terrainPhysicsNode);



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
            getPhysicsSpace().add(physicsSphere);
        }

        PhysicsCharacterNode character = new PhysicsCharacterNode(new SphereCollisionShape(1), 0.1f);
        character.setLocalTranslation(new Vector3f(0, 100, 0));
        character.attachDebugShape(assetManager);
        rootNode.attachChild(character);
        getPhysicsSpace().add(character);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(1, -0.5f, -0.1f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.50f, 0.40f, 0.50f, 1.0f));
        rootNode.addLight(dl);


        getCamera().getLocation().y = 25;
        getCamera().setDirection(new Vector3f(-1, 0, -1));
    }



    private void buildPlayer() {
        float stiffness=120.0f;//200=f1 car
        float compValue=0.2f; //(lower than damp!)
        float dampValue=0.3f;
        final float mass = 400;//400

        Spatial car = assetManager.loadModel("Models/Ferrari/Car.scene");
        Node carNode = (Node) car;
        final Geometry chasis = findGeom(carNode, "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();

        final Vector3f extent = box.getExtent(null);

        // put chasis in center, so that physics box matches up with it
        // also remove from parent to avoid transform issues
        chasis.removeFromParent();
//        chasis.setLocalTranslation(Vector3f.UNIT_Y);
        chasis.setShadowMode(ShadowMode.Cast);

        CompoundCollisionShape compoundShape=new CompoundCollisionShape();
        compoundShape.addChildShape(new BoxCollisionShape(extent), Vector3f.UNIT_Y);

        player = new PhysicsVehicleNode(chasis, compoundShape, mass);

        //setting default values for wheels
        player.setSuspensionCompression(compValue*2.0f*FastMath.sqrt(stiffness));
        player.setSuspensionDamping(dampValue*2.0f*FastMath.sqrt(stiffness));
        player.setSuspensionStiffness(stiffness);
        player.setMaxSuspensionForce(10000);


        //renderer.getF
        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0,-1,0);
        Vector3f wheelAxle = new Vector3f(-1,0,0);

        Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
        wheel_fr.removeFromParent();
        wheel_fr.center();
        node_fr = new Node("wheel_node");
        node_fr.setShadowMode(ShadowMode.Cast);
        node_fr.attachChild(wheel_fr);
        Node primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_fr);
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f)-1f;
        float front_wheel_h = (wheelRadius * 1.9f)-1f;
        player.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);


        Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
        wheel_fl.removeFromParent();
        wheel_fl.center();
        node_fl = new Node("wheel_node");
        node_fl.setShadowMode(ShadowMode.Cast);
        node_fl.attachChild(wheel_fl);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_fl);
        box = (BoundingBox) wheel_fl.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(carNode, "WheelBackRight");
        wheel_br.removeFromParent();
        wheel_br.center();
        node_br = new Node("wheel_node");
        node_br.setShadowMode(ShadowMode.Cast);
        node_br.attachChild(wheel_br);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_br);
        box = (BoundingBox) wheel_br.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
        wheel_bl.removeFromParent();
        wheel_bl.center();
        node_bl = new Node("wheel_node");
        node_bl.setShadowMode(ShadowMode.Cast);
        node_bl.attachChild(wheel_bl);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_bl);
        box = (BoundingBox) wheel_bl.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

//        player.attachDebugShape(assetManager);
        player.getWheel(2).setFrictionSlip(4);
        player.getWheel(3).setFrictionSlip(4);
        rootNode.attachChild(player);
        getPhysicsSpace().add(player);
        player.setLocalTranslation(new Vector3f(150,50,-20));
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
        getPhysicsSpace().add(tb);
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
/*
    private void buildPlayer() {
        float stiffness=120.0f;//200=f1 car
        float compValue=0.2f; //(lower than damp!)
        float dampValue=0.3f;
        final float mass = 400;

        Spatial car = assetManager.loadModel("Models/Ferrari/Car.scene");
        Node carNode = (Node) car;
        final Geometry chasis = findGeom(carNode, "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();

        final Vector3f extent = box.getExtent(null);

        // put chasis in center, so that physics box matches up with it
        // also remove from parent to avoid transform issues
        chasis.removeFromParent();
//        chasis.setLocalTranslation(Vector3f.UNIT_Y);
        chasis.setShadowMode(ShadowMode.Cast);

        CompoundCollisionShape compoundShape=new CompoundCollisionShape();
        compoundShape.addChildShape(new BoxCollisionShape(extent), Vector3f.UNIT_Y);

        player = new PhysicsVehicleNode(chasis, compoundShape, mass);

        //setting default values for wheels
        player.setSuspensionCompression(compValue*2.0f*FastMath.sqrt(stiffness));
        player.setSuspensionDamping(dampValue*2.0f*FastMath.sqrt(stiffness));
        player.setSuspensionStiffness(stiffness);
        player.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0,-1,0);
        Vector3f wheelAxle = new Vector3f(-1,0,0);

        Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
        wheel_fr.removeFromParent();
        wheel_fr.center();
        node_fr = new Node("wheel_node");
        node_fr.setShadowMode(ShadowMode.Cast);
        node_fr.attachChild(wheel_fr);
        Node primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_fr);
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f)-1f;
        float front_wheel_h = (wheelRadius * 1.9f)-1f;
        player.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);


        Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
        wheel_fl.removeFromParent();
        wheel_fl.center();
        node_fl = new Node("wheel_node");
        node_fl.setShadowMode(ShadowMode.Cast);
        node_fl.attachChild(wheel_fl);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_fl);
        box = (BoundingBox) wheel_fl.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(carNode, "WheelBackRight");
        wheel_br.removeFromParent();
        wheel_br.center();
        node_br = new Node("wheel_node");
        node_br.setShadowMode(ShadowMode.Cast);
        node_br.attachChild(wheel_br);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_br);
        box = (BoundingBox) wheel_br.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
        wheel_bl.removeFromParent();
        wheel_bl.center();
        node_bl = new Node("wheel_node");
        node_bl.setShadowMode(ShadowMode.Cast);
        node_bl.attachChild(wheel_bl);
        primaryNode = new Node("primary_wheel_node");
        primaryNode.attachChild(node_bl);
        box = (BoundingBox) wheel_bl.getModelBound();
        player.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

//        player.attachDebugShape(assetManager);
        player.getWheel(2).setFrictionSlip(4);
        player.getWheel(3).setFrictionSlip(4);
        rootNode.attachChild(player);
        getPhysicsSpace().add(player);
    }*/

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if(value)
                steeringValue+=.5f;
            else
                steeringValue+=-.5f;
            player.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if(value)
                steeringValue+=-.5f;
            else
                steeringValue+=.5f;
            player.steer(steeringValue);
        }
        //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if(value)
                accelerationValue-=800;
            else
                accelerationValue+=800;
            player.accelerate(accelerationValue);
        } else if (binding.equals("Downs")) {
            if(value)
                player.brake(40f);
            else
                player.brake(0f);
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                player.setLocalTranslation(0, 0, 0);
                player.setLocalRotation(new Quaternion());
                player.setLinearVelocity(Vector3f.ZERO);
                player.setAngularVelocity(Vector3f.ZERO);
                player.resetSuspension();
            } else {
            }
        } else if (binding.equals("PrintScreen")) {
            if (value) {
                System.out.println("Print Screen");
//                List<ViewPort> viewports = renderManager.getMainViews();

//                if (viewports.size() < 1) {
//                    System.out.println("No viewport available");
//                    return;
//                }

                //We only use one viewport, so... no problem here
//                ViewPort vp = viewports.get(0);

//                FrameBuffer frameBuffer =  vp.getOutputFrameBuffer();
//                renderManager.renderViewPortRaw(vp);

//                if (frameBuffer == null)
//                    System.out.println("tirame la re goma");
//                ByteBuffer byteBuffer = ByteBuffer.allocate(4 * frameBuffer.getWidth() * frameBuffer.getHeight());
//                renderer.readFrameBuffer(frameBuffer, byteBuffer);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
        camera.update(tpf);
    }
}