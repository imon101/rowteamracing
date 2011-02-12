/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import Nodes.BillboardNode;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ArkanRow
 */
public class TreeCreator {
    static public void CreateTreesFromFile(String treeFile, Node root, 
            Camera camera, AssetManager assetManager) {
        //Billboard trees
        Material mat = assetManager.loadMaterial("Materials/Billboard.j3m");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // show back side too
        mat.getAdditionalRenderState().setAlphaTest(true); // alpha on each face

        Vector3f treePositions[];
        try {
            treePositions = Vector3ArrayLoader.load(treeFile);
            for (int i = 0; i < treePositions.length; i++)
                CreateTree(treePositions[i], root, mat, camera, assetManager);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TreeCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void CreateTree(Vector3f pos, Node root, Material mat, Camera cam, AssetManager assetManager) {
        BillboardNode billboard = new BillboardNode(cam, assetManager, mat, new Vector2f(3, 3));
        billboard.setLocalTranslation(pos);
        root.attachChild(billboard);
    }
}
