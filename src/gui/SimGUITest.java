package gui;

import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
 
public class SimGUITest extends Application{
	Circle agent1 = new Circle(15);
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		agent1.setFill(Color.BLUE);
		
		VBox root = new VBox();
		Group simGroup = new Group();
		simGroup.prefHeight(500);
		simGroup.prefWidth(500);
		
		double recHeight = 20;
		double squareSize = recHeight * 1.5;
		int startX = 100;
		int startY = 400;
		int endX = 400;
		int endY = 100;
		
		double centeredStartX = startX - (squareSize / 2);
		double centeredStartY = startY - (squareSize / 2);
		double centeredEndX = endX - (squareSize / 2);
		double centeredEndY = endY - (squareSize / 2);
		
		int quadrant = findQuadrant(new double[]{centeredStartX, centeredStartY, centeredEndX, centeredEndY});
		
		double deltaXAbs = Math.abs(endX - startX);
		double deltaYAbs = Math.abs(endY - startY);
		
 		double recLength = Math.sqrt((Math.pow(deltaXAbs, 2) + Math.pow(deltaYAbs, 2)));
 		System.out.println(recLength);
 		
 		double edgeRot = 0;
 		double angle = Math.toDegrees(Math.asin(deltaYAbs / recLength));
 		System.out.println(quadrant);
 		switch(quadrant){
 		case 1: edgeRot = angle;
 		break;
 		case 2: edgeRot = 180.0 - angle;
 		break;
 		case 3: edgeRot = 180.0 + angle;
 		break;
 		case 4: edgeRot = 360.0 - angle;
 		break;
 		case 5: edgeRot = 270;
 		break;
 		case 6: edgeRot = 90;
 		break;
 		case 7: edgeRot = 0;
 		break;
 		case 8: edgeRot = 180;
 		}
 		double deltaX = endX - startX;
 		double deltaY = endY - startY;
 		System.out.println(deltaX + "," + deltaY);
		
		System.out.println(edgeRot);
 		Rectangle recEdge = new Rectangle(recLength, 20, Color.PINK);
 		recEdge.setArcHeight(recHeight/5.0);
 		recEdge.setArcWidth(recHeight/5.0);
 		recEdge.setTranslateX(startX + (deltaX/2) - (recLength/2));
 		System.out.println("TranslateX: " + (startX - (deltaX/2) - (recLength/2)));
 		recEdge.setTranslateY(startY + (deltaY/2) - (recHeight/2));
 		System.out.println("TranslateY: " + (startY + (deltaY/2) - (recHeight/2)));
 		recEdge.setRotate(edgeRot);
 		
 		Rectangle squareNodeStart = new Rectangle(squareSize, squareSize, Color.BLACK);
 		Rectangle squareNodeEnd = new Rectangle(squareSize, squareSize, Color.RED);

 		squareNodeStart.setArcHeight(squareSize / 5);
 		squareNodeStart.setArcWidth(squareSize / 5);
 		squareNodeEnd.setArcHeight(squareSize / 5);
 		squareNodeEnd.setArcWidth(squareSize / 5);
 		
 		squareNodeStart.setTranslateX(startX - (squareSize/2));
 		squareNodeStart.setTranslateY(startY - (squareSize/2));
 		
 		squareNodeEnd.setTranslateX(endX - (squareSize/2));
 		squareNodeEnd.setTranslateY(endY - (squareSize/2));
 		
 		//root.getChildren().add(canvas);
 		simGroup.getChildren().add(recEdge);
 		simGroup.getChildren().add(squareNodeStart);
 		simGroup.getChildren().add(squareNodeEnd);
 		root.getChildren().add(simGroup);
 		
 		PathTransition ptr = new PathTransition();
 		Path path = new Path();
 		path.getElements().add(new LineTo(400, 400));
 		
 		//ptr.setDuration(69);
 		
 		AnimationTimer timer = new GUITimer();
 		timer.start();
 		
 		primaryStage.setScene(new Scene(root, 500, 500));
 		primaryStage.show();
	}

	private class GUITimer extends AnimationTimer{

		@Override
		public void handle(long arg0) {
			// TODO Auto-generated method stub
			updateAgentPositions();
		}
	}
	
	private void updateAgentPositions(){
		//TODO
	}
	
	private int findQuadrant(double[] coords){//coords: startX, startY, endX, endY
		int result = 0;
		//Quadrant 1 SX < EX, SY < EY
		if(coords[0] < coords[2] && coords[1] < coords[3]) //startX < endX
			result = 1;
		//Quadrant 2 SX > EX, SY < EY
		if(coords[0] > coords[2] && coords[1] < coords[3]) //startX < endX
			result = 2;
		//Quadrant 3 SX > EX, SY > EY
		if(coords[0] > coords[2] && coords[1] > coords[3]) //startX < endX
			result = 3;
		//Quadrant 4 SX < EX, SY > EY
		if(coords[0] < coords[2] && coords[1] > coords[3]) //startX < endX
			result = 4;
		//vertical line SX = EX, SY < EY (Bottom-Top)
		if(coords[0] == coords[2] && coords[1] < coords[3])
			result = 5;
		//vertical line SX = EX, SY > EY (Top-Bottom)
		if(coords[0] == coords[2] && coords[1] > coords[3])
			result = 6;
		//horizontal line SY = EY, SX < EX (Left-Right)
		if(coords[0] < coords[2] && coords[1] == coords[3])
			result = 7;
		//horizontal line SY = EY, SX > EX (Right-Left)
		if(coords[0] > coords[2] && coords[1] == coords[3])
			result = 8;
		return result;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
