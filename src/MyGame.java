import Shapes.*;
import java.awt.Color;

public class MyGame extends Game {

  // Put variables here!

  @Override
  public void setup() {
    // Put code here!

    // This code will be executed when the game begins. This is where you
    // can make the initial shapes, set the background color, etc.
  }

  @Override
  public void update() {
    // Put code here!

    // This code will be executed once per frame. This is where you can make
    // shapes move around, say things, etc.
  }

  public static void main(String[] args) {
    new MyGame();
  }

  public MyGame() {
    super(false);
    setup();
    ready();
  }
}
