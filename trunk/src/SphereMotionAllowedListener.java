import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.SweepSphere;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class SphereMotionAllowedListener {

	private Ray ray = new Ray();
	private SweepSphere ss = new SweepSphere();
	private CollisionResults results = new CollisionResults();
	private Spatial scene;
	private float radius;

	private Vector3f newPos = new Vector3f();
	private Vector3f newVel = new Vector3f();

	final float unitsPerMeter = 100.0f;
	final float unitScale = unitsPerMeter / 100.0f;
	final float veryCloseDist = 0.005f * unitScale;

	private int depth = 0;

	public SphereMotionAllowedListener(Spatial scene, float radius) {
		if (scene == null)
			throw new NullPointerException();

		this.scene = scene;
		this.radius = radius;
	}

	private void collideWithWorld() {
		if (depth > 3) {
			return;
		}

		if (newVel.length() < veryCloseDist)
			return;

		Vector3f destination = newPos.add(0, radius / 3f, 0).add(newVel);

		ss.setCenter(newPos.add(0, radius / 3f, 0));
		ss.setVelocity(newVel);
		ss.setDimension(new Vector3f(radius * 2, radius * 4f / 3f, radius * 2));

		results.clear();
		scene.collideWith(ss, results);

		if (results.size() == 0) {
			newPos.addLocal(newVel);
			return;
		}

		for (int i = 0; i < results.size(); i++) {
			CollisionResult collision = results.getCollision(i);
			// *** collision occured ***
			Vector3f contactPoint = collision.getContactPoint().clone();
			float dist = collision.getDistance();

			if (dist >= veryCloseDist) {
				// P += ||V|| * dist
				Vector3f tmp = new Vector3f(newVel);
				tmp.normalizeLocal().multLocal(dist - veryCloseDist);
				newPos.addLocal(tmp);

				tmp.normalizeLocal();
				tmp.multLocal(veryCloseDist);
				contactPoint.subtractLocal(tmp);
			}

			Vector3f normal = collision.getContactNormal();

			Plane p = new Plane();
			p.setOriginNormal(contactPoint, normal);

			Vector3f destinationOnPlane = p.getClosestPoint(destination);
			newVel.set(destinationOnPlane).subtractLocal(contactPoint);

			// recurse:
			if (newVel.length() < veryCloseDist) {
				return;
			}
		}

		depth = depth + 1;
		collideWithWorld();
	}

	public void checkMotionAllowed(Vector3f position, Vector3f velocity, Vector3f normal) {
		if (velocity.getX() == 0 && velocity.getY() == 0 && velocity.getZ() == 0)
			return;

		depth = 0;
		newPos.set(position);
		newVel.set(velocity);
		velocity.setY(0);
		collideWithWorld();

		ray.setOrigin(newPos.add(0, -radius / 3f, 0));
		ray.setDirection(new Vector3f(0, -1, 0));

		results.clear();
		scene.collideWith(ray, results);
		CollisionResult result = results.getClosestCollision();
		if (result != null) {
			newPos.y = result.getContactPoint().getY() + radius;
		}

		normal.zero();
		for (CollisionResult r : results)
			normal.add(r.getContactNormal());
		normal.divide(results.size());
		if (results.size() >= 1)
			System.out.println("normal changed = " + normal);

		position.set(newPos);
	}

}
