package Core;

import com.jme.scene.Spatial;
import com.jmex.physics.PhysicsSpace;

abstract public class Behaviour {
	private static final long serialVersionUID = 1L;
	boolean started = false;
	protected PhysicsSpace physicsSpace;

	public Behaviour() {
		physicsSpace = Core.instance.physicsSpace;
	}

	public void attachToRootNode(Spatial child) {
		Core.instance.rootNode.attachChild(child);
	}
	
	public abstract void start();

	public abstract void update();

	public abstract void fixedUpdate();
}
