package agent;

import java.util.ArrayList;
import java.util.List;

import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;

public abstract class SimulationAgent implements Runnable{

	protected int id;
	int sensorRange, communicationRange, moveDistance;
	protected SimPosition currentPosition;
	protected SimPosition startPosition; //This is in world coordinates for conveniance.
	protected AgentNode startNode;
	protected AgentNode nextNode;
	protected List<AgentNode> ownGrid = new ArrayList<>();
	protected IMapDataUpdate updater;
	protected List<SimPosition> scanPositions;
	protected List<SimPosition> comPositions;
	protected List<LowLevelGraphNode> mapData;
	protected List<SimulationAgent> agentsInRange;
	
	public SimulationAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange){
		this.id = id;
		this.sensorRange = sensorRange;
		this.communicationRange = communicationRange;
		this.moveDistance = moveDistance;
		this.startPosition = startPosition;
		this.startNode = new AgentNode(new SimPosition(0, 0), true);
		currentPosition = startNode.getCurrentPosition();
	}
	
	protected void doTurn(){
		scanPositions = buildScanPositions();
		comPositions = buildCommunicationScanPositions();
		getSimData();
		scanSurroundings();
		scanForCommunication();
		nextNode = nextMove();
	}

	protected abstract List<SimPosition> buildScanPositions();
	
	protected abstract List<SimPosition> buildCommunicationScanPositions();
	
	protected void getSimData(){
		updater = new SimulationGUI();
		mapData = updater.fetchMapData(this);
		agentsInRange = updater.fetchCommunicationData(this);
	}
	
	protected abstract void scanSurroundings(); //aka what to do with the map information, like adding AgentNodes to the map

	protected abstract void scanForCommunication();
	
	protected List<AgentNode> mergeMap(){
		List<AgentNode> newMap = new ArrayList<>();
		//TODO
		return newMap;
	}
	protected abstract AgentNode nextMove();
	
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

	public List<AgentNode> getOwnGrid() {
		return ownGrid;
	}
	
	public List<SimPosition> getScanPositions() {
		return scanPositions;
	}

	public List<SimPosition> getComPositions() {
		return comPositions;
	}

}
