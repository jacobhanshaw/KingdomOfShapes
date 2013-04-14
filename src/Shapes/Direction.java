package Shapes;

/**
 * Represents a direction in the 2D plane. Directions can be used to control
 * which way shapes face and which way they move.
 * <p>
 * Directions are represented in degrees (or radians) starting with 0 degrees
 * pointing to the right, and increasing counterclockwise (like in a unit
 * circle). For example, up is 90 degrees and left is 180 degrees.
 */
public class Direction {
  private double degrees;

  /**
   * A direction pointing to the right (0 degrees).
   */
  public static Direction RIGHT = new Direction(0.0);
  /**
   * A direction pointing to the left (180 degrees).
   */
  public static Direction LEFT = new Direction(180.0);
  /**
   * A direction pointing downward (270 degrees).
   */
  public static Direction DOWN = new Direction(270.0);
  /**
   * A direction pointing upward (90 degrees).
   */
  public static Direction UP = new Direction(90.0);

  /**
   * Constructs a new direction pointing a given number of degrees
   * counterclockwise from straight right. So, for example, 0 degrees points
   * right, 90 degrees points up, and 180 degrees points left.
   *
   * @param degrees the number of degrees counterclockwise from straight
   *                right this direction will point.
   */
  public Direction(double degrees) {
    setDegrees(degrees);
  }

  /**
   * Constructs a new direction pointing from one point to another. The
   * constructed direction is the direction one would have to move to get from
   * <code>start</code> to <code>finish</code>.
   *
   * @param start   where this direction starts.
   * @param finish  where this direction ends.
   */
  public Direction(Point start, Point finish) {
    if (start == null) {
      throw new IllegalArgumentException("start must not be null");
    }
    if (finish == null) {
      throw new IllegalArgumentException("finish must not be null");
    }
    double deltaX = finish.getX() - start.getX();
    double deltaY = finish.getY() - start.getY();
    double radians = Math.atan2(deltaY, deltaX);
    setRadians(radians);
  }

  /**
   * Returns a new direction pointing a given number of radians
   * counterclockwise from straight right. So, for example, <code>0</code>
   * radians points
   * right, <code>Math.PI / 2.0</code> points up, and <code>Math.PI</code>
   * points up.
   *
   * @param radians the number of radians counterclockwise from straight right
   *                the returned direction will point.
   * @return        a new direction pointing in the specified direction.
   */
  public static Direction inRadians(double radians) {
    return new Direction(radians / Math.PI * 180.0);
  }

  /**
   * Constructs a new direction pointing a given number of degrees
   * counterclockwise from straight right. So, for example, 0 degrees points
   * right, 90 degrees points up, and 180 degrees points left. (This method
   * is basically identical to <code>new Direction(degrees)</code>.)
   *
   * @param degrees the number of degrees counterclockwise from straight
   *                right the returned direction will point.
   * @return        a new direction pointing in the specified direction.
   */
  public static Direction inDegrees(double degrees) {
    return new Direction(degrees);
  }

  /**
   * Return the number of radians this direction is from straight right when
   * rotated counterclockwise. So, for example, if this direction points right,
   * this method returns <code>0</code>, and if this direction points up it
   * returns <code>Math.PI / 2.0</code>.
   *
   * @return  double  radians counterclockwise from straight right this
   *                  direction points.
   */
  public double toRadians() {
    return degrees / 180.0 * Math.PI;
  }

  /**
   * Return the number of degrees this direction is from straight right when
   * rotated counterclockwise. So, for example, if this direction points right,
   * this method returns <code>0</code>, and if this direction points up it
   * returns <code>90</code>.
   *
   *
   * @return  double  degrees counterclockwise from straight right this
   *                  direction points.
   */
  public double toDegrees() {
    return degrees;
  }

  private void setDegrees(double degrees) {
    degrees %= 360.0;
    if (degrees < 0.0) {
      degrees += 360.0;
    }
    this.degrees = degrees;
  }

  private void setRadians(double radians) {
    double degrees = radians / Math.PI * 180.0;
    setDegrees(degrees);
  }

  /**
   * Returns the opposite direction. So, for example, if this direction
   * points up, the returned direction will point down.
   *
   * @return  the opposite direction.
   */
  public Direction reverse() {
    return new Direction(degrees + 180.0);
  }

  /**
   * Returns a direction perpendicular to this one. Note that each direction
   * in 2 dimensions has two perpendicular directions. This method returns
   * the perpendicular direction that is a 90 degree counterclockwise rotation
   * of this direction.
   * <p>
   * To get both perpendicular directions, use
   * <code>direction.perpendicular()</code> and
   * <code>direction.perpendicular().reverse()</code>.
   *
   * @return  a direction perpendicular to this one.
   */
  public Direction perpendicular() {
    return new Direction(degrees + 90.0);
  }

  /**
   * Returns a direction a given number of radians counterclockwise from this
   * direction.
   *
   * @param radians the measure of the angle in radians between this direction
   *                and the returned direction.
   * @return        the direction <code>radians</code> radians counterclockwise
   *                from this direction.
   */
  public Direction rotationByRadians(double radians) {
    return Direction.inRadians(toRadians() + radians);
  }

  /**
   * Returns a direction a given number of degrees counterclockwise from this
   * direction. (This method is identical to {@link #rotation}.)
   *
   * @param degrees the measure of the angle in degrees between this direction
   *                and the returned direction.
   * @return        the direction <code>degrees</code> degrees counterclockwise
   *                from this direction.
   */
  public Direction rotationByDegrees(double degrees) {
    return new Direction(this.degrees + degrees);
  }

  /**
   * Returns a direction a given number of degrees counterclockwise from this
   * direction.
   *
   * @param degrees the measure of the angle in degrees between this direction
   *                and the returned direction.
   * @return        the direction <code>degrees</code> degrees counterclockwise
   *                from this direction.
   */
  public Direction rotation(double degrees) {
    return rotationByDegrees(degrees);
  }

  /**
   * Returns a random direction. All directions are equally likely.
   *
   * @return  a random direction.
   */
  public static Direction random() {
    return new Direction(Math.random() * 360.0);
  }

  /**
   * Return whether this direction is more rightward than leftward.
   *
   * @return  <code>true</code> if this direction is within a quarter
   *          revolution of straight right, <code>false</code> if not.
   */
  public boolean isRightward() {
    return isWithinQuarterTurnOf(RIGHT);
  }

  /**
   * Return whether this direction is more upward than downward.
   *
   * @return  <code>true</code> if this direction is within a quarter
   *          revolution of straight up, <code>false</code> if not.
   */
  public boolean isUpward() {
    return isWithinQuarterTurnOf(UP);
  }

  /**
   * Return whether this direction is more leftward than rightward.
   *
   * @return  <code>true</code> if this direction is within a quarter
   *          revolution of straight left, <code>false</code> if not.
   */
  public boolean isLeftward() {
    return isWithinQuarterTurnOf(LEFT);
  }

  /**
   * Return whether this direction is more downward than upward.
   *
   * @return  <code>true</code> if this direction is within a quarter
   *          revolution of straight down, <code>false</code> if not.
   */
  public boolean isDownward() {
    return isWithinQuarterTurnOf(DOWN);
  }

  boolean isWithinQuarterTurnOf(Direction dir) {
    return
      Math.abs(dir.toDegrees() - degrees) < 90.0 ||
      Math.abs(dir.toDegrees() - degrees - 360.0) < 90.0 ||
      Math.abs(dir.toDegrees() - degrees + 360.0) < 90.0;
  }

  /**
   * Return whether this direction is equal to another object.
   *
   * @param obj the object with which to compare this direction.
   * @return    <code>true</code> if <code>obj</code> is a
   *            <code>Direction</code> object and it points in the same
   *            direction as this one (within a very small tolerance).
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Direction) {
      return equals((Direction) obj);
    }

    return false;
  }

  private boolean equals(Direction that) {
    return Geometry.equals(this.toDegrees(), that.toDegrees());
  }

  /**
   * Returns a string representation of this direction.
   *
   * @return  a string representation of this direction.
   */
  @Override
  public String toString() {
    return degrees + " degrees";
  }
}
