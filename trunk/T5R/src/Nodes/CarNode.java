/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.nodes.PhysicsVehicleNode;
import com.jme3.bullet.nodes.PhysicsVehicleWheel;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author ArkanRow
 */
public class CarNode extends PhysicsVehicleNode {
    float stiffness=120.0f; //200=f1 car
    float compValue=0.2f;   //(lower than damp!)
    float dampValue=0.3f;
    final float mass = 400; //400

    private PhysicsVehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;

    public CarNode(AssetManager assetManager) {
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

        this.attachChild(chasis);
        this.setCollisionShape(compoundShape);
        this.setMass(mass);

        //setting default values for wheels
        this.setSuspensionCompression(compValue*2.0f*FastMath.sqrt(stiffness));
        this.setSuspensionDamping(dampValue*2.0f*FastMath.sqrt(stiffness));
        this.setSuspensionStiffness(stiffness);
        this.setMaxSuspensionForce(10000);


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
        this.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
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
        this.addWheel(primaryNode, box.getCenter().add(0, -front_wheel_h, 0),
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
        this.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
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
        this.addWheel(primaryNode, box.getCenter().add(0, -back_wheel_h, 0),
                        wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        this.getWheel(2).setFrictionSlip(4);
        this.getWheel(3).setFrictionSlip(4);
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
}
