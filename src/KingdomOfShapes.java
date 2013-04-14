import Shapes.*;

import java.awt.Color;
import java.util.ArrayList;

public class KingdomOfShapes extends Game {
	
	  public KingdomOfShapes() {
		    super(false);
		    setup();
		    ready();
		  }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int  difficulty = 0;
	ArrayList<Shape> shapes;
	Rectangle center1;
	Rectangle center2;
	Rectangle center3;
	Rectangle center4;
	Rectangle center5;
	Rectangle pointsLine;
	Rectangle line1;
	Rectangle line2;
	Shape playerShape;
	int level = 2;
	int score = 0;
	int speed = 2;
	int priority[];
	String priorityNames[] = { " Square: ", " Blue: ", " Red: ", " Circle: ",
			" Green: ", " Yellow: ", " Triangle: ", " Orange: ", " Magenta: ",
			" Rectangle: ", " Cyan: ", " Gray: ", " Black: " };
	String shapeNames[] = { "Square", "Circle", " Triangle", "Rectangle"};
	String colorNames[] = { "Blue", "Red", "Green", "Yellow", "Orange", "Magenta", "Cyan", "Gray", "Black" };
	String options[]    = { "No", "Yes" };
	int keys[]       = { Keyboard.UP, Keyboard.DOWN, Keyboard.LEFT, Keyboard.RIGHT, Keyboard.R };
	int attributesSelected[] = { 0, 0, 0 };
	int attributeOptionsLength[] = new int[3];
	int attributeIndex  = 0;
	int selectedIndex = 0;
	int givenIndex = 0;
	int points = -50;
	int invincibleTime = 0;
	int inputPriority[];
	boolean pressed[] = { false, false, false, false, false, };
	boolean hasWon;
	boolean hasStarted;
	boolean hasPickedShape = true;
	
	public enum Keys {
		UP, DOWN, LEFT, RIGHT, R
	}
	
	public enum Options {
		USESHAPE, SHAPE, COLOR
	}

	@Override
	public void setup() {
		Game.setBorderBehavior(BorderBehavior.BOUNCE);
		setBackgroundColor(Color.PINK);
		center1 = new Rectangle(new Point(WIDTH / 6, HEIGHT / 2 - 50), 1, 1);
		center1.setColor(Color.PINK);
		center1.getSpeechStyle().setBackgroundColor(null);
		center2 = new Rectangle(new Point(WIDTH / 6, HEIGHT / 2), 1, 1);
		center2.setColor(Color.PINK);
		center2.getSpeechStyle().setBackgroundColor(null);
		center3 = new Rectangle(new Point(WIDTH / 6, HEIGHT / 2 + 50), 1, 1);
		center3.setColor(Color.PINK);
		center3.getSpeechStyle().setBackgroundColor(null);
		center4 = new Rectangle(new Point(WIDTH / 6, HEIGHT / 2 + 100), 1, 1);
		center4.setColor(Color.PINK);
		center4.getSpeechStyle().setBackgroundColor(null);
		center5 = new Rectangle(new Point(WIDTH / 6, HEIGHT / 2 + 150), 1, 1);
		center5.setColor(Color.PINK);
		center5.getSpeechStyle().setBackgroundColor(null);
		pointsLine = new Rectangle();
		pointsLine.setCenter(new Point(0, 40));
		pointsLine.setColor(Color.PINK);
		pointsLine.setHeight(1);
		pointsLine.setWidth(1);
		pointsLine.getSpeechStyle().setBackgroundColor(null);
		line1 = new Rectangle();
		line1.setCenter(new Point(0, 20));
		line1.setColor(Color.PINK);
		line1.setHeight(1);
		line1.setWidth(1);
		line1.getSpeechStyle().setBackgroundColor(null);
		line2 = new Rectangle();
		line2.setCenter(new Point(0, 0));
		line2.setColor(Color.PINK);
		line2.setHeight(1);
		line2.setWidth(1);
		line2.getSpeechStyle().setBackgroundColor(null);
		shapes = new ArrayList<Shape>();
	}

	public void nextLevel() {
		
		if (level < 13) ++level;
		else ++speed;
		int numShapes = Math.min(((level - 1) / 3 + 1), 4);
		points +=50;
		inputPriority = new int[level];
		priority = new int[level];
		
		if(playerShape != null) playerShape.destroy();
		playerShape = null;
		
		for(Shape shapeA : shapes){
			shapeA.destroy();
		}
		shapes.clear();
		
		for (int i = 0; i < numShapes * 10; ++i) {
			Shape shape;
			if (numShapes >= 4 && (Math.random() * numShapes >= numShapes - 1))
				shape = new Rectangle(new Point(Math.random() * (WIDTH - 100) + 50,
						Math.random() * (HEIGHT - 100) + 50), 20, 40);
			else if (numShapes >= 3 && (Math.random() * numShapes >= numShapes - 1))
				shape = new Triangle(new Point(Math.random() * (WIDTH - 100) + 50,
						Math.random() * (HEIGHT - 100) + 50), 20);
			else if (numShapes >= 2 && (Math.random() * numShapes >= numShapes - 1))
				shape = new Circle(new Point(Math.random() * (WIDTH - 100) + 50,
						Math.random() * (HEIGHT - 100) + 50), 15);
			else
				shape = new Rectangle(new Point(Math.random() * (WIDTH - 100) + 50,
						Math.random() * (HEIGHT - 100) + 50), 25, 25);
			
			if (i % (level - numShapes) == 0)
				shape.setColor(Color.BLUE);
			else if (i % (level - numShapes) == 1)
				shape.setColor(Color.RED);
			else if (i % (level - numShapes) == 2)
				shape.setColor(Color.GREEN);
			else if (i % (level - numShapes) == 3)
				shape.setColor(Color.YELLOW);
			else if (i % (level - numShapes) == 4)
				shape.setColor(Color.ORANGE);
			else if (i % (level - numShapes) == 5)
				shape.setColor(Color.MAGENTA);
			else if (i % (level - numShapes) == 6)
				shape.setColor(Color.CYAN);
			else if (i % (level - numShapes) == 7)
				shape.setColor(Color.GRAY);
			else if (i % (level - numShapes) == 8)
				shape.setColor(Color.BLACK);
			shape.setSpeed(Math.random() * speed + 2);
			shape.setDirection(new Direction(Math.random() * 360));
			shape.setSolid(false);
			shape.setFilled(true);
			shapes.add(shape);
		}
		
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for (int i = 0; i < level; ++i) {
			nums.add(i);
		}
		
		if(level == 3) givenIndex = 0;
		else givenIndex = (int) Math.floor((Math.random() * level));
		if(givenIndex == selectedIndex) selectedIndex++;
		if (selectedIndex >= level) selectedIndex = 0;
		
		for (int i = 0; i < level; ++i) {
			priority[i] = nums.remove((int) Math.floor(Math.random()
					* nums.size()));
			inputPriority[i] = 0;
		}
		inputPriority[givenIndex] = priority[givenIndex];
		// This code will be executed when the game begins. This is where you
		// can make the initial shapes, set the background color, etc.
	}

	@Override
	public void update() {
		hasWon = hasWon();
		updateText();
		respondToMouseClicks();
		if(playerShape != null) {
			playerShape.move(Keyboard.direction(KeySet.WASD), 10);
			if(invincibleTime > 0) --invincibleTime;
		}
		if (hasStarted) {	
			if (!hasWon) respondToKeyPresses();
			checkForCollisionsWithShapes();
			//checkForCollisionsWithWalls();
		}
	}

	public boolean hasWon(){
		if(hasStarted){
		for (int i = 0; i < level; ++i) {
			if (inputPriority[i] != priority[i])
				return false;
		}
		}
		return true;
	}
	
	public void checkForCollisionsWithShapes() {
		for (Shape shapeA : shapes) {
			for (Shape shapeB : shapes) {
				if (shapeA.isTouching(shapeB)) {
					if (shapeA != shapeB && (invincibleTime == 0 || (shapeA != playerShape && shapeB != playerShape)))
						fight(shapeA, shapeB);
				}
			}
		}
	}

	public void checkForCollisionsWithWalls() {
		for (Shape shapeA : shapes) {
			Shape.Border collisionLocation = shapeA.isTouchingWhichBorder();
			if (collisionLocation != Shape.Border.NONE) {
				if (collisionLocation != Shape.Border.OFFSCREEN) {
					double mainAngle = 0.0;
					if (collisionLocation == Shape.Border.TOP)
						mainAngle = 90.0;
					else if (collisionLocation == Shape.Border.LEFT)
						mainAngle = 180.0;
					else if (collisionLocation == Shape.Border.BOTTOM)
						mainAngle = 270.0;
					double difference = shapeA.getDirection().toDegrees()-mainAngle;
					shapeA.setDirection(new Direction(shapeA.getDirection().toDegrees() + 180.0 + (double)(2*difference)));
				} 
				else {
					// should never happen,but reset location and direction anyway
					shapeA.setCenter(new Point(Math.random() * WIDTH, Math
							.random() * HEIGHT));
					shapeA.setDirection(new Direction(Math.random() * 360));
				}
			}
		}
	}

	public void fight(Shape shapeA, Shape shapeB) {
		int priorityA = getPriority(shapeA);
		int priorityB = getPriority(shapeB);
		if (priorityA > priorityB) {
			if(difficulty < 1) shapeA.setFilled(true);
			shapeB.setFilled(false);
		}
		else if (priorityA < priorityB) {
			shapeA.setFilled(false);
			if(difficulty < 1) shapeB.setFilled(true);
		}
		else {
			if(!sameShape(shapeA, shapeB)){
			shapeA.setFilled(false);
			shapeB.setFilled(false);
			}
		}
	}
	
	public boolean sameShape(Shape shapeA, Shape shapeB){
		return (shapeA.getClass().equals(shapeB.getClass()) && shapeA.getColor().equals(shapeB.getColor()));
	}

	public int getPriority(Shape shape) {
		int priorityLevel = 0;
		if (shape instanceof Rectangle && ((Rectangle) shape).getHeight() == ((Rectangle) shape).getWidth())
			priorityLevel += priority[0];
		else if (shape instanceof Circle)
			priorityLevel += priority[3];
		else if(shape instanceof Triangle) 
			priorityLevel += priority[6];
		else if (shape instanceof Rectangle)
			priorityLevel += priority[9];

		if (shape.getColor() == Color.BLUE)
			priorityLevel += priority[1];
		else if (shape.getColor() == Color.RED)
			priorityLevel += priority[2];
		else if (shape.getColor() == Color.GREEN)
			priorityLevel += priority[4];
		else if (shape.getColor() == Color.YELLOW)
			priorityLevel += priority[5];
		else if (shape.getColor() == Color.ORANGE)
			priorityLevel += priority[7];
		else if (shape.getColor() == Color.MAGENTA)
			priorityLevel += priority[8];
		else if (shape.getColor() == Color.CYAN)
			priorityLevel += priority[10];
		else if (shape.getColor() == Color.GRAY)
			priorityLevel += priority[11];
		else if (shape.getColor() == Color.BLACK)
			priorityLevel += priority[12];
		return priorityLevel;
	}
	
	public void createPlayerShape(){
		switch(attributesSelected[KingdomOfShapes.Options.SHAPE.ordinal()]){
			case 0:
				playerShape = new Rectangle(new Point(WIDTH/2,HEIGHT/2), 30, 30);
				break;
			case 1:
				playerShape = new Circle(new Point(WIDTH/2, HEIGHT/2), 20);
				break;
			case 2:
				playerShape = new Triangle(new Point(WIDTH/2,HEIGHT/2), 25);
				break;
			case 3:
				playerShape = new Rectangle(new Point(WIDTH/2, HEIGHT/2), 25, 45);
				break;
			default:
				System.out.println("Picked Shape Switch Statement Reached Impossible State");
		}
		switch(attributesSelected[KingdomOfShapes.Options.COLOR.ordinal()]){
		case 0:
			playerShape.setColor(Color.BLUE);
			break;
		case 1:
			playerShape.setColor(Color.RED);
			break;
		case 2:
			playerShape.setColor(Color.GREEN);
			break;
		case 3:
			playerShape.setColor(Color.YELLOW);
			break;
		case 4:
			playerShape.setColor(Color.ORANGE);
			break;
		case 5:
			playerShape.setColor(Color.MAGENTA);
			break;
		case 6:
			playerShape.setColor(Color.CYAN);
			break;
		case 7:
			playerShape.setColor(Color.GRAY);
			break;
		case 8:
			playerShape.setColor(Color.BLACK);
			break;
		default:
			System.out.println("Picked Color Switch Statement Reached Impossible State");
		}
		invincibleTime = 120;
		shapes.add(playerShape);
	}
	
	public void respondToMouseClicks(){
		if (Mouse.clickLocation() != null) {
			if(!hasStarted || hasWon){
			hasStarted = true;
			nextLevel();
			}
			else if(!hasPickedShape){
				if(playerShape != null) playerShape.destroy();
				playerShape = null;
				for (Shape shape : shapes) {
					shape.setCenter(new Point(Math.random() * WIDTH,
							Math.random() * HEIGHT));
					shape.setDirection(new Direction(
							Math.random() * 360));
					shape.setFilled(true);
				}
				if(attributesSelected[KingdomOfShapes.Options.USESHAPE.ordinal()] == 1){ 
					points -=10;
					createPlayerShape();
				}
				else points -= 5;
				hasPickedShape = true;
			}
		}
	}
	
	public void respondToKeyPresses(){
		for(int i = 0; i < keys.length; ++i){
			if (Keyboard.keyIsPressed(keys[i]))
				pressed[i] = true;
			else {
				if (pressed[i]) {
					respondToKeyPressed(i);
					pressed[i] = false;
				}
			}
		}
	}
	
	public void respondToKeyPressed(int keyIndex){
		final KingdomOfShapes.Keys[] keyValues = KingdomOfShapes.Keys.values();
		if(!hasPickedShape){
		attributeOptionsLength[KingdomOfShapes.Options.USESHAPE.ordinal()] = options.length;
		attributeOptionsLength[KingdomOfShapes.Options.SHAPE.ordinal()]    = Math.min(((level - 1) / 3 + 1), 4);
		attributeOptionsLength[KingdomOfShapes.Options.COLOR.ordinal()]    = (level - Math.min(((level - 1) / 3 + 1), 4));
		switch(keyValues[keyIndex]) {
		case UP:
			--attributeIndex;
			if(attributeIndex < 0) attributeIndex = attributesSelected.length - 1;
			break;
		case DOWN:
			++attributeIndex;
			if(attributeIndex >= attributesSelected.length) attributeIndex = 0;
			break;
		case LEFT:
			--attributesSelected[attributeIndex];
			if(attributesSelected[attributeIndex] < 0) 
				attributesSelected[attributeIndex] = attributeOptionsLength[attributeIndex] - 1;
			break;
		case RIGHT:
			++attributesSelected[attributeIndex];
			if(attributesSelected[attributeIndex] >= attributeOptionsLength[attributeIndex]) 
				attributesSelected[attributeIndex] = 0;
			break;
		case R:
			break;
		default:
			System.out.println("Reached End of Case Statement without Matching Key");
		}
		}
		else{
			switch(keyValues[keyIndex]) {
			case UP:
				if (selectedIndex != givenIndex)
					++inputPriority[selectedIndex];
				if (inputPriority[selectedIndex] >= level)
					inputPriority[selectedIndex] = 0;
				break;
			case DOWN:
				if (selectedIndex != givenIndex)
					--inputPriority[selectedIndex];
				if (inputPriority[selectedIndex] < 0)
					inputPriority[selectedIndex] = level - 1;
				break;
			case LEFT:
				--selectedIndex;
				if (selectedIndex == givenIndex)
					--selectedIndex;
				if (selectedIndex < 0)
					selectedIndex = level - 1;
				if (selectedIndex == givenIndex)
					--selectedIndex;
				break;
			case RIGHT:
				++selectedIndex;
				if (selectedIndex == givenIndex)
					++selectedIndex;
				if (selectedIndex >= level)
					selectedIndex = 0;
				if (selectedIndex == givenIndex)
					++selectedIndex;
				break;
			case R:
				hasPickedShape = false;
				break;
			default:
				System.out.println("Reached End of Case Statement without Matching Key");
			}
		}
	}
	
	public void updateText() {
		updateCenterStatements();
		updateReadOut();
	}
	
	public void updateCenterStatements() {
		if(!hasStarted){
			center5.say(" Welcome to Kingdom of Shapes! Each level, each shape and color is exclusively given a value 0 to (#shapes+#colors-1)");
			center4.say(" The shapes fight when they collide and the shape with the lower shape+color value becomes an outline");
			center3.say(" Think about what happens in a tie or when an already outlined shape hits a lower shape+color value");
			center2.say(" Press r to reset the shapes (at the cost of 5 points) or reset and control 1 shape (at the cost of 10)");
			center1.say(" Click anywhere to get started!");
		} 
		else if(!hasPickedShape){
			center5.say(" Choose a shape to control (if you want it) & click to continue. You'll lose 10 points instead of 5, but you may solve the puzzle sooner. Choose wisely");
			center4.say(" If you do choose a shape You'll be spawned in the center of screen, slightly bigger than other shapes, and given a few seconds to move)");
			center3.say(" Would you like to use a shape? " + options[attributesSelected[KingdomOfShapes.Options.USESHAPE.ordinal()]]);
			center2.say(" What shape? " + shapeNames[attributesSelected[KingdomOfShapes.Options.SHAPE.ordinal()]]);
			center1.say(" What color? " + colorNames[attributesSelected[KingdomOfShapes.Options.COLOR.ordinal()]]);
		}
		else {
			center1.say("");
			center2.say("");
			if(invincibleTime > 0) center3.say("                                                                                                                  Invincible Time: " + invincibleTime);
			else center3.say(""); 
			center4.say("");
			center5.say("");
		}
	}
	
	public void updateReadOut(){
		pointsLine.say(" Points: " + points);
		if (hasWon) {
			line1.say(" YOU WIN");
			line2.say(" Click to play the next level!");
		} 
		else {
			line1.say(" Given Priority: " + priorityNames[givenIndex]
					+ priority[givenIndex]);
			String line2Response = " Priority Levels:";
			for (int i = 0; i < level; ++i) {
				if (i != givenIndex)
					line2Response += (priorityNames[i] + inputPriority[i]);
			}
			line2.say(line2Response);
		}
	}

	public static void main(String[] args) {
		new KingdomOfShapes();
	}
}
