package Shapes;

import java.awt.*;
import java.awt.event.*;

/**
 * Captures input from the mouse. <code>Mouse</code>, along with
 * {@link Keyboard}, is how you make your game interactive.
 * <p>
 * For example, to make a shape move towards the point where the mouse is
 * clicked, you would write:
 * <p>
 * <code>shape.move(shape.towards(Mouse.clickLocation(), 10);</code>
 */
public class Mouse implements MouseMotionListener, MouseListener {
  private static Point click;
  private static Point mouse;
  private static boolean inWindow = false;
  
  /**
   * Returns the location of a mouse click. If the mouse button is pressed and
   * then the mouse is dragged (without releasing the button), the new location
   * of the mouse is returned. Unlike {@link #mouseLocation}, this function
   * will return <code>null</code> if the mouse button isn't pressed.
   *
   * @return  the location of the mouse cursor, if the mouse button is pressed.
   *          Returns <code>null</code> if the mouse button isn't pressed.
   */
  public static Point clickLocation() {
    return click;
  }

  /**
   * Returns the location of the mouse cursor. Unlike {@link #clickLocation},
   * this function will return the mouse's location even if the mouse button
   * isn't pressed.
   *
   * @return  the location of the mouse cursor. If the mouse cursor hasn't yet
   *          entered the game window, returns <code>null</code>. If the cursor
   *          was in the window but has left, returns the location of the mouse
   *          just before it left the window.
   */
  public static Point mouseLocation() {
    return mouse;
  }

  /**
   * Returns whether the mouse cursor is in the game window.
   *
   * @return  <code>false</code> if the mouse cursor is outside the game window,
   *          <code>true</code> if inside.
   */
  public static boolean isInWindow() {
    return inWindow;
  }

  private void updateClick(MouseEvent event) {
    click = new Point(
      Geometry.fromCanvasX(event.getX()),
      Geometry.fromCanvasY(event.getY())
    );
  }

  private void updateMouse(MouseEvent event) {
    mouse = new Point(
      Geometry.fromCanvasX(event.getX()),
      Geometry.fromCanvasY(event.getY())
    );
  }

  /**
   * You can ignore this method. This method gets called when the mouse
   * button is pressed, and is used internally.
   */
  public void mousePressed(MouseEvent event) {
    updateClick(event);
    updateMouse(event);
  }

  /**
   * You can ignore this method. This method gets called when the mouse
   * is dragged, and is used internally.
   */
  public void mouseDragged(MouseEvent event) {
    updateClick(event);
    updateMouse(event);
  }

  /**
   * You can ignore this method. This method gets called when the mouse
   * button is released, and is used internally.
   */
  public void mouseReleased(MouseEvent event) {
    click = null;
  }

  /**
   * You can ignore this method. This method gets called when the mouse
   * is moved, and is used internally.
   */
  public void mouseMoved(MouseEvent event) {
    updateMouse(event);
  }

  /**
   * You can ignore this method. This method gets called when the mouse
   * exits the window, and is used internally.
   */
  public void mouseExited(MouseEvent event) {
    Mouse.inWindow = false;
  }
  /**
   * You can ignore this method. This method gets called when the mouse
   * enters the window, and is used internally.
   */
  public void mouseEntered(MouseEvent event) {
    Mouse.inWindow = true;
  }
  /**
   * You can ignore this method. This method gets called when the mouse
   * button is pressed, and is used internally.
   */
  public void mouseClicked(MouseEvent event) {}
}
