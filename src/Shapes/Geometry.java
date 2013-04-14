package Shapes;

import java.util.*;
import static java.lang.Math.*;

abstract class Geometry {

  // used for general double arithmetic
  static final double EPSILON = 0.001;
  // used for judging closeness (0.5 pixels apart == touching)
  static final double TOLERANCE = 0.5;

  // Returns false if the code for the given shape pair hasn't been written yet (in distance(Shape, Shape))
  // When writing new touching() methods, remember to use TOLERANCE. This means some other Geometry methods
  // (such as intersection(Circle, Segment)) won't be reliable.
  static boolean touching(Shape s, Shape t) {
    // Eww, gross code. Not sure of the best way to do this. Could instead have
    // identical isTouching() methods in each shape subclass, but that's also
    // not nice, since the actual computations should be in Geometry to avoid
    // duplication.
    if (s instanceof Circle) {
      if (t instanceof Circle) {
        return touching((Circle) s, (Circle) t);
      } else if (t instanceof ConvexPolygon) {
        return touching((Circle) s, (ConvexPolygon) t);
      } else {
        return false;
      }
    } else if (s instanceof ConvexPolygon) {
      if (t instanceof Circle) {
        return touching((ConvexPolygon) s, (Circle) t);
      } else if (t instanceof ConvexPolygon) {
        return touching((ConvexPolygon) s, (ConvexPolygon) t);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  static boolean touching(ConvexPolygon poly, Circle circle) {
    return touching(circle, poly);
  }

  static boolean touching(Circle circle, ConvexPolygon poly) {
    for (Point corner : poly.getCorners()) {
      if (distance(corner, circle.getCenter()) <
          circle.getRadius() + TOLERANCE) {
        return true;
      }
    }

    for (Segment side : poly.getSides()) {
      if (touching(circle, side)) {
        return true;
      }
    }

    if (circle.contains(poly) || poly.contains(circle)) {
      return true;
    }

    return false;
  }

  static boolean touching(ConvexPolygon s, ConvexPolygon t) {
    for (Point corner : s.getCorners()) {
      for (Segment side : t.getSides()) {
        if (touching(side, corner)) {
          return true;
        }
      }
    }

    for (Segment side : s.getSides()) {
      for (Point corner : t.getCorners()) {
        if (touching(side, corner)) {
          return true;
        }
      }
    }

    for (Point cornerA : s.getCorners()) {
      for (Point cornerB : t.getCorners()) {
        if (touching(cornerA, cornerB)) {
          return true;
        }
      }
    }

    for (Segment sideA : s.getSides()) {
      for (Segment sideB : t.getSides()) {
        if (segmentIntersection(sideA, sideB) != null) {
          return true;
        }
      }
    }

    if (s.contains(t) || t.contains(s)) {
      return true;
    }

    return false;
  }

  static boolean touching(Circle s, Circle t) {
    double centerDistance = distance(s.getCenter(), t.getCenter());
    return centerDistance < s.getRadius() + t.getRadius() + TOLERANCE;
  }

  static boolean touching(Circle circle, Segment seg) {
    Segment perp = perpendicularThrough(seg, circle.getCenter());
    if (perp.length() < circle.getRadius() + TOLERANCE &&
        seg.contains(perp.getEnd())
    ) {
      return true;
    }
    return false;
  }

  static boolean touching(ConvexPolygon poly, Segment seg) {
    for (Segment side : poly.getSides()) {
      if (segmentIntersection(side, seg) != null) {
        return true;
      }
    }

    for (Point corner : poly.getCorners()) {
      if (touching(seg, corner)) {
        return true;
      }
    }

    return false;
  }

  // Returns false if the code for the given shape pair hasn't been written yet.
  // nate: test
  static boolean touching(Shape shape, Segment seg) {
    if (shape instanceof Circle) {
      return touching((Circle) shape, seg);
    } else if (shape instanceof ConvexPolygon) {
      return touching((ConvexPolygon) shape, seg);
    }

    return false;
  }

  static boolean touching(Segment seg, Point point) {
    Segment perp = perpendicularThrough(seg, point);
    if (perp.length() < TOLERANCE && seg.contains(perp.getEnd())) {
      return true;
    }

    return false;
  }

  static boolean touching(Point s, Point t) {
    return distance(s, t) < TOLERANCE;
  }

  // static boolean touching(Segment, Segment);
  // does not exist because the other touching methods test the interior
  // of segments (using segmentIntersection) and their endpoints (using 
  // touching(Segment, Point)) individually

  // returns null for no intersection
  static Point[] intersection(BasicCircle s, BasicCircle t) {
    double distance = distance(s.center, t.center);
    if (distance > s.radius + t.radius) {
      return null;
    }
    
    // in the triangle formed by two radii and the segment connecting the
    // circles' centers, theta is the angle at s's center.
    double theta = acos(
      (-1 * sq(s.radius) + sq(t.radius) + sq(distance)) /
      (2 * t.radius * distance)
    );  // law of cosines

    Direction betweenCenters = new Direction(t.center, s.center);
    Direction[] radiiDirections = new Direction[2];
    radiiDirections[0] = betweenCenters.rotationByRadians(theta);
    radiiDirections[1] = betweenCenters.rotationByRadians(-1.0 * theta);

    Point[] intersections = new Point[2];
    for (int i = 0; i < intersections.length; i++) {
      Vector radius = new Vector(radiiDirections[i], t.radius);
      intersections[i] = t.center.translation(radius);
    }
    return intersections;
  }

  // nate: test
  // line intersection, not segment intersection
  // returns empty array for no intersection
  static Point[] lineIntersection(BasicCircle circle, Segment line) {
    Segment perp = perpendicularThrough(line, circle.center);
    if (perp.length() > circle.radius) {
      return new Point[0];
    }

    // angle (at the center of the circle) between the perpendicular and
    // the radii to points of intersection
    double radiansFromPerp = acos(perp.length() / circle.radius);

    // direction of radii to points of intersection
    Direction[] radiusDirection = new Direction[] {
      perp.direction().rotationByRadians(radiansFromPerp),
      perp.direction().rotationByRadians(-1.0 * radiansFromPerp)
    };

    Point[] pointOfIntersection = new Point[radiusDirection.length];
    for (int i = 0; i < radiusDirection.length; i++) {
      Vector radius = new Vector(radiusDirection[i], circle.radius);
      pointOfIntersection[i] =
        circle.center.translation(radius);
    }

    return pointOfIntersection;
  }

  static Point[] segmentIntersection(BasicCircle circle, Segment seg) {
    List<Point> segmentIntersections = new ArrayList<Point>();
    for (Point intersection : lineIntersection(circle, seg)) {
      if (seg.contains(intersection)) {
        segmentIntersections.add(intersection);
      }
    }
    return segmentIntersections.toArray(new Point[0]);
  }

  // returns null for parallel lines
  static Point lineIntersection(Segment s, Segment t) {
    double denominator = s.getA() * t.getB() - s.getB() * t.getA();
    if (Math.abs(denominator) < EPSILON) {
      return null;
    }

    double x = (s.getC() * t.getB() - s.getB() * t.getC()) / denominator;
    double y = (s.getA() * t.getC() - s.getC() * t.getA()) / denominator;
    return new Point(x, y);
  }

  static Point segmentIntersection(Segment s, Segment t) {
    Point intersection = lineIntersection(s, t);
    if (intersection == null) {
      return null;
    }
    if (s.contains(intersection) && t.contains(intersection)) {
      return intersection;
    } else {
      return null;
    }
  }

  static Point segmentLineIntersection(Segment segment, Segment line) {
    Point intersection = lineIntersection(segment, line);
    if (intersection == null) {
      return null;
    }
    if (segment.contains(intersection)) {
      return intersection;
    } else {
      return null;
    }
  }

  // Returns a segment perpendicular to the original segment, starting at the
  // given point and ending on the segment (or corresponding line).
  static Segment perpendicularThrough(Segment original, Point through) {
    if (original.length() < EPSILON) {
      // nate: WTF do I do in this case?
      return new Segment(through, original.getStart());
    }
    // perp is perpendicular and has the correct start, but does not have the
    // correct end
    Segment perp = new Segment(
      through,
      through.translation(original.vector().perpendicular())
    );

    Point intersection = lineIntersection(original, perp);
    
    return new Segment(through, intersection);
  }

  static Point maxMovement(Shape mover, Point target, Shape obstacle) {
    Segment path = new Segment(mover.getCenter(), target);
    Point maxMove = null;

    if (mover instanceof Circle) {
      if (obstacle instanceof Circle) {
        maxMove = maxMovement((Circle) mover, target, (Circle) obstacle);
      } else if (obstacle instanceof ConvexPolygon) {
        maxMove = maxMovement((Circle) mover, target, (ConvexPolygon) obstacle);
      }
    } else if (mover instanceof ConvexPolygon) {
      if (obstacle instanceof Circle) {
        maxMove = maxMovement((ConvexPolygon) mover, target, (Circle) obstacle);
      } else if (obstacle instanceof ConvexPolygon) {
        maxMove =
          maxMovement((ConvexPolygon) mover, target, (ConvexPolygon) obstacle);
      }
    } 
    if (maxMove == null) {
      return target;
    }

    maxMove = insertGap(mover, path, maxMove);
    return maxMove;
  }

  // if mover wants to go to target, but obstacle is in the way,
  // how far can it go?
  static Point maxMovement(Circle mover, Point target, Circle obstacle) {
    Segment path = new Segment(mover.getCenter(), target);
    Point maxMove = target;

    Segment obstacleToPath = perpendicularThrough(path, obstacle.getCenter());
    double distanceToPath = obstacleToPath.length();
    double distanceBetweenCenters = mover.getRadius() + obstacle.getRadius();

    if (distanceToPath > distanceBetweenCenters) {
      return target;   // no collision
    }

    // nate: rename variables
    // closest is the point on path closest to obstacle center
    // stopPoint is where the center of mover will be after moving
    Point closest = obstacleToPath.getEnd();
    double distanceToStopPoint =
      sqrt(sq(distanceBetweenCenters) - sq(distanceToPath));
    maxMove =
      closest.translation(path.direction().reverse(), distanceToStopPoint);

    if (!path.contains(maxMove)) {
      return target;
    }

    return maxMove;
  }

  static Point maxMovement(
      Circle mover,
      Point target,
      ConvexPolygon obstacle
  ) {
    Segment path = new Segment(mover.getCenter(), target);
    Point maxMove = target;

    for (Point corner : obstacle.getCorners()) {
      Segment perp = perpendicularThrough(path, corner);
      double perpDistance = perp.length();
      if (perpDistance > mover.getRadius()) {
        continue;
      }

      Point closest = perp.getEnd();
      double centerDestinationToClosest =
        sqrt(sq(mover.getRadius()) - sq(perpDistance));
      Point centerDestination = closest.translation(
        path.direction().reverse(),
        centerDestinationToClosest
      );
      
      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    for (Segment side : obstacle.getSides()) {
      Point centerDestination = maxMovement(mover, target, side);
      
      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    return maxMove;
  }

  static Point maxMovement(Circle mover, Point target, Segment obstacle) {
    Segment path = new Segment(mover.getCenter(), target);
    Point segmentPathIntersection = lineIntersection(obstacle, path);
    if (segmentPathIntersection == null) {
      return target;
    }
    double segmentPathAngle =
      interiorRadians(path.direction(), obstacle.direction());
    double hypoteneuse = mover.getRadius() / sin(segmentPathAngle);
    Point centerDestination = segmentPathIntersection.translation(
      path.direction().reverse(),
      hypoteneuse
    );

    if (
      distance(mover.getCenter(), centerDestination) >
      distance(mover.getCenter(), segmentPathIntersection)
    ) {
      // obstacle is facing the wrong way for a collision to occur
      return target;
    }

    Segment destinationRadius =
      perpendicularThrough(obstacle, centerDestination);

    if (!obstacle.contains(destinationRadius.getEnd()) ||
        !path.contains(centerDestination)) {
      return target;
    }
    
    return centerDestination;
  }

  static Point maxMovement(
      ConvexPolygon mover,
      Point target,
      ConvexPolygon obstacle
  ) {
    Point maxMove = target;
    // nate: maybe parameters should be maxMovement(mover, path, obstacle)
    // to avoid recreating path?
    Segment path = new Segment(mover.getCenter(), target);

    // Two cases: corner hits side, side hits corner
    for (Segment obstacleSide : obstacle.getSides()) {
      Point centerDestination = maxMovement(mover, target, obstacleSide);
      
      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    for (Point obstacleCorner : obstacle.getCorners()) {
      for (Segment side : mover.getSides()) {
        // intersectionPath is a line parallel to path through obstacleCorner
        Segment intersectionPath = new Segment(
          obstacleCorner, 
          obstacleCorner.translation(path.vector())
        );
        Point intersectionOrigin = segmentLineIntersection(side, intersectionPath);
        if (intersectionOrigin == null) {
          continue;
        }
        Vector intersectionOffset =
          new Vector(mover.getCenter(), intersectionOrigin);
        Point centerDestination =
          obstacleCorner.translation(intersectionOffset.reverse());

        if (isShorterMovement(centerDestination, maxMove, path)) {
          maxMove = centerDestination;
        }
      }
    }

    return maxMove;
  }

  static Point maxMovement(
      ConvexPolygon mover,
      Point target,
      Circle obstacle
  ) {
    Point maxMove = target;
    Segment path = new Segment(mover.getCenter(), target);

    // check corner collisions
    for (Point corner : mover.getCorners()) {
      Vector cornerOffset = new Vector(mover.getCenter(), corner);
      Point cornerTarget = target.translation(cornerOffset);
      Segment cornerPath = new Segment(corner, cornerTarget);

      Point[] intersections =
        lineIntersection(new BasicCircle(obstacle), cornerPath);
      if (intersections.length < 2) {
        // length is either 0 or 2
        continue;
      }

      // of the two intersection points, this one is closer to mover's starting point
      Point closer; 
      if (
        distance(intersections[0], corner) <
        distance(intersections[1], corner)
      ) {
        closer = intersections[0];
      } else {
        closer = intersections[1];
      }

      Point centerDestination = closer.translation(cornerOffset.reverse());
      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    // check side collisions
    for (Segment side : mover.getSides()) {
      Segment perp = perpendicularThrough(side, obstacle.getCenter());
      Vector radiusToIntersection =
        new Vector(perp.direction(), obstacle.getRadius());
      Point intersection = obstacle.getCenter().translation(radiusToIntersection);

      // intersectionPath has the correction direction, but not
      // the correct endpoints
      Segment intersectionPath =
        new Segment(intersection, intersection.translation(path.vector()));
      Point intersectionOrigin =
        segmentLineIntersection(side, intersectionPath);
      
      if (intersectionOrigin == null) {
        continue;
      }

      Vector intersectionOffset =
        new Vector(mover.getCenter(), intersectionOrigin);
      Point centerDestination =
        intersection.translation(intersectionOffset.reverse());

      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    return maxMove;
  }

  static Point maxMovement(
      ConvexPolygon mover,
      Point target,
      Segment obstacle
  ) {
    Point maxMove = target;
    Segment path = new Segment(mover.getCenter(), target);

    for (Point corner : mover.getCorners()) {
      Vector cornerOffset = new Vector(mover.getCenter(), corner);
      Segment cornerPath =
        new Segment(corner, target.translation(cornerOffset));
      Point intersection =
        segmentLineIntersection(obstacle, cornerPath);
      if (intersection == null) {
        continue;
      }
      Point centerDestination =
        intersection.translation(cornerOffset.reverse());

      if (isShorterMovement(centerDestination, maxMove, path)) {
        maxMove = centerDestination;
      }
    }

    return maxMove;
  }

  static Point insertGap(Shape mover, Segment path, Point maxMove) {
    // nate: put buffer between mover and obstacle, not along mover's path?
    Vector backwards =
      new Vector(path.vector().getDirection().reverse(), TOLERANCE / 4.0);
    Point ret = maxMove.translation(backwards);
    if (!path.contains(ret)) {
      // but don't overcompensate
      ret = mover.getCenter();
    }

    return ret;
  }

  static Direction insertGap(
      Shape rotator,
      boolean clockwise,
      Direction target,
      Direction maxRotate
  ) {
    Direction ret;
    if (clockwise) {
      ret = maxRotate.rotationByDegrees(0.05);
    } else {
      ret = maxRotate.rotationByDegrees(-0.05);
    }
    if (closer(rotator.getDirection(), clockwise, ret, maxRotate) == ret) {
      return ret;
    } else {
      return rotator.getDirection();
    }
  }

  static double interiorRadians(Direction dirA, Direction dirB) {
    double difference = abs(dirA.toRadians() - dirB.toRadians());
    if (difference > PI) {
      difference = 2 * PI - difference;
    }
    if (difference > PI / 2) {
      difference = PI - difference;
    }
    return difference;
  }

  // helper method for maxMovement()
  private static boolean isShorterMovement(
    Point candidate,
    Point leader,
    Segment path
  ) {

    if (!path.contains(candidate)) {
      return false;
    }

    return
      distance(candidate, path.getStart()) <
      distance(leader, path.getStart());
  }

  static Direction maxRotation(
      Shape rotator,
      Direction target,
      boolean clockwise,
      Shape obstacle
  ) {
    Direction maxRotate = null;
    if (rotator instanceof Circle) {
      maxRotate = target;   // circle rotation doesn't cause collisions
    } else if (rotator instanceof ConvexPolygon) {
      if (obstacle instanceof Circle) {
        maxRotate = maxRotation(
          (ConvexPolygon) rotator,
          target,
          clockwise,
          (Circle) obstacle
        );
      } else if (obstacle instanceof ConvexPolygon) {
        maxRotate = maxRotation(
          (ConvexPolygon) rotator,
          target,
          clockwise,
          (ConvexPolygon) obstacle
        );
      }
    } 
    if (maxRotate == null) {
      return target;
    }

    maxRotate = insertGap(rotator, clockwise, target, maxRotate);

    return maxRotate;
  }

  static Direction maxRotation(
      Shape rotator,
      Direction target,
      boolean clockwise,
      Segment obstacle
  ) {
    Direction maxRotate = null;
    if (rotator instanceof Circle) {
      maxRotate = target;   // circle rotation doesn't cause collisions
    } else if (rotator instanceof ConvexPolygon) {
      maxRotate = maxRotation(
        (ConvexPolygon) rotator,
        target,
        clockwise,
        obstacle
      );
    }
    if (maxRotate == null) {
      return target;
    }

    maxRotate = insertGap(rotator, clockwise, target, maxRotate);

    return maxRotate;
  }
  
  static Direction maxRotation(
      ConvexPolygon rotator,
      Direction target,
      boolean clockwise,
      Segment obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = rotator.getDirection();

    for (Point corner : rotator.getCorners()) {
      Direction cornerOrigin = new Direction(rotator.getCenter(), corner);
      double cornerOffset = cornerOrigin.toRadians() - origin.toRadians();
      Direction cornerTarget = target.rotationByRadians(cornerOffset);
      Direction cornerCandidate = maxRotation(
        corner,
        rotator.getCenter(),
        cornerTarget,
        clockwise,
        obstacle
      );
      Direction candidate =
        cornerCandidate.rotationByRadians(-1 * cornerOffset);
      maxRotate = closer(origin, clockwise, candidate, maxRotate);
    }

    return maxRotate;
  }

  static Direction maxRotation(
      ConvexPolygon rotator,
      Direction target,
      boolean clockwise,
      Circle obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = rotator.getDirection();

    for (Point corner : rotator.getCorners()) {
      Direction cornerOrigin = new Direction(rotator.getCenter(), corner);
      double cornerOffset = cornerOrigin.toRadians() - origin.toRadians();
      Direction cornerTarget = target.rotationByRadians(cornerOffset);
      Direction cornerCandidate = maxRotation(
        corner,
        rotator.getCenter(),
        cornerTarget,
        clockwise,
        obstacle
      );
      Direction candidate =
        cornerCandidate.rotationByRadians(-1 * cornerOffset);
      maxRotate = closer(origin, clockwise, candidate, maxRotate);
    }

    for (Segment side : rotator.getSides()) {
      Direction sideOrigin =
        new Direction(rotator.getCenter(), side.getStart());
      double sideOffset = sideOrigin.toRadians() - origin.toRadians();
      Direction sideTarget = target.rotationByRadians(sideOffset);
      Direction sideCandidate = maxRotation(
        side,
        rotator.getCenter(),
        sideTarget,
        clockwise,
        obstacle
      );
      Direction candidate =
        sideCandidate.rotationByRadians(-1 * sideOffset);
      maxRotate = closer(origin, clockwise, candidate, maxRotate);
    }

    return maxRotate;
  }

  static Direction maxRotation(
      ConvexPolygon rotator,
      Direction target,
      boolean clockwise,
      ConvexPolygon obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = rotator.getDirection();

    for (Point corner : rotator.getCorners()) {
      Direction cornerOrigin = new Direction(rotator.getCenter(), corner);
      double cornerOffset = cornerOrigin.toRadians() - origin.toRadians();
      Direction cornerTarget = target.rotationByRadians(cornerOffset);
      for (Segment obstacleSide : obstacle.getSides()) {
        Direction cornerCandidate = maxRotation(
          corner,
          rotator.getCenter(),
          cornerTarget,
          clockwise,
          obstacleSide
        );
        Direction candidate =
          cornerCandidate.rotationByRadians(-1 * cornerOffset);
        maxRotate = closer(origin, clockwise, candidate, maxRotate);
      }
    }

    for (Segment side : rotator.getSides()) {
      Direction sideOrigin =
        new Direction(rotator.getCenter(), side.getStart());
      double sideOffset = sideOrigin.toRadians() - origin.toRadians();
      Direction sideTarget = target.rotationByRadians(sideOffset);
      for (Point obstacleCorner : obstacle.getCorners()) {
        Direction sideCandidate = maxRotation(
          side,
          rotator.getCenter(),
          sideTarget,
          clockwise,
          obstacleCorner
        );
        Direction candidate =
          sideCandidate.rotationByRadians(-1 * sideOffset);
        maxRotate = closer(origin, clockwise, candidate, maxRotate);
      }
    }

    return maxRotate;
  }

  // returns direction from pivot to rotator.getStart()
  static Direction maxRotation(
    Segment rotator,
    Point pivot,
    Direction target,
    boolean clockwise,
    Circle obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = new Direction(pivot, rotator.getStart());

    // Instead of rotating rotator, imagine revolving obstacle around pivot
    // and finding the points where it is tangent to rotator. To do so, draw
    // a circle (obstacleRevolution) around pivot and find the points
    // (revolutionIntersections) on the circle that are obstacle.getRadius()
    // pixels away from rotator.
    BasicCircle obstacleRevolution = new BasicCircle(
      pivot,
      distance(pivot, obstacle.getCenter())
    );

    Vector[] rotatorShifts = new Vector[2];
    rotatorShifts[0] = new Vector(
      rotator.direction().perpendicular(),
      obstacle.getRadius()
    );
    rotatorShifts[1] = rotatorShifts[0].reverse();

    List<Point> revolutionIntersections = new ArrayList<Point>();
    for (Vector rotatorShift : rotatorShifts) {
      Segment shifted = rotator.translation(rotatorShift);
      Collections.addAll(
        revolutionIntersections,
        segmentIntersection(obstacleRevolution, shifted)
      );
    }

    Direction obstacleDirection = new Direction(pivot, obstacle.getCenter());
    for (Point revolutionIntersection : revolutionIntersections) {
      Direction d = new Direction(pivot, revolutionIntersection);
      double intersectionOffset =
        obstacleDirection.toRadians() - d.toRadians();
      Direction candidate =
        origin.rotationByRadians(intersectionOffset);

      maxRotate = closer(origin, clockwise, candidate, maxRotate);
    }

    return maxRotate;
  }

  static Direction maxRotation(
    Segment rotator,
    Point pivot,
    Direction target,
    boolean clockwise,
    Point obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = new Direction(pivot, rotator.getStart());

    BasicCircle intersectionPath =
      new BasicCircle(pivot, distance(pivot, obstacle));
    Point[] intersectionOrigins = lineIntersection(intersectionPath, rotator);
    for (Point intersectionOrigin : intersectionOrigins) {
      if (!rotator.contains(intersectionOrigin)) {
        continue;
      }
      Direction intersectionOriginDirection =
        new Direction(pivot, intersectionOrigin);
      double intersectionOffset =
        intersectionOriginDirection.toRadians() - origin.toRadians();
      Direction intersectionDirection = new Direction(pivot, obstacle);
      Direction candidate =
        intersectionDirection.rotationByRadians(-1 * intersectionOffset);
      maxRotate = closer(origin, clockwise, candidate, maxRotate);
    }
    return maxRotate;
  }

  static Direction maxRotation(
    Point rotator,
    Point pivot,
    Direction target,
    boolean clockwise,
    Circle obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = new Direction(pivot, rotator);
    
    BasicCircle path = new BasicCircle(pivot, distance(pivot, rotator));
    Point[] intersections = intersection(path, new BasicCircle(obstacle));
    if (intersections == null) {
      return target;  // no collision
    }
    for (Point intersection : intersections) {
      maxRotate = closer(
        origin,
        clockwise,
        new Direction(pivot, intersection),
        maxRotate
      );
    }

    return maxRotate;
  }

  static Direction maxRotation(
    Point rotator,
    Point pivot,
    Direction target,
    boolean clockwise,
    Segment obstacle
  ) {
    Direction maxRotate = target;
    Direction origin = new Direction(pivot, rotator);

    BasicCircle path = new BasicCircle(pivot, distance(rotator, pivot));
    Point[] intersections = lineIntersection(path, obstacle);
    for (int i = 0; i < intersections.length; i++) {
      if (!obstacle.contains(intersections[i])) {
        continue;
      }
      maxRotate = closer(
        origin,
        clockwise,
        new Direction(pivot, intersections[i]),
        maxRotate
      );
    }

    return maxRotate;
  }

  static Direction closer(
    Direction origin,
    boolean clockwise,
    Direction s,
    Direction t
  ) {
    if (rotationalDistance(origin, clockwise, s) <
        rotationalDistance(origin, clockwise, t)) {
      return s;
    }
    return t;
  }

  static double rotationalDistance(
    Direction origin,
    boolean clockwise,
    Direction target
  ) {
    if (origin.equals(target)) {
      return 0.0;
    }
    double distance = target.toDegrees() - origin.toDegrees();
    if (distance < 0) {
      distance += 360;
    }
    if (clockwise) {
      distance = 360 - distance;
    }
    return distance;
  }

  static boolean offscreen(Point point) {
    if (
      point.getX() < 0 || point.getX() > Game.WIDTH ||
      point.getY() < 0 || point.getY() > Game.HEIGHT
    ) {
      return true;
    }

    return false;
  }

  // Returns NaN if the code for the given shape pair hasn't been written yet.
  static double distance(Shape s, Shape t) {
    if (touching(s, t)) {
      return 0.0;
    }
    if (s instanceof Circle) {
      if (t instanceof Circle) {
        return distance((Circle) s, (Circle) t);
      } else if (t instanceof ConvexPolygon) {
        return distance((Circle) s, (ConvexPolygon) t);
      } else {
        return Double.NaN;
      }
    } else if (s instanceof ConvexPolygon) {
      if (t instanceof Circle) {
        return distance((Circle) t, (ConvexPolygon) s);
      } else if (t instanceof ConvexPolygon) {
        return distance((ConvexPolygon) s, (ConvexPolygon) t);
      } else {
        return Double.NaN;
      }
    } else {
      return Double.NaN;
    }
  }

  static double distance(Circle s, Circle t) {
    double distance =
      distance(s.getCenter(), t.getCenter()) -
      (s.getRadius() + t.getRadius());
    return max(distance, 0);
  }

  // assumes not touching
  static double distance(Circle circle, ConvexPolygon poly) {
    double centerDistance = Double.POSITIVE_INFINITY;
    for (Segment side : poly.getSides()) {
      centerDistance = min(
        centerDistance,
        segmentDistance(side, circle.getCenter())
      );
    }
    double distance = centerDistance - circle.getRadius();
    return distance;
  }

  // assumes not touching
  static double distance(ConvexPolygon s, ConvexPolygon t) {
    double distance = Double.POSITIVE_INFINITY;
    for (Segment side : s.getSides()) {
      for (Point corner : t.getCorners()) {
        distance = min(distance, segmentDistance(side, corner));
      }
    }

    for (Segment side : t.getSides()) {
      for (Point corner : s.getCorners()) {
        distance = min(distance, segmentDistance(side, corner));
      }
    }

    return distance;
  }

  static double distance(Shape shape, Point point) {
    if (shape.contains(point)) {
      return 0.0;
    }

    if (shape instanceof Circle) {
      return distance((Circle) shape, point);
    } else if (shape instanceof ConvexPolygon) {
      return distance((ConvexPolygon) shape, point);
    } else {
      return Double.NaN;
    }
  }

  // assumes point is outside circle
  static double distance(Circle circle, Point point) {
    return distance(circle.getCenter(), point) - circle.getRadius();
  }

  // assumes point is outside poly
  static double distance(ConvexPolygon poly, Point point) {
    double distance = Double.POSITIVE_INFINITY;
    for (Segment side : poly.getSides()) {
      distance = min(distance, segmentDistance(side, point));
    }
    return distance;
  }

  static double segmentDistance(Segment seg, Point point) {
    Segment perp = perpendicularThrough(seg, point);
    if (seg.contains(perp.getEnd())) {
      return perp.length();
    } else {
      return min(
        distance(point, seg.getStart()),
        distance(point, seg.getEnd())
      );
    }
  }

  static double lineDistance(Segment seg, Point point) {
    return perpendicularThrough(seg, point).length();
  }

  static double distance(Point s, Point t) {
    return sqrt(sq(s.getX() - t.getX()) + sq(s.getY() - t.getY()));
  }
  
  static double cross(Vector s, Vector t) {
    return s.getXComponent() * t.getYComponent() -
           s.getYComponent() * t.getXComponent();
  }

  // NOTE: this returns NaN if the points are horizontally aligned
  static double slope(Point s, Point t) {
    return (s.getY() - t.getY()) / (s.getX() - t.getX());
  }

  static double fromCanvasX(double canvasX) {
    return canvasX;
  }

  static double fromCanvasY(double canvasY) {
    return Game.HEIGHT - canvasY;
  }

  static double sq(double x) {
    return x*x;
  }

  static boolean equals(double s, double t) {
    return Math.abs(s - t) < EPSILON;
  }

  static double hypoteneuse(double legA, double legB) {
    return sqrt(sq(legA) + sq(legB));
  }
}

class BasicCircle {
  Point center;
  double radius;

  BasicCircle(Point center, double radius) {
    this.center = center;
    this.radius = radius;
  }

  BasicCircle(Circle circle) {
    this.center = circle.getCenter();
    this.radius = circle.getRadius();
  }
}
