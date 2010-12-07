/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author ArkanRow
 */
public class BillboardNode extends Node {
    final float billboardThickness =  0.001f;
    private Camera camera;

    private Matrix3f orientation;
    private Vector3f look;
    private Vector3f left;

    public BillboardNode(Camera camera, AssetManager assetManager, String material, Vector2f size) {
        this.camera = camera;

        orientation = new Matrix3f();
        look = new Vector3f();
        left = new Vector3f();

        Box plane = new Box(Vector3f.ZERO, size.x, -size.y, billboardThickness);
        Geometry geom = new Geometry("Billboard", plane);
        geom.setShadowMode(ShadowMode.Off);
        Material mat = assetManager.loadMaterial(material);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // show back side too
        mat.getAdditionalRenderState().setAlphaTest(true); // alpha on each face
        geom.setQueueBucket(RenderQueue.Bucket.Transparent); // IMPORTANT


        geom.setMaterial(mat);
        attachChild(geom);
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