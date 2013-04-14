package Shapes;

class Vector {
  private Direction direction;
  private double magnitude;

  public static Vector UP = new Vector(Direction.UP, 1.0);
  public static Vector DOWN = new Vector(Direction.DOWN, 1.0);
  public static Vector LEFT = new Vector(Direction.LEFT, 1.0);
  public static Vector RIGHT = new Vector(Direction.RIGHT, 1.0);

  public Vector(Direction direction, double magnitude) {
    this.direction = direction;
    this.magnitude = magnitude;
  }

  public Vector(double x, double y) {
    direction = new Direction(0.0); // must be initialized to call setComponents
    setComponents(x, y);
  }

  public Vector(Point origin, Point terminus) {
    this(terminus.getX() - origin.getX(), terminus.getY() - origin.getY());
  }

  public Vector add(Vector that) {
    return new Vector(
      this.getXComponent() + that.getXComponent(),
      this.getYComponent() + that.getYComponent()
    );
  }

  public Vector perpendicular() {
    return new Vector(direction.perpendicular(), magnitude);
  }

  public Vector reverse() {
    return new Vector(direction.reverse(), magnitude);
  }

  private void setComponents(double x, double y) {
    direction = Direction.inRadians(Math.atan2(y, x));
    magnitude = Math.sqrt(x * x + y * y);
  }

  public double getXComponent() {
    return magnitude * Math.cos(direction.toRadians());
  }

  public double getYComponent() {
    return magnitude * Math.sin(direction.toRadians());
  }

  public Direction getDirection() {
    return direction;
  }

  public double getMagnitude() {
    return magnitude;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vector) {
      return equals((Vector) obj);
    }

    return false;
  }

  boolean equals(Vector that) {
    return this.getDirection().equals(that.getDirection()) &&
      Geometry.equals(this.getMagnitude(), that.getMagnitude());
  }

  @Override
  public String toString() {
    return String.format("direction: (%s) magnitude: %f", direction.toString(), magnitude);
  }
}
