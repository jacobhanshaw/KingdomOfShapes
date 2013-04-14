package Shapes;

/**
 * Represents a location in the 2D plane. Points are not displayed in the game.
 * <p>
 * The origin (<code>0</code>, <code>0</code>) is in the lower-lefthand corner
 * of the game window. The upper-righthand corner of the game window is
 * (<code>Game.WIDTH</code>, <code>Game.HEIGHT</code>).
 */
public class Point {
  private double x, y;

  /**
   * Constructs a new point with the given x and y coordinates.
   *
   * @param x the horizontal coordinate of the point.
   * @param y the vertical coordinate of the point.
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructs a new point in the center of the game window.
   */
  public Point() {
    this(Game.WIDTH / 2.0, Game.HEIGHT / 2);
  }

  Point translation(Vector v) {
    if (v == null) {
      throw new IllegalArgumentException("direction must not be null.");
    }
    Point p = new Point(x, y);
    p.setX(p.getX() + v.getXComponent());
    p.setY(p.getY() + v.getYComponent());
    return p;
  }

  /**
   * Returns a point the given number of pixels in the given direction.
   *
   * @param direction the direction to move to the returned point.
   * @param pixels    the number of pixels moved to the returned point.
   * @return          a point <code>pixels</code> pixels in the direction
   *                  <code>direction</code> away from this point.
   */
  public Point translation(Direction direction, double pixels) {
    return translation(new Vector(direction, pixels));
  }

  /**
   * Get the x-coordinate of this point.
   *
   * @return  the horizontal coordinate of this point.
   */
  public double getX() {
    return x;
  }

  /**
   * Get the y-coordinate of this point.
   *
   * @return  the vertical coordinate of this point.
   */
  public double getY() {
    return y;
  }
  
  int getCanvasX() {
    return (int)x;
  }

  int getCanvasY() {
    return (int)(Game.HEIGHT - y);
  }

  /**
   * Set the x-coordinate of this point.
   *
   * @param x the new horizontal coordinate of this point.
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Set the y-coordinate of this point.
   *
   * @param y the new vertical coordinate of this point.
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Returns a random point within the game window. The x-coordinate will be
   * between <code>0</code> and {@link Game#WIDTH}, and the y-coordinate will
   * be between <code>0</code> and {@link Game#HEIGHT}.
   *
   * @return  a random point in the game window.
   */
  public static Point random() {
    double x = Math.random() * Game.WIDTH;
    double y = Math.random() * Game.HEIGHT;
    return new Point(x, y);
  }

  /**
   * Returns the distance between this point and a given point.
   *
   * @param point the point with which to compare this point.
   * @return      the distance in pixels between this point and
   *              <code>point</code>.
   */
  public double distanceTo(Point point) {
    return Geometry.distance(this, point);
  }

  /**
   * Returns whether this point equals a given object.
   *
   * @param obj the object with which to compare this point.
   * @return    <code>true</code> if <code>obj</code> is a <code>Point</code>
   *            object and has the same x- and y-coordinates as this point
   *            (within a small tolerance).
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      return this.equals((Point) obj);
    }

    return false;
  }

  private boolean equals(Point that) {
    return 
      Math.abs(this.getX() - that.getX()) < Geometry.TOLERANCE &&
      Math.abs(this.getY() - that.getY()) < Geometry.TOLERANCE;
  }

  /**
   * Returns a string representation of this point.
   *
   * @return  a string representation of this point.
   */
  @Override
  public String toString() {
    return String.format("(%f, %f)", x, y);
  }

}
