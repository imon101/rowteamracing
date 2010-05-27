package Game;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jmetest.physics.vehicle.Car;

import Core.Behaviour;
import Core.Core;

public class CarBehaviour extends Behaviour {
	private Car car;

	@Override
	public void fixedUpdate() {
	}

	@Override
	public void start() {
		attachToRootNode(car = new Car(physicsSpace));

		// Attaching the custom input actions (and its respective keys) to the
		// carInput handler.
		InputHandler input = Core.getInstance().getInput();
		input.addAction(new AccelAction(car, 1), InputHandler.DEVICE_KEYBOARD,
				KeyInput.KEY_UP, InputHandler.AXIS_NONE, false);
		input.addAction(new AccelAction(car, -1), InputHandler.DEVICE_KEYBOARD,
				KeyInput.KEY_DOWN, InputHandler.AXIS_NONE, false);
		input.addAction(new SteerAction(car, -1), InputHandler.DEVICE_KEYBOARD,
				KeyInput.KEY_LEFT, InputHandler.AXIS_NONE, false);
		input.addAction(new SteerAction(car, 1), InputHandler.DEVICE_KEYBOARD,
				KeyInput.KEY_RIGHT, InputHandler.AXIS_NONE, false);
	}

	@Override
	public void update() {
	}

	/**
	 * Simple input action for steering the wheel.
	 */
	private class SteerAction implements InputActionInterface {
		Car car;

		int direction;

		public SteerAction(Car car, int direction) {
			this.car = car;
			this.direction = direction;
		}

		public void performAction(final InputActionEvent e) {
			// If the key is down (left or right) lets steer
			if (e.getTriggerPressed())
				car.steer(direction);
			// If it's up, lets unsteer
			else
				car.unsteer();
		}

	}

	/**
	 * Simple input action for accelerating and braking the car.
	 */
	private class AccelAction implements InputActionInterface {
		Car car;
		int direction;

		public AccelAction(final Car car, final int direction) {
			this.car = car;
			this.direction = direction;
		}

		public void performAction(final InputActionEvent e) {
			if (e.getTriggerPressed())
				car.accelerate(direction);
			// Otherwise, lets release the wheels.
			else
				car.releaseAccel();
		}
	}
}
