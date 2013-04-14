package Shapes;

import java.awt.*;
import java.util.*;

/**
 * A shape that appears on screen and interacts with other shapes.
 * <p>
 * Shapes are the basic building blocks of games built with the Shapes
 * framework. They can be of different colors and sizes, can move around the
 * screen, can collide with one another, and can say things, to name a few.
 * <p>
 * You won't use this class directly; you'll use one of its subclasses:
 * {@link Circle}, {@link Rectangle} or {@link Triangle}.
 *
 * @author Nate Sullivan
 * @version v0
 */
public abstract class Shape {
  private Color color;
  private boolean fill; 
  private boolean invisible; 
  private boolean solid;
  private String speech;
  private int speechDuration;
  private TextStyle speechStyle;
  private boolean destroyed;
  private Direction direction;
  private double speed;
  private Point center;
  
  public enum Border {
	  NONE, TOP, BOTTOM, 
	  LEFT, RIGHT, OFFSCREEN
  }

  /**
   * Initializes the Shape. When you subclass shape, you'll
   * override this method to do things like set the shape's color, set its
   * starting position, etc.
   */
  abstract public void setup();
  /**
   * Updates the shape once per frame. When you subclass shape, you'll
   * override this method to do things like move the shape, change its
   * size, etc.
   */
  abstract public void update();

  /**
   * Draws the shape to the canvas.
   */
  abstract void render(Graphics2D g);

  /**
   * Checks if this shape contains another given shape.
   *
   * @param  s  the shape that may be contained in this shape.
   * @return    true if s is entirely inside this shape, false otherwise.
   */
  abstract public boolean contains(Shape s);

  void renderSpeech(Graphics2D g) {
    if (!isSpeaking()) {
      return;
    }
    Point bottomLeft = new Point(getRight(), getTop());
    getSpeechStyle().renderString(
      getSpeech(),
      bottomLeft,
      TextStyle.ReferencePointLocation.BOTTOM_LEFT,
      g,
      getSpeechOrigin()
    );
  }

  Point getSpeechOrigin() {
    return
      (new Point(getRight(), getTop())).translation(new Vector(-5, -5));
  }

  /**
   * Checks if this shape contains a given point.
   *
   * @param p   a point to check for inside this shape.
   * @return    true if p is inside or on the border of this shape.
   */
  abstract public boolean contains(Point p);

  /**
   * Updates the shape automatically.
   *
   * This method moves the shape when it has a speed, etc.
   */
  void autoUpdate() {
    if (this.isSpeaking()) {
      speechDuration--;
    }
    if (Math.abs(speed) > Geometry.EPSILON) {
      move(getDirection(), speed);
    }
  }

  /**
   * Constructs a new shape with default values.
   * <p>
   * Overriding constructors should call super() to ensure that Game.addShape()
   * is called, and should call setup().
   */
  public Shape() {
    Game.addShape(this);
    Game.setLayer(this, 0);

    // set default values
    setColor(Color.BLACK);
    setFilled(true);
    TextStyle defaultSpeech = TextStyle.sansSerif();
    defaultSpeech.setBackgroundColor(Color.WHITE);
    setSpeechStyle(defaultSpeech);
    setDirection(Direction.RIGHT);
    destroyed = false;
  }

  /**
   * Returns which borders of the game window this shape is touching.
   *
   * @return  an array of {@link shapes.Game.Border}s representing the borders that
   *          this
   *          shape is touching. If the shape is entirely within the game
   *          window, returns an empty array. If the shape is entirely outside
   *          the game window, returns an array containing only
   *          {@link shapes.Game.Border#OFFSCREEN}.
   */
  public Game.Border[] touchingBorders() {
    if (isOffscreen()) {
      return new Game.Border[] { Game.Border.OFFSCREEN };
    }
    Set<Game.Border> touchingBorders = new HashSet<Game.Border>();
    for (Game.Border border : Game.Border.all()) {
      if (isTouching(border.getSegment())) {
        touchingBorders.add(border);
      }
    }
    return touchingBorders.toArray(new Game.Border[0]);
  }

  /**
   * Checks if this shape is touching a given segment.
   *
   * @param   seg the segment to check.
   * @return      <code>true</code> if this shape is touching 
   *              <code>seg</code>, <code>false</code> otherwise. Returns
   *              <code>false</code> if <code>seg</code> is <code>null</code>.
   */
  boolean isTouching(Segment seg) {
    if (seg == null) return false;
    if (isDestroyed()) return false;
    return Geometry.touching(this, seg);
  }

  /**
   * Checks if this shape is touching another shape.
   *
   * @param   s the shape to check.
   * @return    <code>true</code> if this shape is touching <code>s</code>,
   *            <code>false</code> otherwise.
   *            Returns <code>false</code> if <code>s</code> is
   *            <code>null</code>.
   */
  public boolean isTouching(Shape s) {
    if (s == null) return false;
    if (isDestroyed() || s.isDestroyed()) {
      return false;
    }
    return Geometry.touching(this, s);
  }

  /**
   * Checks whether this shape is entirely offscreen.
   *
   * @return  <code>true</code> if this shape is entirely outside the game
   *          window, <code>false</code> if not.
   * @see     #isTouchingBorder
   */
  abstract public boolean isOffscreen();

  /**
   * Checks if this shape is touching the border of the window.
   *
   * @return  true if any part of the shape is touching the border of the
   *          window, or if the shape is entirely outside the window, and false
   *          otherwise.
   * @see     #isOffscreen
   * @see     #touchingBorders
   */
  public boolean isTouchingBorder() {
    if (isOffscreen()) {
      return true;
    }
    for (Segment border : Game.getBorders()) { 
      if (isTouching(border)) {
        return true;
      }
    }
    return false;
  }
  
  public Border isTouchingWhichBorder() {
	  if(isOffscreen()) return Shape.Border.OFFSCREEN;
	  Segment border[] = Game.getBorders();
	  if(isTouching(border[0])) return Shape.Border.TOP;
	  else if(isTouching(border[1])) return Shape.Border.RIGHT;
	  else if(isTouching(border[2])) return Shape.Border.BOTTOM;
	  else if(isTouching(border[3])) return Shape.Border.LEFT;
	  return Shape.Border.NONE;
  }

  /**
   * Finds the farthest this shape can go towards a target with an obstacle in
   * the way.
   * <p>
   * If this shape tries to move to <code>target</code>, then
   * <code>obstacle</code> may get in its way. Finds the farthest this shape
   * can move towards <code>target</code>, and returns the location where the
   * shape's center would be if it advanced as far as possible.
   *
   * @param   target    the point towards which this shape is moving.
   * @param   obstacle  another shape which may obstruct this shape's movement.
   * @return            a point representing the farthest this shape's center
   *                    can move towards <code>target</code>.
   */
  abstract Point maxMovement(Point target, Shape obstacle);
  // nate: move code here

  // nate:
  abstract Point maxMovement(Point target, Segment obstacle);

  private Direction maxRotation(
      Direction target,
      boolean clockwise,
      Shape obstacle
  ) {
    Direction maxRotation = Geometry.maxRotation(
      this,
      target,
      clockwise,
      obstacle
    );
    return maxRotation;
  }

  private Direction maxRotation(
    Direction target,
    boolean clockwise,
    Segment obstacle
  ) {
    return
      Geometry.maxRotation(
        this,
        target,
        clockwise,
        obstacle
      );
  }

  /**
   * Moves in the shape's direction.
   * <p>
   * Moves <code>pixels</code> pixels in the direction set using
   * {@link #setDirection(Direction)}. Won't move through solid shapes, and
   * won't move if direction hasn't been set.
   *
   * @param   pixels  the distance to move.
   * @see     #move(Direction, double)
   * @see     #setDirection(Direction)
   */
  public void move(double pixels) {
    move(getDirection(), pixels);
  }

  /**
   * Moves in the given direction.
   * <p>
   * Moves <code>pixels</code> pixels in the given direction.
   * Won't move through solid shapes.
   *
   * @param   pixels    the distance to move.
   * @param   direction the direction in which to move. Won't move if
   *                    <code>null</code>.
   * @see     #move(double)
   */
  public void move(Direction direction, double pixels) {
    if (direction == null || Math.abs(pixels) < Geometry.EPSILON) {
      return;
    }
    Point end = getCenter().translation(new Vector(direction, pixels));
    Point maxMovement = end;
    Shape[] obstacles;
    if (isSolid()) {
      obstacles = Game.getAllShapes();
    } else {
      obstacles = Game.getSolids();
    }
    for (Shape obstacle : obstacles) {
      if (obstacle == this) continue;
      Point blockedEnd = this.maxMovement(end, obstacle);
      if (Geometry.distance(getCenter(), blockedEnd) < Geometry.distance(getCenter(), maxMovement)) {
        maxMovement = blockedEnd;
      }
    }
    if (Game.getBorderBehavior() == Game.BorderBehavior.SOLID ||
        Game.getBorderBehavior() == Game.BorderBehavior.BOUNCE
    ) {
      for (Segment border: Game.getBorders()) {
        Point blockedEnd = this.maxMovement(end, border);
        if (Geometry.distance(getCenter(), blockedEnd) < Geometry.distance(getCenter(), maxMovement)) {
          maxMovement = blockedEnd;
        }
      }
    }
    setCenter(maxMovement);
    if (Game.getBorderBehavior() == Game.BorderBehavior.BOUNCE) {
      bounce();
    }
  }

  private void bounce() {
    Game.Border[] borders = touchingBorders();
    if (borders.length == 0 || borders[0] == Game.Border.OFFSCREEN) {
      return;
    }
    if (getDirection() == null) return;

    // We assume that, with these old limits, this shape is not "stuck" on the
    // border. Rotation may cause the shape to get "stuck," so we'll
    // translate it back to these old limits after rotation.
    double[] oldLimit = new double[borders.length];
    for (int i = 0; i < borders.length; i++) {
      oldLimit[i] = getLimit(borders[i].getDirection());
    }

    // set new direction
    for (Game.Border border : borders) {
      if (!getDirection().isWithinQuarterTurnOf(
          border.getDirection())
      ) {
        continue;
      }
      Vector old = new Vector(getDirection(), 1);
      Vector newVector = null;
      switch (border) {
        case LEFT:
        case RIGHT:
          newVector = new Vector(
            -1 * old.getXComponent(),
            old.getYComponent()
          );
          break;
        case TOP:
        case BOTTOM:
          newVector = new Vector(
            old.getXComponent(),
            -1 * old.getYComponent()
          );
          break;
      }
      setDirection(newVector.getDirection());
    }

    // Translate shape back to old limits to make sure it doesn't get stuck.
    for (int i = 0; i < borders.length; i++) {
      double change = getLimit(borders[i].getDirection()) - oldLimit[i];
      Direction correctionDirection = borders[i].getDirection();
      if (borders[i] == Game.Border.RIGHT || borders[i] == Game.Border.TOP) {
        // nate: nicer way to do this?
        correctionDirection = correctionDirection.reverse();
      }
      setCenter(getCenter().translation(correctionDirection, change));
    }
  }

  /**
   * Checks if this shape was clicked since the previous frame.
   *
   * Returns true if:
   * <ul>
   *  <li>the mouse button was clicked inside this shape since the last
   *  frame</li>
   *  <li>the mouse was dragged into this shape while the mouse button was
   *  down, and is currently inside this shape</li>
   * </ul>
   *
   * @return  true if this shape was clicked since the previous frame, false otherwise.
   * @see     Mouse#clickLocation()
   */
  public boolean isClicked() {
    if (Mouse.clickLocation() == null) {
      return false;
    }

    return this.contains(Mouse.clickLocation());
  }

  /**
   * Moves right.
   * <p>
   * Moves <code>pixels</code> pixels to the right.
   * Won't move through solid shapes.
   *
   * @param   pixels  the distance to move.
   * @see     #move(Direction, double)
   */
  public void moveRight(double pixels) {
    move(Direction.RIGHT, pixels);
  }

  /**
   * Moves left.
   * <p>
   * Moves <code>pixels</code> pixels to the left.
   * Won't move through solid shapes.
   *
   * @param   pixels  the distance to move.
   * @see     #move(Direction, double)
   */
  public void moveLeft(double pixels) {
    move(Direction.LEFT, pixels);
  }

  /**
   * Moves up.
   * <p>
   * Moves <code>pixels</code> pixels to the up.
   * Won't move through solid shapes.
   *
   * @param   pixels  the distance to move.
   * @see     #move(Direction, double)
   */
  public void moveUp(double pixels) {
    move(Direction.UP, pixels);
  }
  
  /**
   * Moves down.
   * <p>
   * Moves <code>pixels</code> pixels to the down.
   * Won't move through solid shapes.
   *
   * @param   pixels  the distance to move.
   * @see     #move(Direction, double)
   */
  public void moveDown(double pixels) {
    move(Direction.DOWN, pixels);
  }

  /**
   * Display text near this shape.
   * <p>
   * Continues speaking until this method is called again with new text. Stops
   * speaking if <code>speech</code> is <code>null</code>.
   *
   * @param   speech  what the shape says.
   * @see     #say(String, int)
   * @see     #setSpeechStyle(TextStyle)
   * @see     #getSpeech()
   * @see     #isSpeaking()
   */
  public void say(String speech) {
    if (speech == null) {
      this.speechDuration = 0;
      return;
    }
    this.speech = speech;
    this.speechDuration = -1;
  }
  
  /**
   * Displays text near this shape for a limited time. Stops speaking if
   * <code>speech</code> is <code>null</code>.
   * 
   * @param   speech  what the shape says.
   * @param   frames  how long the shape speaks.
   * @see     #say(String)
   * @see     #setSpeechStyle(TextStyle)
   * @see     #getSpeech()
   * @see     #isSpeaking()
   */
  public void say(String speech, int frames) {
    if (speech == null) {
      this.speechDuration = 0;
      return;
    }
    if (frames < 0) {
      throw new IllegalArgumentException("frames must be non-negative.");
    }
    this.speech = speech;
    this.speechDuration = frames;
  }

  /**
   * Returns what this shape is saying.
   *
   * @return  what this shape is saying, or <code>null</code> if the shape
   *          isn't saying anything.
   * @see     #say(String, int)
   * @see     #say(String)
   */
  public String getSpeech() {
    if (isSpeaking()) {
      return speech;
    } else {
      return null;
    }
  }

  /**
   * Sets the visual style of this shape's speech text. The speech's size, font,
   * color and bold/italics can be customized with the passed {@link TextStyle}
   * object.
   *
   * @param   speechStyle the styling of this shape's speech.
   * @see     #getSpeechStyle()
   */
  public void setSpeechStyle(TextStyle speechStyle) {
    if (speechStyle == null) {
      throw new IllegalArgumentException("speechStyle must not be null.");
    }
    this.speechStyle = speechStyle;
  }

  /**
   * Returns information about the visual styling of this shape's speech text.
   *
   * @return  the style of this shape's speech text.
   * @see     #setSpeechStyle(TextStyle)
   */
  public TextStyle getSpeechStyle() {
    return speechStyle;
  }

  /**
   * Returns true if the shape is currently speaking.
   *
   * @return  true if the shape is speaking, false otherwise.
   * @see     #say(String, int)
   * @see     #say(String)
   * @see     #getSpeech()
   */
  public boolean isSpeaking() {
    return speechDuration != 0;
  }

  /**
   * Returns the direction of a given point relative to this shape.
   * <p>
   * Can be used to {@link #move} this shape towards a point:
   * <code>this.move(this.towards(targetPoint), 10.0);</code>
   *
   * @param   target  the point to aim towards.
   * @return          the direction of <code>target</code> relative to this
   *                  shape, or null if <code>target</code> is null.
   * @see             #towards(Shape)
   * @see             #move(Direction, double)
   * @see             #setDirection(Direction)
   */
  public Direction towards(Point target) {
    if (target == null) return null;
    Vector v = new Vector(this.getCenter(), target);
    return v.getDirection();
  }

  /**
   * Returns the direction of a shape relative to this shape.
   * <p>
   * Can be used to {@link #move} this shape towards another shape:
   * <code>this.move(this.towards(targetShape), 10.0);</code>
   *
   * @param   target  the shape to aim towards.
   * @return          the direction of <code>target</code>'s relative to this
   *                  shape's center, or <code>null</code> if
   *                  <code>target</code> is <code>null</code>.
   * @see             #towards(Point)
   * @see             #move(Direction, double)
   * @see             #setDirection(Direction)
   */
  public Direction towards(Shape target) {
    return towards(target.getCenter());
  }

  /**
   * Returns the distance between this shape and another shape.
   *
   * @param target  the shape whose distance away to find.
   * @return  distance in pixels from this shape to <code>target</code>.
   */
  public double distanceTo(Shape target) {
    return Geometry.distance(this, target);
  }

  /**
   * Returns the distance between this shape and a point.
   *
   * @param target  the point whose distance away to find.
   * @return  distance in pixels from this shape to <code>target</code>.
   */
  public double distanceTo(Point target) {
    return Geometry.distance(this, target);
  }

  /**
   * Destroys this shape so it will no longer be rendered. Call this method
   * when you have finished using the shape.
   * <p>
   * A destroyed shape will not appear on the screen and will not interact with
   * other shapes. A destroyed shape cannot be undestroyed. Continuing to call
   * this shape's methods after it has been destroyed has undefined results.
   */
  public void destroy() {
    // nate: who remove the same from Game?
    destroyed = true;
  }

  /**
   * Returns true if this shape has been destroyed.
   *
   * @return  true if {@link #destroy()} has been called on this shape,
   *          false otherwise.
   */
  public boolean isDestroyed() {
    return destroyed;
  }

  /**
   * Rotate this shape's direction. Passing a negative value to 
   * <code>degrees</code> causes a clockwise rotation.
   *
   * @param degrees the number of degrees by which to rotate.
   */
  public void rotate(double degrees) {
    if (Math.abs(degrees) < Geometry.EPSILON) return;
    Direction target = getDirection().rotation(degrees);
    boolean clockwise = degrees < 0;
    Direction maxRotate = target;
    Shape[] obstacles;
    if (isSolid()) {
      obstacles = Game.getAllShapes();
    } else {
      obstacles = Game.getSolids();
    }
    for (Shape obstacle : obstacles) {
      if (obstacle == this) continue;
      Direction blockedRotate =
        maxRotation(target, clockwise, obstacle);
      maxRotate = Geometry.closer(
        getDirection(),
        clockwise,
        blockedRotate,
        maxRotate
      );
    }
    if (Game.getBorderBehavior() == Game.BorderBehavior.SOLID ||
        Game.getBorderBehavior() == Game.BorderBehavior.BOUNCE
    ) {
      for (Segment border: Game.getBorders()) {
        Direction blockedEnd =
          this.maxRotation(target, clockwise, border);
        maxRotate = Geometry.closer(
          getDirection(),
          clockwise,
          blockedEnd,
          maxRotate
        );
      }
    }
    setDirection(maxRotate);
    if (Game.getBorderBehavior() == Game.BorderBehavior.BOUNCE) {
      bounce();
    }
  }

  /**
   * Set this shape's z-axis layer.
   * <p>
   * When two shapes overlap, the one in the higher layer will be displayed
   * on top of the one in the lower layer. All shapes start at layer 0 by
   * default. A shape's layer only affects its appearance--shapes in
   * different layers still interact with one another.
   *
   * @param layer layer along the z-axis this shape will be contained in.
   */
  public void setLayer(int layer) {
    if (isDestroyed()) return;
    Game.setLayer(this, layer);
  }

  /**
   * Get this shape's z-axis layer.
   * <p>
   * When two shapes overlap, the one in the higher layer will be displayed
   * on top of the one in the lower layer. All shapes start at layer 0 by
   * default. A shape's layer only affects its appearance--shapes in
   * different layers still interact with one another.
   *
   * @return layer along the z-axis this shape is contained in.
   */
  public int getLayer() {
    if (isDestroyed()) return -1;
    return Game.getLayerOf(this);
  }

  // Getters & setters

  /**
   * Set whether this shape is filled or outlined.
   * <p>
   * A filled shape has a colored interior, and a non-filled shape has a
   * colored outline but a transparent interior. Fill affects how the shape
   * appears but not its functionality.
   *
   * @param fill  true for fill, false for outline.
   */
  public void setFilled(boolean fill) {
    this.fill = fill;
  }

  /**
   * Returns whether this shape is filled or outlined.
   * <p>
   * A filled shape has a colored interior, and a non-filled shape has a
   * colored outline but a transparent interior. Fill affects how the shape
   * appears but not its functionality.
   *
   * @return  true if filled, false if outline.
   */
  public boolean isFilled() {
    return fill;
  }

  /**
   * Set this shape's visibility.
   * <p>
   * Invisible shapes won't appear on the canvas, but still exist and will
   * interact with other shapes.
   *
   * @param invisible true for invisible, false for visible.
   */
  public void setInvisible(boolean invisible) {
    this.invisible = invisible;
  }

  /**
   * Returns whether this shape is invisible.
   * <p>
   * Invisible shapes won't appear on the canvas, but still exist and will
   * interact with other shapes.
   *
   * @return  true if invisible, false if visible.
   */
  public boolean isInvisible() {
    return invisible;
  }
  
  /**
   * Set this shape's color.
   *
   * @param color the color the shape will be drawn in.
   */
  public void setColor(Color color) {
    if (color == null) {
      throw new IllegalArgumentException("color must not be null.");
    }
    this.color = color;
  }

  /**
   * Get this shape's color.
   *
   * @return  the color the shape is drawn in.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Set whether other shapes can overlap with this shape.
   * <p>
   * An example of a solid shape is a wall that other shapes can't pass
   * through.
   * <p>
   * No shape can overlap with a solid shape. A non-solid shape can overlap
   * with other non-solid shapes.
   *
   * @param solid true for a shape that can't be overlapped, false for a shape
   *              that can be.
   */
  public void setSolid(boolean solid) {
    if (this.solid == solid) {
      return;
    }

    if (solid) {
      Game.addSolid(this);
    } else {
      Game.removeSolid(this);
    }

    this.solid = solid;
  }

  /**
   * Get whether other shapes can overlap with this shape.
   * <p>
   * An example of a solid shape is a wall that other shapes can't pass
   * through.
   * <p>
   * No shape can overlap with a solid shape. A non-solid shape can overlap
   * with other non-solid shapes.
   *
   * @return true for a shape that can't be overlapped, false for a shape
   *         that can be.
   */
  public boolean isSolid() {
    return solid;
  }

  /**
   * Set the direction this shape is facing.
   *
   * A shape's direction determines:
   * <ul>
   *  <li>The orientation in which the shape is drawn.</li>
   *  <li>
   *    The direction the shape will move in calls to {@link #move(double)}.
   *    (To move in other directions, see
   *    {@link #move(Direction, double)}).
   *   </li>
   *  <li>
   *    The direction the shape will move if it has a speed (see
   *    {@link #setSpeed(double)}).
   *   </li>
   * </ul>
   *
   * If this shape's direction is set to <code>null</code>, it will be drawn
   * facing to the right,
   * and calls to {@link #move(double)} will not move the shape.
   *
   * @param  direction the direction the shape will face.
   */
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * Get the direction this shape is facing.
   *
   * A shape's direction determines:
   * <ul>
   *  <li>The orientation in which the shape is drawn.</li>
   *  <li>
   *    The direction the shape will move in calls to {@link #move(double)}.
   *    (To move in other directions, see
   *    {@link #move(Direction, double)}).
   *   </li>
   *  <li>
   *    The direction the shape will move if it has a speed (see
   *    {@link #setSpeed(double)}).
   *   </li>
   * </ul>
   *
   * @return  the direction the shape is facing.
   */
  public Direction getDirection() {
    return direction;
  }

  /**
   * Set the shape's speed in pixels per frame.
   * <p>
   * This shape will automatically advance every frame in the direction set
   * using {@link #setDirection(Direction)}.
   *
   * @param speed the number of pixels to move each frame.
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Get the shape's speed in pixels per frame.
   * <p>
   * This shape automatically advances every frame in the direction set
   * using {@link #setDirection(Direction)}.
   *
   * @return the number of pixels this shape moves each frame.
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Get the location of the shape's center.
   *
   * @return  a point representing the shape's center.
   */
  public Point getCenter() {
    return center;
  }
  
  /**
   * Changes the location of this shape.
   * <p>
   * Note that this method will allow you to relocate this shape into
   * a solid shape. Moving a shape into a solid shape has undefined behavior.
   * (If you haven't called {@link #setSolid(boolean)}, you don't have
   * to worry about this.) To move this shape without going through solid
   * shapes, use {@link #move}.
   *
   * @param x the x-coordinate of this shape's new center
   * @param y the y-coordinate of this shape's new center
   */
  public void setCenter(double x, double y) {
    setCenter(new Point(x, y));
  }

  /**
   * Changes the location of this shape.
   * <p>
   * Note that this method will allow you to relocate this shape into
   * a solid shape. Moving a shape into a solid shape has undefined behavior.
   * (If you haven't called {@link #setSolid(boolean)}, you don't have
   * to worry about this.) To move this shape without going through solid
   * shapes, use {@link #move}.
   *
   * @param center  a point representing the location of the shape's new center.
   */
  public void setCenter(Point center) {
    if (center == null) {
      throw new IllegalArgumentException("center must not be null.");
    }
    this.center = center;
  }

  /**
   * Returns the x-value of the rightmost point in this shape.
   *
   * @return  x-value of the rightmost point in this shape.
   */
  abstract public double getRight();
  /**
   * Returns the y-value of the highest point in this shape.
   *
   * @return  y-value of the highest point in this shape.
   */
  abstract public double getTop();
  /**
   * Returns the x-value of the leftmost point in this shape.
   *
   * @return  x-value of the leftmost point in this shape.
   */
  abstract public double getLeft();
  /**
   * Returns the y-value of the lowest point in this shape.
   *
   * @return  y-value of the lowest point in this shape.
   */
  abstract public double getBottom();
  double getLimit(Direction d) {
    if (d.equals(Direction.RIGHT)) return getRight();
    if (d.equals(Direction.UP)) return getTop();
    if (d.equals(Direction.LEFT)) return getLeft();
    if (d.equals(Direction.DOWN)) return getBottom();
    return 0;
  }

  /**
   * Returns a string representing this shape.
   *
   * @return  a string representing this shape.
   */
  @Override
  public String toString() {
    return "Shape with center at " + center.toString();
  }
}
