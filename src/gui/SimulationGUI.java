package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import graphGen.AbstractedGraph;
import graphGen.LowLevelGraph;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SimulationGUI extends Application{
	LowLevelGraph lowGraph;
	AbstractedGraph abstractedGraph;
	ListView<String> listCurrentLLG;
	@Override
	public void start(Stage primaryStage) throws Exception {
		// This will never be used but is required by Application
	}
	
	public void start(Stage primaryStage, LowLevelGraph graph) throws Exception {
		// TODO 
		if(graph != null){
			System.out.println("SimGUI: Graph recived.");
			System.out.println("Size" + graph.getSizeX() + ", " + graph.getSizeY());
			System.out.println("Number of Intersections" + graph.getIntersections().size());
		}
		
		Button buttonProcessToGraph = new Button();
		listCurrentLLG = new ListView<String>();
		Label labelCurrentLLG = new Label();
		
		GridPane grid = new GridPane();
		
		labelCurrentLLG.setText("Current Grid:");
		if(graph != null){
			lowGraph = graph;
			fillListCurrentLLG();
		}
		
		buttonProcessToGraph.setText("Process to Graph");
		buttonProcessToGraph.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO 
				abstractedGraph = new AbstractedGraph(graph.getIntersections(), graph.getLocatedGraph());
				abstractedGraph.startProcessing();
				buildSimScene(primaryStage, abstractedGraph);
			}
		});
		
		grid.add(labelCurrentLLG, 0, 0);
		grid.add(listCurrentLLG, 0, 2);
		grid.add(buttonProcessToGraph, 0, 4);
		
		Scene scene = new Scene(grid);
		primaryStage.setTitle("Simulation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void fillListCurrentLLG(){
		String endLine = "\n";
		ObservableList<String> items = FXCollections.observableArrayList();
		items.add("Size: " + lowGraph.getSizeX() + " x " + lowGraph.getSizeY() + endLine);
		items.add("Intersections: " + endLine);
		items.add("Number of Intersections: " + lowGraph.getIntersections().size() + endLine);
		
		for(int i= 0; i < lowGraph.getIntersections().size(); i++){
			items.add("" + i + ": " + lowGraph.getIntersections().get(i).getX() + ", " + lowGraph.getIntersections().get(i).getY() + endLine);
		}
		listCurrentLLG.setItems(items);
	}
	
	private void buildSimScene(Stage primaryStage, AbstractedGraph abstractedGraph){
		
		int sizeX = abstractedGraph.getLowLevelGraph().length;
		int sizeY = abstractedGraph.getLowLevelGraph()[0].length;
		
		double simWidth = sizeX  * 10;
		double simHeight = sizeY  * 10;
		double edgeWidth = 0;
		double squareSize = edgeWidth * 1.5;
		HBox rootPanel = new HBox();
		Group simGroup = new Group();
		Group controlsGroup = new Group();
		GridPane controlsGrid = new GridPane();
		Button saveGraphButton = new Button();
		TextField saveGraphTextField = new TextField();
		
		saveGraphButton.setText("Save Graph");
		saveGraphButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String name = saveGraphTextField.getText();
				if(name == "" || name ==null){
					System.out.println("Name error for saving Graph");
					return;
				}
				saveGraph(name, abstractedGraph);
			}
		});
		
		controlsGrid.add(saveGraphTextField, 0, 0);
		controlsGrid.add(saveGraphButton, 1, 0);
		controlsGroup.getChildren().add(controlsGrid);
		
		rootPanel.getChildren().add(simGroup);
		rootPanel.getChildren().add(controlsGroup);
		
		//makeEdges
		for(int i = 0; i > abstractedGraph.getEdges().size(); i++){
			double[] coordinates = new double[4];
			coordinates[0] = abstractedGraph.getEdges().get(i).getNodes().get(0).getGridPosition()[0];
			coordinates[1] = abstractedGraph.getEdges().get(i).getNodes().get(0).getGridPosition()[1];
			coordinates[2] = abstractedGraph.getEdges().get(i).getNodes().get(1).getGridPosition()[0];
			coordinates[3] = abstractedGraph.getEdges().get(i).getNodes().get(1).getGridPosition()[1];
			
			Rectangle edge = makeEdgeRectangle(coordinates, edgeWidth);
			simGroup.getChildren().add(edge);
		}
		
		//make Nodes
		for(int i = 0; i < abstractedGraph.getNodes().size(); i++){
			int[] coords = abstractedGraph.getNodes().get(i).getGridPosition();
			Rectangle rec = makeNodeSquare(coords[0], coords[1], squareSize);
			simGroup.getChildren().add(rec);
		}
		
		//TODO Some UI design, fix intersection detection and test the shit.
		Scene scene = new Scene(rootPanel);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void saveGraph(String name, AbstractedGraph abstractedGraph){
		//TODO Seemingly works final test needs deserialization.
		try {
			FileOutputStream fileOut = new FileOutputStream(name + ".abg");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(abstractedGraph);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Rectangle makeEdgeRectangle(double[] coordinates, double edgeWidth){
		//TODO
		double squareSize = edgeWidth * 1.5;
		double startX = coordinates[0];
		double startY = coordinates[1];
		double endX = coordinates[2];
		double endY = coordinates[3];
		
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
		
 		Rectangle recEdge = new Rectangle(recLength, 20, Color.DARKGREY);
 		recEdge.setArcHeight(edgeWidth/5.0);
 		recEdge.setArcWidth(edgeWidth/5.0);
 		recEdge.setTranslateX(startX + (deltaX/2.0) - (recLength/2.0));
 		recEdge.setTranslateY(startY + (deltaY/2.0) - (edgeWidth/2.0));
 		recEdge.setRotate(edgeRot);
 		
 		return recEdge;
	}
	
	private Rectangle makeNodeSquare(double x, double y, double size){
		Rectangle square = new Rectangle(size, size);
		square.setArcHeight(size / 5.0);
		square.setArcWidth(size / 5.0);
		square.setFill(Color.DARKBLUE);
		
		square.setTranslateX(x - (size / 2.0));
		square.setTranslateY(y - (size / 2.0));
		
		return square;
	}
	
	private double[] gridToSimCoordinates(int gridX, int gridY){
		double[] simCoords = new double[2];
		
		//TODO
		
		return simCoords;
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
}
