import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * Test simple collision with a plane.
 */
public class TestSimpleCollision extends SimpleApplication {

	public static void main(String[] args) {
		TestSimpleCollision app = new TestSimpleCollision();
		AppSettings settings = new AppSettings(true);
		settings.setFrameRate(60);
		app.setSettings(settings);
		app.start();
	}

	SurfaceMover sm;

	@Override
	public void simpleInitApp() {
		Geometry g, sc;

		Sphere s = new Sphere(32, 32, 0.2f);
		sc = new Geometry("Sphere Character", s);

		sc.move(-2.5f, 0.5f, 2.5f);
		sc.setMaterial((Material) assetManager.loadAsset(new AssetKey(
				"Interface/Logo/Logo.j3m")));
		rootNode.attachChild(sc);

		Quad q = new Quad(5, 5);
		q.createCollisionData();

		Node scenario = new Node("Scenario");
		
		g = new Geometry("Quad Geom", q);
		g.rotate(FastMath.HALF_PI, 0, FastMath.PI);
		g.setMaterial((Material) assetManager.loadAsset(new AssetKey(
				"Interface/Logo/Logo.j3m")));
		scenario.attachChild(g);

		g = new Geometry("Hill", q);
		g.rotate(FastMath.HALF_PI * 1.1f, 0, FastMath.PI);
		g.move(0, 0, 5);
		g.setMaterial((Material) assetManager.loadAsset(new AssetKey(
				"Interface/Logo/Logo.j3m")));
		scenario.attachChild(g);

		rootNode.attachChild(scenario);
		
		sm = new SurfaceMover(sc, scenario, inputManager);
	}

}