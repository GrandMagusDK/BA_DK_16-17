package agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;
import javafx.scene.shape.Rectangle;
import testStuff.NewSimGUITest;

public abstract class SimulationAgent implements Runnable{

	protected int id;
	protected int sensorRange;
	protected int communicationRange;
	protected int moveDistance;
	protected int currentSizeX;
	protected int currentSizeY;
	protected SimPosition currentPosition;
	protected SimPosition coordsOfFirstOrigin; //This is the coords in own coords of the original spawn node.
	protected SimPosition startPosition; //This is in world coordinates for conveniance.
	protected FacingDirectionEnum facing;
	protected AgentNode startNode;
	protected List<AgentNode> ownMap = new ArrayList<>();
	protected IMapDataUpdate updater;
	protected List<SimPosition> scanPositions;
	protected List<SimPosition> comPositions;
	protected Map<SimPosition, LowLevelGraphNode> mapData;
	protected List<SimulationAgent> agentsInRange;
	protected List<SimPosition> knownMapPositions = new ArrayList<>();
	protected boolean test = false;
	protected SimulationGUI simGUI;
	protected NewSimGUITest guiTest;
	protected Rectangle simRectangle;
	
	public SimulationAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing, SimulationGUI simGUI){
		this.id = id;
		this.sensorRange = sensorRange;
		this.communicationRange = communicationRange;
		this.moveDistance = moveDistance;
		this.startPosition = startPosition;
		this.startNode = new AgentNode(new SimPosition(0, 0), true);
		this.facing = facing;
		this.simGUI = simGUI;
		currentPosition = startNode.getPosition();
		coordsOfFirstOrigin = new SimPosition(0, 0);
		knownMapPositions.add(new SimPosition(0, 0));
	}
	
	public SimulationAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing, boolean test){
		this.id = id;
		this.sensorRange = sensorRange;
		this.communicationRange = communicationRange;
		this.moveDistance = moveDistance;
		this.startPosition = startPosition;
		this.startNode = new AgentNode(new SimPosition(0, 0), true);
		this.facing = facing;
		currentPosition = startNode.getPosition();
		coordsOfFirstOrigin = new SimPosition(0, 0);
		knownMapPositions.add(new SimPosition(0, 0));
		this.test = true;
	}
	
	protected void doTurn(){
		scanPositions = buildScanPositions();
		//comPositions = buildCommunicationScanPositions();
		getSimData();
		scanSurroundings();
		if(!checkOriginAlignment()){
			alignOrigin();
		}
		scanForCommunication();
		try {
			nextMove();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected List<SimPosition> buildScanPositions(){
		return rangeBuilder(sensorRange, currentPosition);
	}
	
	protected List<SimPosition> buildCommunicationScanPositions(){
		return rangeBuilder(communicationRange, currentPosition);
	}
	
	protected void getSimData(){
		if(test){
			updater = guiTest;
		}
		else{
			updater = simGUI;
		}
		mapData = updater.fetchMapData(this);
		agentsInRange = updater.fetchCommunicationData(this);
	}
	
	protected abstract void scanSurroundings(); //aka what to do with the map information, like adding AgentNodes to the map

	protected abstract void scanForCommunication();
	
	protected List<AgentNode> mergeMap(SimulationAgent otherAgent, SimPosition otherAgentPosition){
		//TODO
		List<AgentNode> newMap = new ArrayList<>();
		List<AgentNode> oldMap = ownMap;
		List<AgentNode> otherMap = otherAgent.getOwnMap();
		//calc new Origin Varaiant: Know each others relative position. Also everytime they share map they first put their Origin in the upper left corner of their known map		
		// otherAgentPosition - otherAgent.getCurrentPosition();
		SimPosition otherOrigin = otherAgentPosition.minus(otherAgent.getCurrentPosition()); 
		//translate all nodes from other map into our coords
		for(AgentNode node : otherMap){
			node.setPosition(otherOrigin.plus(node.getPosition()));
		}
		//merge the two lists:
		for(AgentNode otherNode : otherMap){
			for(AgentNode oldNode : oldMap){
				if(otherNode.getPosition() != oldNode.getPosition()){
					newMap.add(otherNode);
				}
			}
		}
		newMap.addAll(oldMap);
		ownMap = newMap;
		alignOrigin();
		return newMap;
	}
	
	protected abstract void nextMove() throws InterruptedException;
	
	private List<SimPosition> rangeBuilder(int range, SimPosition currentPosition){ //returns SimPos based on range in own Coords
		double distance;
		List<SimPosition> result = new ArrayList<>();
		
		for(int i = -range; i <= range; i++){
			for(int j = -range; j <= range; j++){
				distance = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
				if(distance <= range){
					double x = i + currentPosition.getX();
					double y = j + currentPosition.getY();
					SimPosition newPosition = new SimPosition((int) x, (int) y);
					if(!(i == 0 && j == 0))
						result.add(newPosition);
				}
			}
		}
		return result;
	}
	
	private boolean checkOriginAlignment(){
		for(AgentNode node : ownMap){
			if(node.getPosition().getX() < 0|| node.getPosition().getY() < 0){
				return false;
			}
		}
		return true;
	}
	
	protected void alignOrigin(){
		int minX = 0, minY = 0, maxX = 0, maxY = 0, newX, newY;
		for(AgentNode node : ownMap){
			if(node.getPosition().getX() < minX)
				minX = node.getPosition().getX();
			if(node.getPosition().getX() < minY)
				minY = node.getPosition().getX();
		}
		SimPosition newOrigin = new SimPosition(minX, minY);
		for(AgentNode node : ownMap){
			newX = node.getPosition().getX() - minX;
			newY = node.getPosition().getY() - minY;
			node.getPosition().setX(newX);
			node.getPosition().setY(newY);
			if(newX > maxX)
				maxX = newX;
			if(newY > maxY);
		}
		currentPosition.setX(currentPosition.getX() - newOrigin.getX());
		currentPosition.setY(currentPosition.getY() - newOrigin.getY());
		coordsOfFirstOrigin.setX(coordsOfFirstOrigin.getX() - newOrigin.getX());
		coordsOfFirstOrigin.setY(coordsOfFirstOrigin.getY() - newOrigin.getY());
		currentSizeX = maxX;
		currentSizeY = maxY;
	}
	
	public AgentNode getNodeFromPosition(SimPosition position){
		AgentNode resultNode = null;
		
		for(AgentNode node : ownMap){
			if(node.getPosition().equals(position)){
				resultNode = node;
				break;
			}
		}
		return resultNode;
	}
	
	public boolean isKnownPosition(SimPosition position){
		for(SimPosition pos : knownMapPositions){
			if(pos.equals(position))
				return true;
		}
		return false;
	}
	
	public List<SimPosition> getKnownMapPositions() {
		return knownMapPositions;
	}
	
	public SimPosition getStartPosition() {
		return startPosition;
	}

	public SimPosition getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(SimPosition currentPosition) {
		this.currentPosition = currentPosition;
	}

	public int getId() {
		return id;
	}

	public AgentNode getStartNode() {
		return startNode;
	}

	public List<AgentNode> getOwnMap() {
		return ownMap;
	}
	
	public List<SimPosition> getScanPositions() {
		return scanPositions;
	}

	public List<SimPosition> getComPositions() {
		return comPositions;
	}
	
	public SimPosition getCurrentWorldCoord(){
		int x = currentPosition.getX() - coordsOfFirstOrigin.getX() + startPosition.getX();
		int y = currentPosition.getY() - coordsOfFirstOrigin.getY() + startPosition.getY();
		SimPosition result = new SimPosition(x, y);
		return result;
	}

	public SimPosition getCoordsOfFirstOrigin() {
		return coordsOfFirstOrigin;
	}

	public Rectangle getSimRectangle() {
		return simRectangle;
	}

	public void setSimRectangle(Rectangle simRectangle) {
		this.simRectangle = simRectangle;
	}

	public FacingDirectionEnum getFacing() {
		return facing;
	}

	public void setFacing(FacingDirectionEnum facing) {
		this.facing = facing;
	}

	public int getCurrentSizeY() {
		return currentSizeY;
	}

	public void setCurrentSizeY(int currentSizeY) {
		this.currentSizeY = currentSizeY;
	}

	public int getCommunicationRange() {
		return communicationRange;
	}
}
