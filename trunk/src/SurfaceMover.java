import com.jme3.asset.AssetKey;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.binding.BindingListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SurfaceMover implements BindingListener {
	// The root of the objects we collide with
	Spatial scene;

	// The node moved by this
	Spatial movedNode;
	Spatial normalAligned;

	SphereMotionAllowedListener smal;
	Vector3f velocity = new Vector3f(0, 0, 0);

	public SurfaceMover(Spatial movedNode, Spatial child, Spatial scene, InputManager inputManager) {
		if (scene == null)
			throw new NullPointerException();

		this.scene = scene;
		//TODO: Clean SphereMotionAllowedListener code
		smal = new SphereMotionAllowedListener(scene, 0.2f);

		inputManager.registerKeyBinding("Left", KeyInput.KEY_J);
		inputManager.registerKeyBinding("Right", KeyInput.KEY_L);
		inputManager.registerKeyBinding("Up", KeyInput.KEY_I);
		inputManager.registerKeyBinding("Down", KeyInput.KEY_K);
		inputManager.registerKeyBinding("PgDn", KeyInput.KEY_PGDN);
		inputManager.registerKeyBinding("PgUp", KeyInput.KEY_PGUP);
		inputManager.registerKeyBinding("Space", KeyInput.KEY_SPACE);
		// used with method onBinding in BindingListener interface
		// in order to add function to keys
		inputManager.addBindingListener(this);
		
		this.movedNode = movedNode;
		this.normalAligned = child;
	}

	@Override
	public void onBinding(String binding, float value) {
		System.out.println(binding);
		
		if (binding.equals("Left")) {
			velocity.set(-5, 0, 0);
		} else if (binding.equals("Right")) {
			velocity.set(5, 0, 0);
		} else if (binding.equals("Up")) {
			velocity.set(0, 0, -5);
		} else if (binding.equals("Down")) {
			velocity.set(0, 0, 5);
		} else if (binding.equals("PgUp")) {
			velocity.set(0, 5, 0);
		} else if (binding.equals("PgDn")) {
			velocity.set(0, -5, 0);
		}
	}

	@Override
	public void onPostUpdate(float arg0) {
		// TODO Auto-generated method stub
	}

	Vector3f normal = new Vector3f();
	
	@Override
	public void onPreUpdate(float tpf) {
		Vector3f delta = velocity.mult(tpf);
		Vector3f pos = movedNode.getLocalTranslation().clone();
		smal.checkMotionAllowed(pos, delta, normal);
		movedNode.setLocalTranslation(pos.add(delta));
		velocity.set(0, 0, 0);
		
		normalAligned.rotateUpTo(normal);
	}

}
