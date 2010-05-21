package Core;

import com.jme.scene.Node;

public abstract class Scene {
	protected Node rootNode;
	
	public Scene() {
		rootNode = Core.instance.rootNode;
	}

	public void addBehaviour(Behaviour behaviour) {
		Core.instance.behaviours.add(behaviour);
	}
	
	public void removeBehaviour(Behaviour behaviour) {
		Core.instance.behaviours.remove(behaviour);
	}
	
	public abstract void initialize();
}
