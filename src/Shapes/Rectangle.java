package Shapes;

import java.awt.*;

/**
 * A rectangle that appears on screen and interacts with other shapes.
 * Many of <code>Rectangle</code>'s useful methods are in its superclass
 * {@link Shape}.
 */
public class Rectangle extends ConvexPolygon {
  private double width, height;

  /**
   * Construct a rectangle with a given center, width and height.
   *
   * @param center  the location of the center of the rectangle.
   * @param width   the width of the rectangle in pixels.
   * @param height  the height of the rectangle in pixels.
   */
  public Rectangle(Point center, double width, double height) {
    setCenter(center);
    setWidth(width);
    setHeight(height);

    // set defaults
    setColor(Color.GREEN);
  }

  /**
   * Returns a default rectangle.
   */
  public Rectangle() {
    this(new Point(100, 100), 50, 100);
  }

  // Override this!
  public void setup() {}

  // Override this!
  public void update() {}

  Point[] getUnrotatedCorners() {
    return new Point[] {
      getCenter().translation(new Vector(-1 * width / 2, height / 2)),
      getCenter().translation(new Vector(width / 2,      height / 2)),
      getCenter().translation(new Vector(width / 2,      -1 * height / 2)),
      getCenter().translation(new Vector(-1 * width / 2, -1 * height / 2))
    };
  }

  @Override
  double maxRadius() {
    return Geometry.hypoteneuse(width / 2, height / 2);
  }

  /**
   * Sets the location of the upper-left corner of this rectangle. Does not
   * modify the width or height of the rectangle.
   * <p>
   * The upper-left corner of the rectangle is the corner that is in the
   * upper left when the rectangle is facing right.
   *
   * @param upperLeft the new location of the upper-left corner of this
   *                  rectangle.
   */
  public void setUpperLeft(Point upperLeft) {
    if (upperLeft == null) {
      throw new IllegalArgumentException("upperLeft must not be null.");
    }
    setCenter(upperLeft.getX() + width / 2, upperLeft.getY() - height / 2);
  }

  /**
   * Returns the height of this rectangle.
   *
   * @return  the height of this rectangle in pixels.
   */
  public double getHeight() {
    return height;
  }

  /**
   * Sets the height of this rectangle.
   *
   * @param height  the new height of this rectangle in pixels.
   */
  public void setHeight(double height) {
    this.height = height;
  }

  /**
   * Returns the width of this rectangle.
   *
   * @return  the width of this rectangle in pixels.
   */
  public double getWidth() {
    return width;
  }

  /**
   * Sets the width of this rectangle.
   *
   * @param width  the new width of this rectangle in pixels.
   */
  public void setWidth(double width) {
    this.width = width;
  }

  @Override
  public String toString() {
    return "Rectangle at " + getCenter().toString() +
      " with height " + getHeight() + " and width " + getWidth();
  }
}
