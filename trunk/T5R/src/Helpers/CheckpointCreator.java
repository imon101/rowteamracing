/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import Controllers.GameController;
import Nodes.CarNode;
import Nodes.CheckpointNode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.FileNotFoundException;

/**
 *
 * @author ArkanRow
 */
public class CheckpointCreator {
    private static final float CHECKPOINT_RADIUS = 11;

    static public void createCheckpoints(String positionsFile,
            String timeExtensionsFile, CarNode car, GameController gameController,
            Node rootNode, PhysicsSpace space, AssetManager assetManager)
            throws FileNotFoundException {
        Vector3f[] positions = Vector3ArrayLoader.load(positionsFile);
        Float[] timeExt = FloatArrayLoader.load(timeExtensionsFile);

        if (positions.length != timeExt.length)
            throw new RuntimeException("Archivos de Checkpoints con cantidad de checkpoints diferentes");

        for (int i = 0; i < positions.length; i++) {
            CheckpointNode node = new CheckpointNode(i, timeExt[i], 
                    positions[i], CHECKPOINT_RADIUS, car, gameController,
                    assetManager);
            rootNode.attachChild(node);
            space.add(node);
        }

        gameController.setup(positions.length, positions[positions.length - 1],
                Quaternion.IDENTITY);
    }
}
