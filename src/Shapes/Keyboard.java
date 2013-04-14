package Shapes;

import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Captures input from the keyboard. <code>Keyboard</code>, along with
 * {@link Mouse}, is how you make your game interactive.
 * <p>
 * For example, to make a shape move according to the arrow keys, you would
 * write:
 * <p>
 * <code>shape.move(Keyboard.direction(), 10);</code>
 */
public class Keyboard extends KeyAdapter {
  private static Set<Integer> keysPressed;
  private static Integer mostRecentKeyPressed;
  private static boolean printKeyPresses = false;

  Keyboard() {
    if (keysPressed == null) {
      keysPressed =
        Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    }
  }

  /**
   * Print keypresses to standard output. Most of the methods in this class use
   * codes to represent keys, so this method can be used to determine which
   * codes represent which keys.
   * <p>
   * This class has constants equal to the codes for most common keys. For
   * example,
   * <code>boolean upIsPressed = Keyboard.keyIsPressed(Keyboard.UP);</code> can
   * be used to determine whether the up arrow is pressed.
   *
   * @param printKeyPresses passing <code>true</code> prints all key presses.
   */
  public static void printKeyPresses(boolean printKeyPresses) {
    Keyboard.printKeyPresses = printKeyPresses;
  }

  /**
   * Returns true if the given key is being pressed.
   *
   * @param keyCode integer representing the key whose presses to detect. (See
   *                {@link #printKeyPresses} to find which strings represent
   *                which keys.)
   * @return        true if the key is being pressed, false otherwise.
   */
  public static boolean keyIsPressed(int keyCode) {
    for (int pressedCode : keysPressed) {
      if (keyCode == pressedCode) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an array of integers representing all keys currently being pressed.
   * Returns an empty array if no keys are being pressed. See
   * {@link #printKeyPresses} to find which codes represent which keys.
   *
   * @return  an array of integers representing all keys currently being pressed.
   */
  public static int[] keys() {
    Iterator<Integer> keyPressedIterator = keysPressed.iterator();
    int[] keys = new int[keysPressed.size()];
    for (int i = 0; i < keys.length && keyPressedIterator.hasNext(); i++) {
      keys[i] = keyPressedIterator.next();
    }

    return keys;
  }

  /**
   * Returns an integer code representing the most recently pressed key.
   * Returns <code>Keyboard.NO_KEY</code>
   * if no key is currently being pressed. See {@link #printKeyPresses} to find
   * which integer codes represent which keys.
   *
   * @return  an integer code representing the most recently pressed key.
   */
  public static int key() {
    return mostRecentKeyPressed;
  }

  /**
   * Returns the direction currently being pressed on the arrow keys. Returns
   * null if no direction is being pressed (or if the directions being pressed
   * cancel each other out, like left and right).
   *
   * @return  a <code>Direction</code> object representing the direction being
   *          pressed on the arrow keys.
   */
  public static Direction direction() {
    return direction(KeySet.ARROWS);
  }

  /**
   * Returns the direction currently being pressed on the given key set.
   * Returns null if no direction is being pressed (or if the directions being
   * pressed cancel each other out, like left and right).
   *
   * @param set the set of keys to get directions from, either
   *            {@link KeySet#ARROWS} or {@link KeySet#WASD}.
   * @return    a <code>Direction</code> object representing the direction
   *            being pressed on the given key set.
   */
  public static Direction direction(KeySet set) {
    if (set == null) {
      throw new IllegalArgumentException("set must not be null.");
    }
    ArrayList<Vector> vectorsPressed = new ArrayList<Vector>();
    for (int keyPressed : keysPressed) {
      Vector vectorPressed = set.getVector(keyPressed);
      if (vectorPressed != null) {
        vectorsPressed.add(vectorPressed);
      }
    }

    if (vectorsPressed.size() == 0) {
      return null;
    }
    Vector ret = vectorsPressed.get(0);
    for (int i = 1; i < vectorsPressed.size(); i++) {
      ret = ret.add(vectorsPressed.get(i));
    }
    if (Math.abs(ret.getMagnitude()) < Geometry.EPSILON) {
      return null;
    }
    return ret.getDirection();
  }

  /**
   * You can ignore this method. This method gets called whenever a key is
   * pressed.
   */
  @Override
  public void keyPressed(KeyEvent keyPressedEvent) {
    keysPressed.add(keyPressedEvent.getKeyCode());
    mostRecentKeyPressed = keyPressedEvent.getKeyCode();
    if (printKeyPresses) {
      System.out.println(keyPressedEvent.getKeyCode());
    }
  }

  /**
   * You can ignore this method. This method gets called whenever a key is
   * released.
   */
  @Override
  public void keyReleased(KeyEvent keyReleasedEvent) {
    // remove from keysPressed
    Iterator<Integer> iter = keysPressed.iterator();
    while (iter.hasNext()) {
      if (iter.next() == keyReleasedEvent.getKeyCode()) {
        iter.remove();
      }
    }

    if (mostRecentKeyPressed == keyReleasedEvent.getKeyCode()) {
      mostRecentKeyPressed = NO_KEY;
    }
  }

  /**
   * Key code for the letter a.
   */
  public final static int A = 65;
  /**
   * Key code for the letter b.
   */
  public final static int B = 66;
  /**
   * Key code for the letter c.
   */
  public final static int C = 67;
  /**
   * Key code for the letter d.
   */
  public final static int D = 68;
  /**
   * Key code for the letter e.
   */
  public final static int E = 69;
  /**
   * Key code for the letter f.
   */
  public final static int F = 70;
  /**
   * Key code for the letter g.
   */
  public final static int G = 71;
  /**
   * Key code for the letter h.
   */
  public final static int H = 72;
  /**
   * Key code for the letter i.
   */
  public final static int I = 73;
  /**
   * Key code for the letter j.
   */
  public final static int J = 74;
  /**
   * Key code for the letter k.
   */
  public final static int K = 75;
  /**
   * Key code for the letter l.
   */
  public final static int L = 76;
  /**
   * Key code for the letter m.
   */
  public final static int M = 77;
  /**
   * Key code for the letter n.
   */
  public final static int N = 78;
  /**
   * Key code for the letter o.
   */
  public final static int O = 79;
  /**
   * Key code for the letter p.
   */
  public final static int P = 80;
  /**
   * Key code for the letter q.
   */
  public final static int Q = 81;
  /**
   * Key code for the letter r.
   */
  public final static int R = 82;
  /**
   * Key code for the letter s.
   */
  public final static int S = 83;
  /**
   * Key code for the letter t.
   */
  public final static int T = 84;
  /**
   * Key code for the letter u.
   */
  public final static int U = 85;
  /**
   * Key code for the letter v.
   */
  public final static int V = 86;
  /**
   * Key code for the letter w.
   */
  public final static int W = 87;
  /**
   * Key code for the letter x.
   */
  public final static int X = 88;
  /**
   * Key code for the letter y.
   */
  public final static int Y = 89;
  /**
   * Key code for the letter z.
   */
  public final static int Z = 90;

  /**
   * Key code for the zero key.
   */
  public final static int NUMBER_0 = 48;
  /**
   * Key code for the one key.
   */
  public final static int NUMBER_1 = 49;
  /**
   * Key code for the two key.
   */
  public final static int NUMBER_2 = 50;
  /**
   * Key code for the three key.
   */
  public final static int NUMBER_3 = 51;
  /**
   * Key code for the four key.
   */
  public final static int NUMBER_4 = 52;
  /**
   * Key code for the five key.
   */
  public final static int NUMBER_5 = 53;
  /**
   * Key code for the six key.
   */
  public final static int NUMBER_6 = 54;
  /**
   * Key code for the seven key.
   */
  public final static int NUMBER_7 = 55;
  /**
   * Key code for the eight key.
   */
  public final static int NUMBER_8 = 56;
  /**
   * Key code for the nine key.
   */
  public final static int NUMBER_9 = 57;

  /**
   * Key code for the left arrow key.
   */
  public final static int LEFT = 37;
  /**
   * Key code for the up arrow key.
   */
  public final static int UP = 38;
  /**
   * Key code for the right arrow key.
   */
  public final static int RIGHT = 39;
  /**
   * Key code for the down arrow key.
   */
  public final static int DOWN = 40;

  /**
   * Key code for the space bar.
   */
  public final static int SPACE = 32;
  /**
   * Key code for the enter key.
   */
  public final static int ENTER = 10;
  /**
   * Key code for the escape key.
   */
  public final static int ESC = 27;
  /**
   * Key code for the shift key.
   */
  public final static int SHIFT = 16;
  /**
   * Key code for the backspace key.
   */
  public final static int BACKSPACE = 8;

  /**
   * Key code representing no key. This value is returned by {@link #key} when
   * no keys are being pressed.
   */
  public final static int NO_KEY = -1;
}
