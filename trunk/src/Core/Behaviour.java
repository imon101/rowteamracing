package Core;

abstract class Behaviour {
	private static final long serialVersionUID = 1L;
	boolean started = false;

	public abstract void start();

	public abstract void update();

	public abstract void fixedUpdate();
}
