/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author ArkanRow
 */
public class BillboardNode extends Node {

    private Camera camera;

    private Matrix3f orientation;
    private Vector3f look;
    private Vector3f left;

    public BillboardNode(Camera camera) {
        this.camera = camera;

        orientation = new Matrix3f();
        look = new Vector3f();
        left = new Vector3f();
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        look.set(camera.getDirection()).negateLocal();
        left.set(camera.getLeft()).negateLocal();
        orientation.fromAxes(left, camera.getUp(), look);
        setLocalRotation(orientation);
    }
}