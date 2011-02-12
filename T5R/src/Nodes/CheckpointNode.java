/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;

import Controllers.GameController;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.nodes.PhysicsGhostNode;
import com.jme3.math.Vector3f;

/**
 *
 * @author ArkanRow
 */
public class CheckpointNode extends PhysicsGhostNode {
    int index;
    float extraTime;
    GameController gameController;
    CarNode car;

    public float getExtraTime() {
        return extraTime;
    }

    public float getIndex() {
        return index;
    }

    public CheckpointNode(int index, float extraTime, Vector3f position,
            float radius, CarNode car, GameController gameController,
            AssetManager assetManager) {
        super(new SphereCollisionShape(radius));
        this.index = index;
        this.extraTime = extraTime;
        this.gameController = gameController;
        this.car = car;
        attachDebugShape(assetManager);
        setLocalTranslation(position);
    }

    @Override
    public void updateLogicalState(float tpf) {
        if (getOverlappingObjects().contains(car)) {
            gameController.grabCheckpoint(this);
        }
    }
}
