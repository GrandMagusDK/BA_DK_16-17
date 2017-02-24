package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import agent.AgentNode;
import agent.IMapDataUpdate;
import agent.SimpleAgent;
import agent.SimulationAgent;
import agent.SimPosition;
import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphEdge;
import graphGen.AbstractedGraphNode;
import graphGen.FullGraph;
import graphGen.LowLevelGraph;
import graphGen.LowLevelGraphNode;
import javafx.application.Application;
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

public class SimulationGUI extends Application implements IMapDataUpdate{
	double squareSize;
	double simWidth;
	double simHeight;
	double edgeWidth;
	LowLevelGraph lowGraph;
	AbstractedGraph abstractedGraph;
	FullGraph fullGraph;
	ListView<String> listCurrentGraph;
	boolean calledWithFullGraph = false;
	boolean calledWithLowLevelGraph = false;
	List<SimpleAgent> agents;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// This will never be used but is required by Application
	}
	public void start(Stage primaryStage, FullGraph fullGraph){
		this.fullGraph = fullGraph;
		calledWithFullGraph = true;
		try {
			buildProcessingScreen(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(Stage primaryStage, LowLevelGraph graph){
		this.lowGraph = graph;
		calledWithLowLevelGraph = true;
		
		try {
			buildProcessingScreen(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildProcessingScreen(Stage primaryStage) throws Exception {
		// TODO 
		listCurrentGraph = new ListView<String>();
		Label labelCurrentGraph = new Label();
		labelCurrentGraph.setText("Current Graph:");
		Button buttonnextStep = new Button();
		GridPane grid = new GridPane();
		
		if(calledWithLowLevelGraph){
			fillListCurrentLLG();
			buttonnextStep.setText("Process to Graph");
			
		}
		else if(calledWithFullGraph){
			fillListCurrentFG();
			buttonnextStep.setText("Continue to Simulation");
		}
		
		buttonnextStep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO 
				if(calledWithLowLevelGraph){
					processToGraph();
				}
				saveGraph("tempGraph", fullGraph);
				buildSimScene(primaryStage);
				
			}
		});
		
		grid.add(labelCurrentGraph, 0, 0);
		grid.add(listCurrentGraph, 0, 2);
		grid.add(buttonnextStep, 0, 4);
		
		Scene scene = new Scene(grid);
		primaryStage.setTitle("Simulation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void processToGraph(){
		abstractedGraph = new AbstractedGraph(lowGraph.getIntersections(), lowGraph.getLocatedGraph());
		abstractedGraph.startProcessing();
		fullGraph = new FullGraph(abstractedGraph.getNodes(), abstractedGraph.getEdges(), lowGraph);
	}
	
	private void fillListCurrentFG(){
		String endLine = "\n";
		ObservableList<String> items = FXCollections.observableArrayList();
		items.add("Size: " + fullGraph.getLowLevelGraph().getSizeX() + " x " + fullGraph.getLowLevelGraph().getSizeY() + endLine);
		items.add("Intersections: " + endLine);
		items.add("Number of Intersections: " + fullGraph.getNodes().size() + endLine);
		
		for(int i= 0; i < fullGraph.getNodes().size(); i++){
			items.add("" + i + ": " + fullGraph.getNodes().get(i).getX() + ", " + fullGraph.getNodes().get(i).getY() + endLine);
		}
		listCurrentGraph.setItems(items);
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
		listCurrentGraph.setItems(items);
	}
	
	private void buildSimScene(Stage primaryStage){
		
		int sizeX = fullGraph.getLowLevelGraph().getSizeX();
		int sizeY = fullGraph.getLowLevelGraph().getSizeY();
		
		simWidth = sizeX  * 10;
		simHeight = sizeY  * 10;
		edgeWidth = 10;
		squareSize = edgeWidth * 1.5;
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
				saveGraph(name, fullGraph);
			}
		});
		
		controlsGrid.add(saveGraphTextField, 0, 0);
		controlsGrid.add(saveGraphButton, 1, 0);
		controlsGroup.getChildren().add(controlsGrid);
		
		rootPanel.getChildren().add(simGroup);
		rootPanel.getChildren().add(controlsGroup);
		
		
		
		//TODO Some UI design, fix intersection detection and test the shit.
		Scene scene = new Scene(rootPanel);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void buildGridView(Group simGroup){ //builds the base grid for the Simulation
		List<Rectangle> nodeSquares = new ArrayList<>();
		List<Rectangle> edgeRectangles = new ArrayList<>();
		
		//TODO
		
		nodeSquares = drawNodes(fullGraph.getNodes());
		edgeRectangles = drawEdges(fullGraph.getEdges());
	}
	
	private List<Rectangle> drawNodes(List<AbstractedGraphNode> nodes){
		List<Rectangle> squares = new ArrayList<>();
		for(int i = 0; i<nodes.size(); i++){
			squares.add(makeNodeSquare(nodes.get(i).getX(), nodes.get(i).getY(), squareSize));
		}
		return squares;
	}

	private List<Rectangle> drawEdges(List<AbstractedGraphEdge> edges){
		List<Rectangle> edgeRecs = new ArrayList<>();
		return edgeRecs;
	}
	
	private void saveGraph(String name, FullGraph abstractedGraph){
		//TODO Seemingly works final test needs visualization.
		System.out.println("Saving Graph");
		try {
			FileOutputStream fileOut = new FileOutputStream(name + ".abg");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(abstractedGraph);
			out.close();
			fileOut.close();
			System.out.println("Finished Saving Graph!");
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
	@Override
	public List<LowLevelGraphNode> fetchMapData(SimulationAgent agent) {
		SimPosition translated;
		List<LowLevelGraphNode> nodes = new ArrayList<>();
		for(SimPosition position: agent.getScanPositions()){
			translated = translatePositionForAgents(position, agent.getStartPosition());
			nodes.add(fullGraph.getLowLevelGraph().getLocatedGraph()[translated.getX()][translated.getY()]);
		}
		return nodes;
	}
	
	public List<SimulationAgent> fetchCommunicationData(SimulationAgent agent){
		List<SimulationAgent> resultAgents = new ArrayList<>();
		List<SimPosition> translatedPositions = new ArrayList<>();
		SimPosition translated;
		for(SimPosition position : agent.getComPositions()){
			translatedPositions.add(translatePositionForAgents(position, agent.getStartPosition()));
		}
		for(SimulationAgent otherAgent : agents){
			translated = translatePositionForAgents(otherAgent.getCurrentPosition(), otherAgent.getStartPosition());
			for(SimPosition position : translatedPositions){
				if(translated == position){
					resultAgents.add(otherAgent);
				}
			}
		}
		return resultAgents;
	}
	
	private SimPosition translatePositionForAgents(SimPosition position, SimPosition agentStartPosition){
		SimPosition newPos = new SimPosition(position.getX() + agentStartPosition.getX(), position.getY() + agentStartPosition.getY());
		return newPos;
	}
}
