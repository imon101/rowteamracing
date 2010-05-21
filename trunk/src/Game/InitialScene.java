package Game;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

import Core.Scene;

public class InitialScene extends Scene {
	@Override
	public void initialize() {
		rootNode.attachChild(new Box("my box", new Vector3f(0, 0, 0),
				new Vector3f(1, 1, 1)));
	}
}
