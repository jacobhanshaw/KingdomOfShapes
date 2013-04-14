package Shapes;

import java.awt.event.*;

/**
 * Used in calls to {@link Keyboard#direction(KeySet)} to specify which
 * direction keys to listen to. Either the arrow keys or WASD.
 * <p>
 * <strong>Usage example:</strong>
 * <p>
 * <code>
 *  Direction inputDirection = Keyboard.direction(KeySet.WASD);<br />
 *  circle.move(inputDirection, 10);
 * </code>
 * <p>
 * This example will move <code>circle</code> ten pixels in the direction
 * pressed on the WASD keys.
 */
public enum KeySet {
  /**
   * The arrow keys.
   */
  ARROWS(Keyboard.UP, Keyboard.DOWN, Keyboard.LEFT, Keyboard.RIGHT),
  /**
   * The W, A, S, and D letter keys. These are often used as secondary
   * directional buttons, since they are laid out like the arrow keys on the
   * left side of the keyboard.
   */
  WASD(Keyboard.W, Keyboard.S, Keyboard.A, Keyboard.D);

  int UP, DOWN, LEFT, RIGHT;

  private KeySet(int up, int down, int left, int right) {
    UP = up;
    DOWN = down;
    LEFT = left;
    RIGHT = right;
  }

  Vector getVector(int keyCode) {
    if (keyCode == UP)
      return Vector.UP;
    if (keyCode == DOWN)
      return Vector.DOWN;
    if (keyCode == LEFT)
      return Vector.LEFT;
    if (keyCode == RIGHT)
      return Vector.RIGHT;

    return null;
  }
}
