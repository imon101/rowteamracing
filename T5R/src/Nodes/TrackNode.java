/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.nodes.PhysicsNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture.WrapMode;

/**
 *
 * @author ArkanRow
 */
public class TrackNode extends PhysicsNode {
    public TrackNode(AssetManager assetManager) {
        Geometry collisions = (Geometry) assetManager.loadModel("Models/Track/TrackCollisions.obj");
        mass = 0;
        setCollisionShape(new MeshCollisionShape(collisions.getMesh()));    //TODO Update collision shape
        attachDebugShape(assetManager); //TODO Remove

        LoadModel("Models/Track/Advertisments.obj", "Materials/Advertisment.j3m", assetManager);
        LoadModel("Models/Track/GuardRails.obj", "Materials/GuardRails.j3m", assetManager);
        LoadModel("Models/Track/Grass.obj", "Materials/Grass.j3m", assetManager);
        LoadModel("Models/Track/Track.obj", "Materials/Road.j3m", assetManager);
        LoadModel("Models/Track/Crowd.obj", "Materials/Crowd.j3m", assetManager);
        LoadModel("Models/Track/Seats.obj", "Materials/Wall.j3m", assetManager);
    }

    private void LoadModel(String model, String material, AssetManager assetManager) {
        Geometry geom = (Geometry) assetManager.loadModel(model);
        Material mat = assetManager.loadMaterial(material);

        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // show back side too
        mat.getAdditionalRenderState().setAlphaTest(true); // alpha on each face
        mat.getTextureParam("m_ColorMap").getTextureValue().setWrap(WrapMode.Repeat);

        geom.setShadowMode(ShadowMode.CastAndReceive);
        geom.setMaterial(mat);
        geom.setLocalTranslation(Vector3f.ZERO);

        attachChild(geom);
    }
}
