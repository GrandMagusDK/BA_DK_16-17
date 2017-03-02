package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.IMapDataUpdate;
import agent.SimpleAgent;
import agent.SimulationAgent;
import agent.SimPosition;
import graphGen.AbstractedGraph;
import graphGen.FullGraph;
import graphGen.LowLevelGraph;
import graphGen.LowLevelGraphNode;
import javafx.animation.AnimationTimer;
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
	int resolution;
	LowLevelGraph lowGraph;
	AbstractedGraph abstractedGraph;
	FullGraph fullGraph;
	ListView<String> listCurrentGraph;
	boolean calledWithFullGraph = false;
	boolean calledWithLowLevelGraph = false;
	List<SimpleAgent> agents;
	GridPane simGrid;
	Rectangle[][] nodeSquares;
	
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
		
		resolution = 20;
		HBox rootPanel = new HBox();
		Group simGroup = new Group();
		Group controlsGroup = new Group();
		GridPane controlsGrid = new GridPane();
		simGrid = new GridPane();
		Button saveGraphButton = new Button();
		Button startButton = new Button();
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
		
		startButton.setText("Start");
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				for(SimulationAgent agent : agents){
					new Thread(agent).start();
				}
			}
		});
		
		controlsGrid.add(saveGraphTextField, 0, 1);
		controlsGrid.add(saveGraphButton, 1, 1);
		controlsGrid.add(startButton, 0, 0);
		controlsGroup.getChildren().add(controlsGrid);
		
		rootPanel.getChildren().add(simGroup);
		rootPanel.getChildren().add(controlsGroup);
		buildGridView();
		
		GUITimer timer = new GUITimer();
		timer.start();
		//TODO Some UI design, fix intersection detection and test the shit.
		Scene scene = new Scene(rootPanel);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private class GUITimer extends AnimationTimer{

		@Override
		public void handle(long arg0) {
			for(SimulationAgent agent : agents){
				updateAgentPositions(agent);
			}
			updateNodeColor();
		}
	}
	
	private void buildGridView(){ //builds the base grid for the Simulation
		//TODO
		Rectangle newRec;
		for(int i = 0; i < fullGraph.getLowLevelGraph().getLocatedGraph().length; i++){
			for(int j = 0; j < fullGraph.getLowLevelGraph().getLocatedGraph().length; j++){
				newRec = new Rectangle(resolution, resolution);
				if(fullGraph.getLowLevelGraph().getLocatedGraph()[i][j].isTraversable()){
					newRec.setFill(Color.WHITE);
				}
				else{
					newRec.setFill(Color.GRAY);
				}
				nodeSquares[i][j] = newRec;
				simGrid.add(newRec, i, j);
			}
		}
	}
	
	public void updateNodeColor() {
		SimPosition translated;
		for(SimulationAgent agent : agents){
			for(SimPosition position: agent.getKnownMapPositions()){
				translated = translateToWorld(position, agent);
				if(agent.isKnownPosition(translated)){
					if(fullGraph.getLowLevelGraph().getLocatedGraph()[translated.getX()][translated.getY()].isTraversable()){
						nodeSquares[translated.getX()][translated.getY()].setFill(Color.WHITE);
					}
				}
			}
		}
	}
	
	public void updateAgentPositions(SimulationAgent agent) {
		SimPosition newPosition = new SimPosition(agent.getCurrentWorldCoord().getX(), agent.getCurrentWorldCoord().getY());
		moveAgent(newPosition, agent.getSimRectangle());
	}
	
	private void moveAgent(SimPosition newPosition, Rectangle agentRectangle) {
		simGrid.getChildren().remove(agentRectangle);
		simGrid.add(agentRectangle, newPosition.getX(), newPosition.getY());
	}
	
	private SimPosition translateToWorld(SimPosition position, SimulationAgent agent){
		int x = position.getX() - agent.getCoordsOfFirstOrigin().getX() + agent.getStartPosition().getX();
		int y = position.getY() - agent.getCoordsOfFirstOrigin().getY() + agent.getStartPosition().getY();
		SimPosition result = new SimPosition(x, y);
		return result;
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
	
	@Override
	public Map<SimPosition, LowLevelGraphNode> fetchMapData(SimulationAgent agent) {
		SimPosition translated;
		Map<SimPosition, LowLevelGraphNode> result = new HashMap<>();
		for(SimPosition position: agent.getScanPositions()){
			translated = translatePositionForAgents(position, agent.getStartPosition());
			if(translated != null){
				result.put(position, fullGraph.getLowLevelGraph().getLocatedGraph()[translated.getX()][translated.getY()]);
			}
			else{
				result.put(position, null);
			}
		}
		return result;
	}
	
	public Map<SimulationAgent, SimPosition> fetchCommunicationData(SimulationAgent agent){
		Map<SimulationAgent, SimPosition> resultAgents = new HashMap<>();
		List<SimPosition> translatedPositions = new ArrayList<>();
		SimPosition translated;
		SimPosition ownPositionTranslated = new SimPosition(agent.getCurrentPosition().getX() + agent.getStartPosition().getX(),
				agent.getCurrentPosition().getY() + agent.getStartPosition().getY());
		double deltaX, deltaY, distance;
		
		for(SimPosition position : agent.getComPositions()){
			translatedPositions.add(translatePositionForAgents(position, agent.getStartPosition()));
		}
		for(SimulationAgent otherAgent : agents){
			translated = translatePositionForAgents(otherAgent.getCurrentPosition(), otherAgent.getStartPosition());
			for(SimPosition position : translatedPositions){
				if(translated == position){
					//deltaX = position.getX() - ownPositionTranslated.getX();
					//deltaY = position.getY() - ownPositionTranslated.getY();
					//distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
					int index = translatedPositions.indexOf(position);
					resultAgents.put(otherAgent, agent.getComPositions().get(index));
				}
			}
		}
		return resultAgents;
	}
	
	private SimPosition translatePositionForAgents(SimPosition position, SimPosition agentStartPosition){
		SimPosition newPos = null;
		int newX = position.getX() + agentStartPosition.getX();
		int newY = position.getY() + agentStartPosition.getY();
		if(newX >= 0 && newY >= 0){
			if(newX < lowGraph.getSizeX() && newY < lowGraph.getSizeY()){
				newPos = new SimPosition(newX, newY);
			}
		}
		return newPos;
	}
}
