package Shapes;

import java.awt.*;
import java.util.*;

/**
 * Displays numerical values, such as score or lives remaining. Counters are
 * displayed in the upper-left corner of the game window. They have a name and
 * value, both of which are displayed.
 */
public class Counter {
  private String name;
  private int value;
  private boolean destroyed = false;

  /**
   * Construct a new counter with a given name and value.
   *
   * @param name          the name of this counter.
   * @param initialValue  this counter's starting numerical value.
   */
  public Counter(String name, int initialValue) {
    if (name == null) {
      throw new IllegalArgumentException("name must not be null.");
    }
    this.name = name;
    this.value = initialValue;
    Game.addCounter(this);
  }

  /**
   * Increase this counter's value by a given amount.
   *
   * @param amount amount by which to increase this counter's value.
   */
  public void increaseBy(int amount) {
    value += amount;
  }

  /**
   * Decrease this counter's value by a given amount.
   *
   * @param amount  amount by which to decrease this counter's value.
   */
  public void decreaseBy(int amount) {
    value -= amount;
  }

  /**
   * Destroys this counter so it will be removed from the game. Call this method
   * only when you have finished using the counter.
   * <p>
   * A destroyed counter will not appear on the screen. A destroyed counter
   * cannot be undestroyed.
   */
  // nate: test whether this works
  public void destroy() {
    destroyed = true;
    Game.removeCounter(this);
  }

  static void renderCounters(java.util.List<Counter> counters, TextStyle style, Graphics2D g) {
    Point windowTopLeft = new Point(0, Game.HEIGHT);
    Point countersTopLeft = windowTopLeft.translation(new Vector(10, -10));
    StringBuffer buf = new StringBuffer();
    Iterator<Counter> iter = counters.iterator();
    while (iter.hasNext()) {
      buf.append(iter.next().toString());
      if (iter.hasNext()) {
        buf.append('\n');
      }
    }
    style.renderString(
      buf.toString(),
      countersTopLeft,
      TextStyle.ReferencePointLocation.TOP_LEFT,
      g,
      null
    );
  }

  /* Getters and settings*/

  /**
   * Sets this counter's name.
   *
   * @param name  this counter's new name.
   */
  public void setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name must not be null.");
    }
    this.name = name;
  }

  /**
   * Returns this counter's name.
   *
   * @return this counter's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set this counter's numerical value.
   *
   * @param value this counter's new numerical value.
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * Returns this counter's numerical value.
   *
   * @return this counter's numerical value.
   */
  public int getValue() {
    return value;
  }

  /**
   * Return whether this counter has been destroyed.
   *
   * @return  <code>true</code> if this counter has been destroyed,
   *          <code>false</code> if not.
   */
  public boolean isDestroyed() {
    return destroyed;
  }

  /**
   * Returns a string representing this counter.
   *
   * @return  a string representing this counter.
   */
  @Override
  public String toString() {
    return name + "  " + value;
  }
}
