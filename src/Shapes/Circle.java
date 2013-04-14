package Shapes;

import java.util.*;
import java.awt.*;

/**
 * A circle that appears on screen and interacts with other shapes.
 * Many of <code>Circle</code>'s useful methods are in its superclass
 * {@link Shape}.
 */
public class Circle extends Shape {
  private double radius;

  /**
   * Construct a circle at the given center location with the given radius.
   *
   * @param center  the location of the circle's center.
   * @param radius  the length, in pixels, of the circle's radius.
   */
  public Circle(Point center, double radius) {
    super();
    setCenter(center);
    setRadius(radius);

    // set defaults
    setColor(Color.PINK);
  }

  /**
   * Construct a circle at a default location and with a default radius.
   */
  public Circle() {
    this(new Point(200, 400), 10);
  }

  public boolean contains(Point p) {
    if (p == null) {
      return false;
    }
    return Geometry.distance(getCenter(), p) < getRadius();
  }

  public boolean contains(Shape s) {
    if (s == null) {
      return false;
    }
    // nate: move disambiguation into Geometry, make Shape.contains(Shape) non-abstract, remove this code.
    if (this.isDestroyed() || s.isDestroyed()) {
      return false;
    }
    if (s instanceof Circle) {
      Circle c = (Circle)s;
      double distance = Geometry.distance(c.getCenter(), this.getCenter());
      return distance + c.getRadius() < this.getRadius();
    } else if (s instanceof ConvexPolygon) {
      ConvexPolygon poly = (ConvexPolygon) s;
      for (Point corner : poly.getCorners()) {
        if (!this.contains(corner)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  void render(Graphics2D g) {
    renderSpeech(g);
    if (isInvisible()) {
      return;
    }
    g.setColor(getColor());
    if (isFilled()) {
      g.fillOval((int)(getCenter().getCanvasX() - radius),
                 (int)(getCenter().getCanvasY() - radius),
                 (int)(radius * 2),
                 (int)(radius * 2));
    } else {
      g.drawOval((int)(getCenter().getCanvasX() - radius),
                 (int)(getCenter().getCanvasY() - radius),
                 (int)(radius * 2),
                 (int)(radius * 2));
    }
  }

  Point maxMovement(Point target, Segment obstacle) {
    Segment path = new Segment(getCenter(), target);
    Point maxMove = Geometry.maxMovement(this, target, obstacle);
    maxMove = Geometry.insertGap(this, path, maxMove);
    return maxMove;
  }

  Point maxMovement(Point target, Shape obstacle) {
    Segment path = new Segment(getCenter(), target);
    Point maxMove = Geometry.maxMovement(this, target, obstacle);
    maxMove = Geometry.insertGap(this, path, maxMove);
    return maxMove;
  }

  // Override this!
  public void update() {}

  // Override this!
  public void setup() {}

  public boolean isOffscreen() {
    if (
      getCenter().getX() - radius > Game.WIDTH ||
      getCenter().getX() + radius < 0.0 ||
      getCenter().getY() - radius > Game.HEIGHT ||
      getCenter().getY() + radius < 0.0
    ) {
      return true;
    }

    if (
      getCenter().getX() < Game.WIDTH && getCenter().getX() > 0.0 ||
      getCenter().getY() < Game.HEIGHT && getCenter().getY() > 0.0
    ) {
      return false;
    }

    for (Point corner : Game.getCorners()) {
      if (Geometry.distance(getCenter(), corner) < radius) {
        return false;
      }
    }
    return true;
  }

  public double getRight() {
    return getCenter().getX() + getRadius();
  }
  public double getTop() {
    return getCenter().getY() + getRadius();
  }
  public double getLeft() {
    return getCenter().getX() - getRadius();
  }
  public double getBottom() {
    return getCenter().getY() - getRadius();
  }

  /**
   * Returns the length of this circle's radius.
   *
   * @return  the length of this circle's radius in pixels.
   * @see     #setRadius(double)
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Set the length of this circle's radius.
   *
   * @param radius the new length of this circle's radius in pixels.
   * @see   #getRadius()
   */
  public void setRadius(double radius) {
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be positive.");
    }
    this.radius = radius;
  }

  @Override
  public String toString() {
    return
      "Circle at " + getCenter().toString() +
      " with radius " + getRadius();
  }
}
