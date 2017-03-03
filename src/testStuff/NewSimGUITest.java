package testStuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import agent.FacingDirectionEnum;
import agent.IMapDataUpdate;
import agent.SimPosition;
import agent.SimpleAgent;
import agent.SimulationAgent;
import graphGen.LowLevelGraphNode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class NewSimGUITest extends Application implements IMapDataUpdate{
	Rectangle[][] grid;
	LowLevelGraphNode[][] lowNodes;
	SimPosition agentPosition;
	SimpleAgent agent;
	Rectangle agentRec;
	int resolution = 20;
	int size = 22;
	int agentRecsize = (int) (Math.sqrt(2 * Math.pow(size, 2)) + 0.5);
	GridPane gridPane;
	List<SimulationAgent> agents = new ArrayList<>();
	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit( false );
		if(!Platform.isImplicitExit())
			System.out.println("implicit exit false");
		HBox hbox = new HBox();
		Group group = new Group();
		gridPane = new GridPane();
		grid = buildGrid(gridPane);
		fillGridPane(gridPane);
		
		//agent = new SimpleAgent(0, new SimPosition(0, 0), 1, 2, 2, FacingDirectionEnum.RIGHT, true);
		agentPosition = agent.getCurrentWorldCoord();
		agentRec = new Rectangle(14, 14, Color.BLACK);
		agentRec.setRotate(45);
		Button btn = new Button();
		btn.setText("Start");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				new Thread(agent).start();
//				updateThread = new UIUpdateThread(grid, agentRec, agent);
//				updateThread.run();
			}
		});
		gridPane.add(agentRec, agentPosition.getX(), agentPosition.getY());
		group.getChildren().add(gridPane);
		hbox.getChildren().add(group);
		hbox.getChildren().add(btn);
		
		GUITimer timer = new GUITimer();
		timer.start();
		
		Scene scene = new Scene(hbox);
		primaryStage.setScene(scene);
 		primaryStage.show();
	}
	
	private class GUITimer extends AnimationTimer{

		@Override
		public void handle(long arg0) {
			updateAgentPositions();
			updateRecColor();
		}
	}
	
	protected void updateAgentPositions(){
		SimPosition newPosition = new SimPosition(agent.getCurrentWorldCoord().getX(), agent.getCurrentWorldCoord().getY());
		moveAgent(newPosition);
	}
	
	public void moveAgent(SimPosition position){
		gridPane.getChildren().remove(agentRec);
		gridPane.add(agentRec, position.getX(), position.getY());
	}

	private void updateRecColor(){
		SimPosition translated;
		for(SimPosition position: agent.getKnownMapPositions()){
			translated = translateToWorld(position, agent);
			if(agent.isKnownPosition(translated)){
				if(lowNodes[translated.getX()][translated.getY()].isTraversable())
					grid[translated.getX()][translated.getY()].setFill(Color.WHITE);
			}
		}
	}
	
	private Rectangle[][] buildGrid(GridPane grid){
		Rectangle[][] recs = new Rectangle[size][size];
		lowNodes = testMazeNodes();
		
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				if(lowNodes[i][j].isTraversable()){
					recs[i][j] = new Rectangle(resolution, resolution, Color.BISQUE);
				}else
				{
					recs[i][j] = new Rectangle(resolution, resolution, Color.GRAY);
				}
			}
		}
		return recs;
	}
	
	private void fillGridPane(GridPane pane){
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				pane.add(grid[i][j], i, j);
			}
		}
	}
	
	private LowLevelGraphNode[][] testMazeNodes(){
		LowLevelGraphNode[][] nodes = new LowLevelGraphNode[size][size];
		
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				nodes[i][j] = new LowLevelGraphNode(i, j);
				nodes[i][j].setTraversable(true);
			}
		}
		for(int i = 1; i < size; i=i+3){
			for(int j = 1; j < size; j=j+3){
				nodes[i][j].setTraversable(false);
				nodes[i+1][j].setTraversable(false);
				nodes[i][j+1].setTraversable(false);
				nodes[i+1][j+1].setTraversable(false);
			}
		}
		return nodes;
	}

	private SimPosition translateToWorld(SimPosition position, SimulationAgent agent){
		int x = position.getX() - agent.getCoordsOfFirstOrigin().getX() + agent.getStartPosition().getX();
		int y = position.getY() - agent.getCoordsOfFirstOrigin().getY() + agent.getStartPosition().getY();
		SimPosition result = new SimPosition(x, y);
		return result;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public Map<SimPosition, LowLevelGraphNode> fetchMapData(SimulationAgent agent) {
		SimPosition translated;
		Map<SimPosition, LowLevelGraphNode> result = new HashMap<>();
		for(SimPosition position: agent.getScanPositions()){
			translated = translatePositionForAgents(position, agent.getStartPosition());
			if(translated != null){
				result.put(position, lowNodes[translated.getX()][translated.getY()]);
			}
			else{
				result.put(position, null);
			}
		}
		return result;
	}
	
	public List<SimulationAgent> fetchCommunicationData(SimulationAgent agent){
		List<SimulationAgent> resultAgents = new ArrayList<>();
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
					resultAgents.add(otherAgent);
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
			if(newX < lowNodes.length && newY < lowNodes.length){
				newPos = new SimPosition(newX, newY);
			}
		}
		return newPos;
	}
}

