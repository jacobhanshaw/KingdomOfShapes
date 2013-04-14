package Shapes;

class Segment {
  private Point start, end;

  Segment(Point start, Point end) {
    setEndpoints(start, end);
  }

  Segment(Point start, Vector vector) {
    this.start = start;
    this.end = new Point(
      start.getX() + vector.getXComponent(),
      start.getY() + vector.getYComponent()
    );
  }

  Segment(double x1, double y1, double x2, double y2) {
    this(new Point(x1, y1), new Point(x2, y2));
  }

  void setEndpoints(Point start, Point end) {
    this.start = start;
    this.end = end;
  }

  // NOTE: this returns NaN if this is a vertical segment
  double slope() {
    return Geometry.slope(start, end);
  }

  double length() {
    return Geometry.distance(start, end);
  }

  boolean contains(Point p) {
    double circuit = Geometry.distance(start, p) + Geometry.distance(p, end);
    double difference = Math.abs(circuit - length());
    return difference < Geometry.EPSILON;
  }

  // NOTE: returns NaN if this is a horizontal segment
  double perpendicularSlope() {
    double slope = slope();
    if (slope == Double.NaN) {
      return 0.0;
    } else {
      return 1.0 / slope;
    }
  }

  Direction direction() {
    return vector().getDirection();
  }

  Vector vector() {
    return new Vector(end.getX() - start.getX(), end.getY() - start.getY());
  }

  Point getEnd() {
    return end;
  }

  Point getStart() {
    return start;
  }

  Point[] getEndpoints() {
    return new Point[] { getStart(), getEnd() };
  }

  Segment translation(Vector vector) {
    return new Segment(
      start.translation(vector),
      end.translation(vector)
    );
  }

  // Ax + By = C
  double getA() {
    return end.getY() - start.getY();
  }
  double getB() {
    return start.getX() - end.getX();
  }
  double getC() {
    return start.getX() * end.getY() - end.getX() * start.getY();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Segment) {
      return this.equals((Segment) obj);
    }

    return false;
  }

  private boolean equals(Segment that) {
    return
      this.getStart().equals(that.getStart()) &&
      this.getEnd().equals(that.getEnd());
  }

  @Override
  public String toString() {
    return String.format("start: %s end: %s", start.toString(), end.toString());
  }
}
