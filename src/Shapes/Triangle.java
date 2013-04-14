package Shapes;

import java.awt.*;

/**
 * An isosceles triangle that appears on screen and interacts with other
 * shapes. Many of <code>Triangle</code>'s useful methods are in its
 * superclass {@link Shape}.
 * <p>
 */
public class Triangle extends ConvexPolygon {
  private double size;

  /**
   * Constructs a new triangle with the given center location and size. See
   * {@link #setSize} for more information about triangle size.
   *
   * @param center  the location of the triangle's center.
   * @param size    the size of the triangle (see {@link #setSize}).
   */
  public Triangle(Point center, double size) {
    super();
    setCenter(center);
    setSize(size);

    // set defaults
    setColor(Color.YELLOW);
  }

  /**
   * Constructs a default triangle.
   */
  public Triangle() {
    this(new Point(20, 400), 10);
  }

  // Override this!
  public void setup() {}

  // Override this!
  public void update() {}

  // Contract: zeroth corner is the tip
  Point[] getUnrotatedCorners() {
    return new Point[] {
      getCenter().translation(new Vector(size, 0)),
      getCenter().translation(new Vector(-size, size)),
      getCenter().translation(new Vector(-size, -size))
    };
  }

  /**
   * Returns the location of the tip of the triangle. The triangle is an
   * isoceles triangle with two long sides and one short side. The tip is the
   * corner between the two long sides. The tip points in the direction the
   * triangle is facing.
   *
   * @return  the location of the tip of the triangle.
   */
  public Point getTip() {
    return getCorners()[0];
  }

  /**
   * Set this triangle's size. The length of the triangle's side opposite the
   * tip is equal to <code>size</code>, and the length of the altitude from the tip
   * to that size is <code>size * 2</code>.
   * <p>
   * Basically, the triangle is pointy.
   *
   * @param size  the new size of the triangle.
   * @see   #getSize
   */
  public void setSize(double size) {
    if (size <= 0.0) {
      throw new IllegalArgumentException("size must be positive.");
    }
    this.size = size;
  }

  /**
   * Returns this triangle's size. See {@link #setSize} for the exact meaning
   * of size.
   *
   * @return  this triangle's size.
   * @see     #setSize
   */
  public double getSize() {
    return size;
  }
}
