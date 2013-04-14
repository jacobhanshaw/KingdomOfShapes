package Shapes;

import java.awt.*;
import javax.swing.*;

class Canvas extends JPanel implements Runnable {

  private Thread animator;
  private Game game;

  private final int DELAY = 20; // nate: FPS should be a global constant

  Canvas(Game game) {
    super();

    this.game = game;

    setDoubleBuffered(true);
    setPreferredSize(new Dimension(game.WIDTH, game.HEIGHT));
    setSize(game.WIDTH, game.HEIGHT);
  }

  public void paint(Graphics g0) {
    super.paint(g0);

    Graphics2D g = (Graphics2D)g0;

    if (game.hasTitle()) {
      game.renderTitle(g);
    } else {
      for (Integer layer : game.getLayers()) {
        for (Shape s : game.getLayerContents(layer)) {
          if (!s.isDestroyed()) {
            s.render(g);
          } else {
            game.getLayerContents(layer).remove(s);
          }
        }
      }
      game.renderCounters(g);
      if (game.hasSubtitle()) {
        game.renderSubtitle(g);
      }
    }

    Toolkit.getDefaultToolkit().sync();
    g0.dispose();
  }

  public void addNotify() {
    super.addNotify();
    animator = new Thread(this);
    animator.start();
  }

  public void run() {
    long beforeTime, timeDiff, sleep;

    beforeTime = System.currentTimeMillis();

    while (true) {

      game.autoUpdate();
      if (!game.hasTitle()) {
        game.update();
      }
      repaint();

      timeDiff = System.currentTimeMillis() - beforeTime;
      sleep = DELAY - timeDiff;

      if (sleep < 0)
        sleep = 2;
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        System.out.println("interrupted");
      }

      beforeTime = System.currentTimeMillis();
    }
  }

  public Point[] getCorners() {
    return new Point[] {
      new Point(0, game.HEIGHT),
      new Point(game.WIDTH, game.HEIGHT),
      new Point(game.WIDTH, 0),
      new Point(0, 0)
    };
  }

  Segment[] getBorders() {
    return new Segment[] {
      Game.Border.TOP.getSegment(),
      Game.Border.RIGHT.getSegment(),
      Game.Border.BOTTOM.getSegment(),
      Game.Border.LEFT.getSegment()
    };
  }
}
