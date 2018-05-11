package Shapes;

import java.awt.*;
import java.applet.*;
import javax.swing.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * A game built using the Shapes framework.
 * <p>
 * <code>Game</code> is one of the two most important classes (along with
 * {@link Shape}) for building a game using the Shapes framework. To make
 * a game, you will subclass <code>Game</code> and override the
 * {@link #setup()} and {@link #update()} functions. In <code>setup()</code>,
 * you will write code that will execute when your game begins, and in
 * <code>update()</code> you will write code that executes once per frame.
 * <p>
 * <code>Game</code> also has a few useful static methods and constants. For
 * example, you can call <code>Game.getAllShapes()</code> to get all the shapes
 * that currently exist in your game. (In case you haven't encountered static
 * methods before: if you see a method named <code>xx</code> listed as
 * <code>static</code> below, then you can call it with
 * <code>Game.xx()</code>.)
 */
public abstract class Game extends Applet {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private static JFrame frame;
  private static Canvas canvas;
  private static boolean applet;
  private static Set<Shape> solidShapes;
  private static Set<Shape> allShapes;

  private static java.util.List<Counter> counters;
  private static TextStyle counterStyle;

  private static Map<Integer, java.util.List<Shape>> layerContents;
  private static java.util.List<Integer> layers;
  private static Map<Shape, Integer> layerOf;

  private static String title;
  private static TextStyle titleStyle;
  private static int titleDuration;

  private static String subtitle;
  private static TextStyle subtitleStyle;
  private static int subtitleDuration;

  /**
   * Represents the ways in which shapes can react to reaching the edge of the
   * game window. Enumeration values are:
   * <ul>
   *  <li><code>NONE</code> allows shapes to leave the game window.</li>
   *  <li><code>SOLID</code> prevents shapes from leaving the game window.</li>
   *  <li><code>BOUNCE</code> makes shapes "bounce" off the edges of the game
   *  window by changing their direction.</li>
   * </ul>
   * <strong>Example usage:</strong>
   * <p>
   * <code>
   *  Game.setBorderBehavior(Game.BorderBehavior.SOLID);
   * </code>
   */
  public enum BorderBehavior { NONE, SOLID, BOUNCE };
  private static BorderBehavior borderBehavior;

  /**
   * The height of the game window in pixels.
   */
  public static final int HEIGHT = 800;
  /**
   * The width of the game window in pixels.
   */
  public static final int WIDTH = 1400;
  
  /**
   * Constructs a new game.
   *
   * @param web <code>true</code> if this game is meant to be an applet (which
   *            can be played online), and <code>false</code> otherwise. Note:
   *            this doesn't work yet.
   */
  public Game() {
    Game.applet = false;
    canvas = new Canvas(this);
    solidShapes =
      Collections.newSetFromMap(new ConcurrentHashMap<Shape, Boolean>());
    allShapes = 
      Collections.newSetFromMap(new ConcurrentHashMap<Shape, Boolean>());

    // TODO: sort out which data structures actually have to support concurrency
    layerContents = new ConcurrentHashMap<Integer, java.util.List<Shape>>();
    layers = new CopyOnWriteArrayList<Integer>();
    layerOf = new ConcurrentHashMap<Shape, Integer>();

    counters = new ArrayList<Counter>();

    Mouse mouse = new Mouse();
    if (applet) {
      addMouseMotionListener(mouse);
      addMouseListener(mouse);
      addKeyListener(new Keyboard());
    } else {
      frame = new JFrame();
      frame.addMouseMotionListener(mouse);
      frame.addMouseListener(mouse);
      frame.addKeyListener(new Keyboard());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    setDefaults();
  }

  private void setDefaults() {
    setBackgroundColor(Color.BLUE);
    setBorderBehavior(BorderBehavior.NONE);

    TextStyle titleStyle = TextStyle.sansSerif();
    titleStyle.setFontSize(40);
    setTitleStyle(titleStyle);
    setCounterStyle(TextStyle.sansSerif());
    setSubtitleStyle(TextStyle.sansSerif());
  }

  /**
   * You can ignore this method. This method is used internally to initialize
   * this game as an applet.
   */
  public void init() {
    ready();
  }
  
  /**
   * You can ignore this method. This method gets called by the subclass's
   * constructor when it has finished initializing, but that call is already
   * written in <code>MyGame.java</code>.
   */
  protected void ready() {
    if (applet) {
      add(canvas);
    } else {
      frame.add(canvas);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setResizable(false);
      frame.setVisible(true);
    }
  }

  /**
   * This method gets called when the game begins, and you should override it
   * in your subclass. Put any code here that you want to execute at the
   * beginning of the game and never again, such as creating shapes and setting
   * background color.
   */
  public void setup() {
  }

  /**
   * This method gets called once per frame, and you should override it
   * in your subclass. Put any code here that you want to execute every frame
   * to update the state of the game, such as moving shapes and making shapes
   * say things.
   */
  public void update() {
  }

  void autoUpdate() {
    if (hasTitle()) {
      return;
    }

    for (Shape s : allShapes) {
      s.autoUpdate();
    }

    Iterator<Shape> iter = allShapes.iterator();
    while (iter.hasNext()) {
      Shape s = iter.next();
      s.update();
      if (s.isDestroyed()) {
        if (s.isSolid()) {
          removeSolid(s);
        }
        removeFromLayers(s);
        iter.remove();
      }
    }
  }

  static Shape[] getSolids() {
    return solidShapes.toArray(new Shape[0]);
  }

  static void addSolid(Shape shape) {
    solidShapes.add(shape);
  }

  static void removeSolid(Shape shape) {
    solidShapes.remove(shape);
  }

  static void addShape(Shape shape) {
    allShapes.add(shape);
  }

  static void removeShape(Shape shape) {
    allShapes.remove(shape);
    removeFromLayers(shape);
  }

  /**
   * Returns an array of all shapes currently in the game.
   *
   * @return  an array of all shapes currently in the game.
   */
  public static Shape[] getAllShapes() {
    return allShapes.toArray(new Shape[0]);
  }

  static void removeFromLayers(Shape shape) {
    if (!layerOf.containsKey(shape)) return;

    int oldLayer = layerOf.get(shape);
    layerContents.get(oldLayer).remove(shape);
    if (layerContents.get(oldLayer).isEmpty()) {
      layerContents.remove(oldLayer);
      layers.remove((Integer) oldLayer);
    }
    layerOf.remove(shape);
  }

  /**
   * Set the layer that a given shape will be displayed in. Since the shapes
   * in this game are two dimensional, it must be decided which will appear "on
   * top" when two shapes overlap. The shape in the higher layer will appear
   * on top.
   * <p>
   * Setting a shape's layer will only affect how it is displayed; a shape's
   * layer has no effect on how it interacts with other shapes. (For example,
   * two shapes can touch even if they are in different layers. See
   * {@link Shape#isTouching(Shape)}.)
   *
   * @param shape the shape whose layer is being set.
   * @param layer the layer into which this shape will be moved.
   */
  static void setLayer(Shape shape, int layer) {
    removeFromLayers(shape);

    // add new stuff
    if (!layerContents.containsKey(layer)) {
      layerContents.put(layer, new CopyOnWriteArrayList<Shape>());
      int insertionPoint = ~Collections.binarySearch(layers, layer);
      layers.add(insertionPoint, layer);
    }
    layerContents.get(layer).add(shape);
    layerOf.put(shape, layer);
  }

  static java.util.List<Shape> getLayerContents(int layer) {
    return layerContents.get(layer);
  }

  /**
   * Get the layer that the given shape is in. See {@link #setLayer} for more
   * information about layers.
   *
   * @param shape the shape whose layer index will be returned.
   * @return      the index of the layer that <code>shape</code> is contained
   *              in.
   * @see         #setLayer
   */
  static int getLayerOf(Shape shape) {
    return layerOf.get(shape);
  }

  static java.util.List<Integer> getLayers() {
    return layers;
  }

  static void addCounter(Counter counter) {
    counters.add(counter);
  }

  static void removeCounter(Counter counter) {
    counters.remove(counter);
  }

  /**
   * Set the text style of all {@link Counter} objects. All counters share
   * one text style.
   *
   * @param counterStyle  the new visual style of this game's counters.
   * @see   #getCounterStyle
   */
  public static void setCounterStyle(TextStyle counterStyle) {
    if (counterStyle == null) {
      throw new IllegalArgumentException("counterStyle must not be null.");
    }
    Game.counterStyle = counterStyle;
  }

  /**
   * Returns the text style of all {@link Counter} objects. All counters share
   * one text style.
   *
   * @return  the visual style of this game's counters.
   * @see     #setCounterStyle
   */
  public static TextStyle getCounterStyle() {
    return Game.counterStyle;
  }

  /**
   * Returns the background color.
   *
   * @return  the background color.
   * @see     #setBackgroundColor
   */
  public static Color getBackgroundColor() {
    return canvas.getBackground();
  }

  /**
   * Sets the background color.
   *
   * @param backgroundColor the new background color.
   * @see     #getBackgroundColor
   */
  public static void setBackgroundColor(Color backgroundColor) {
    if (backgroundColor == null) {
      throw new IllegalArgumentException("backgroundColor must not be null.");
    }
    canvas.setBackground(backgroundColor);
  }

  /**
   * Returns four points representing the corners of the window.
   *
   * @return  the corners of the window.
   */
  public static Point[] getCorners() {
    return canvas.getCorners();
  }

  static Segment[] getBorders() {
    return canvas.getBorders();
  }

  // not public because no user-defined methods should be executing while
  // a title is displayed.
  boolean hasTitle() {
    return titleDuration > 0;
  }

  /**
   * Return whether a subtitle is currently being displayed.
   *
   * @return  <code>true</code> if a subtitle is currently being displayed,
   *          <code>false</code> if not.
   * @see     #setSubtitle
   * @see     #getSubtitle
   */
  public static boolean hasSubtitle() {
    return subtitleDuration > 0 || subtitleDuration == -1;
  }

  /**
   * Set the visual style of the game's title. See {@link #setTitle} for more
   * about titles.
   *
   * @param titleStyle  the new visual style of the game's title.
   * @see   #setTitle
   * @see   #getTitleStyle
   */
  public static void setTitleStyle(TextStyle titleStyle) {
    if (titleStyle == null) {
      throw new IllegalArgumentException("titleStyle must not be null.");
    }
    Game.titleStyle = titleStyle;
  }

  /**
   * Returns the visual style of the game's title. See {@link #setTitle} for more
   * about titles.
   *
   * @return  the visual style of the game's title.
   * @see     #setTitle
   * @see     #setTitleStyle
   */
  public static TextStyle getTitleStyle() {
    return titleStyle;
  }

  /**
   * Displays the given title in the game window. Displays <code>title</code>
   * in the center of the screen. While the title is being displayed, no shapes
   * or other text will be displayed, and no <code>update()</code> functions
   * will be called. To display text while other things are displayed, see
   * {@link #setSubtitle} or {@link Shape#say}.
   *
   * @param title     the text to display.
   * @param duration  the number of frames the title will be displayed.
   */
  public static void setTitle(String title, int duration) {
    if (duration < 0) {
      throw new IllegalArgumentException("Duration cannot be negative.");
    }
    if (title == null) {
      throw new IllegalArgumentException("Title cannot be null.");
    }
    Game.title = title;
    Game.titleDuration = duration;
  }

  void renderTitle(Graphics2D g) {
    titleDuration--;
    titleStyle.renderString(
      title,
      new Point(WIDTH / 2.0, HEIGHT / 2.0),
      TextStyle.ReferencePointLocation.CENTER,
      g,
      null
    );
  }

  /**
   * Sets the visual style of the game's subtitles. See {@link #setSubtitle}
   * for more about subtitles.
   *
   * @param subtitleStyle the new visual style of the game's subtitles.
   * @see   #setSubtitle
   * @see   #getSubtitleStyle
   */
  public static void setSubtitleStyle(TextStyle subtitleStyle) {
    if (subtitleStyle == null) {
      throw new IllegalArgumentException("subtitleStyle must not be null.");
    }
    Game.subtitleStyle = subtitleStyle;
  }

  /**
   * Returns the visual style of the game's subtitles. See {@link #setSubtitle}
   * for more about subtitles.
   *
   * @return  the visual style of the game's subtitles.
   * @see     #setSubtitle
   * @see     #setSubtitleStyle
   */
  public static TextStyle getSubtitleStyle() {
    return subtitleStyle;
  }

  /**
   * Displays the given subtitle for a given duration. Displays
   * <code>subtitle</code> centered in the bottom of the screen. Only one
   * subtitle can be displayed at a time.
   *
   * @param subtitle  the text to display. Won't display any subtitle if 
   *                  <code>subtitle</code> is <code>null</code>.
   * @param duration  the number of frames the subtitle will be displayed.
   * @see   #setSubtitle(String)
   * @see   #getSubtitle
   */
  public static void setSubtitle(String subtitle, int duration) {
    if (subtitle == null) {
      duration = 0;
    }
    if (duration < 0) {
      throw new IllegalArgumentException("Duration cannot be negative.");
    }
    Game.subtitle = subtitle;
    Game.subtitleDuration = duration;
  }

  /**
   * Displays the given subtitle until it is cleared. Displays
   * <code>subtitle</code> centered in the bottom of the screen. Only one
   * subtitle can be displayed at a time. To clear the subtitle, either set
   * a new subtitle or set it to <code>null</code>.
   *
   * @param subtitle  the text to display. Won't display any subtitle if 
   *                  <code>subtitle</code> is <code>null</code>.
   * @see   #setSubtitle(String, int)
   * @see   #getSubtitle
   */
  public static void setSubtitle(String subtitle) {
    if (subtitle == null) {
      Game.subtitleDuration = 0;
    } else {
      Game.subtitleDuration = -1;
    }
    Game.subtitle = subtitle;
  }

  /**
   * Returns the subtitle currently being displayed.
   *
   * @return  the subtitle currently being displayed, or <code>null</code> if
   *          no subtitle is being displayed.
   * @see     #setSubtitle
   */
  public static String getSubtitle() {
    if (!hasSubtitle()) {
      return null;
    }

    return subtitle;
  }

  void renderSubtitle(Graphics2D g) {
    subtitleDuration--;
    subtitleStyle.renderString(
      subtitle,
      new Point(WIDTH / 2.0, 30),
      TextStyle.ReferencePointLocation.BOTTOM_CENTER,
      g,
      null
    );
  }

  void renderCounters(Graphics2D g) {
    Counter.renderCounters(counters, counterStyle, g);
  }

  /**
   * Set the behavior of shapes when they reach the game window's borders.
   * The options are:
   * <ul>
   *  <li><code>NONE</code>: allow shapes to leave the window.</li>
   *  <li><code>SOLID</code>: prevent shapes from leaving the window. (See
   *  {@link Shape#setSolid}.)</li>
   *  <li><code>BOUNCE</code>: make shapes bounce when they hit the window
   *  borders, i.e., reverse direction as if bouncing off a physical
   *  border.</li>
   * </ul>
   * <p>
   * Usage example:
   * <code>
   *  Game.setBorderBehavior(Game.BorderBehavior.SOLID);
   * </code>
   *
   * @param behavior  specifies how shapes will act when they reach the border
   *                  of the window.
   */
  public static void setBorderBehavior(BorderBehavior behavior) {
    if (behavior == null) {
      throw new IllegalArgumentException("behavior must not be null.");
    }
    Game.borderBehavior = behavior;
  }

  /**
   * Returns the behavior of shapes at the game window's borders.
   * The options are:
   * <ul>
   *  <li><code>NONE</code>: allow shapes to leave the window.</li>
   *  <li><code>SOLID</code>: prevent shapes from leaving the window. (See
   *  {@link Shape#setSolid}.)</li>
   *  <li><code>BOUNCE</code>: make shapes bounce when they hit the window
   *  borders, i.e., reverse direction as if bouncing off a physical
   *  border.</li>
   * </ul>
   *
   * @return  a <code>BorderBehavior</code> specifying the way shapes react
   *          when they reach the border of the window.
   */
  public static BorderBehavior getBorderBehavior() {
    return borderBehavior;
  }

  /**
   * Represents a border of the game window. Used by
   * {@link Shape#touchingBorders}.
   */
  public enum Border { 
    TOP(Direction.UP, new Segment(0, HEIGHT, WIDTH, HEIGHT)),
    RIGHT(Direction.RIGHT, new Segment(WIDTH, HEIGHT, WIDTH, 0)),
    BOTTOM(Direction.DOWN, new Segment(WIDTH, 0, 0, 0)),
    LEFT(Direction.LEFT, new Segment(0, 0, 0, HEIGHT)),
    OFFSCREEN(null, null);

    private Direction direction;
    private Segment segment;
  
    private Border(Direction d, Segment seg) {
      direction = d;
      segment = seg;
    }

    /**
     * Returns the direction pointing towards this border. For example,
     * <code>Game.Border.LEFT.getDirection()</code> returns a direction
     * pointing left.
     *
     * @return  the direction pointing towards this border, or
     *          <code>null</code> if this <code>Border</code> is
     *          <code>OFFSCREEN</code>.
     */
    public Direction getDirection() {
      return direction;
    }

    Segment getSegment() {
      return segment;
    }

    /**
     * Returns an array of all four borders. Doesn't include
     * <code>OFFSCREEN</code>.
     *
     * @return  an array of all four borders.
     */
    public static Border[] all() {
      return new Border[] { TOP, RIGHT, BOTTOM, LEFT };
    }
  };
}
