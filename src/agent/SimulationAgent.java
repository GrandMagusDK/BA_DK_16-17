package agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;
import javafx.scene.shape.Rectangle;
import testStuff.NewSimGUITest;

public abstract class SimulationAgent implements Runnable {

	protected int id;
	protected int sensorRange;
	protected int communicationRange;
	protected int moveDistance;
	protected int currentSizeX;
	protected int currentSizeY;
	protected int[] status = new int[2];;
	public int turnCounter = 0;
	protected boolean test = false;
	protected boolean modifyingMap = false;
	protected boolean agentDone = false;
	protected static String agentType;
	protected SimPosition currentPosition;
	protected SimPosition coordsOfFirstOrigin;
	protected SimPosition startPosition;
	protected SimPosition originTransformationVektor;
	protected AgentNode currentNode;
	protected AgentNode startNode;
	protected FacingDirectionEnum facing;
	protected List<AgentNode> ownMap = new ArrayList<>();
	protected List<AgentNode> backupMap = new ArrayList<>();
	protected List<SimPosition> scanPositions = new ArrayList<>();
	protected List<SimPosition> comPositions = new ArrayList<>();
	protected List<SimulationAgent> agentsInRange = new ArrayList<>();
	protected List<AgentNode> exploredNodes = new ArrayList<>();
	protected Map<Integer, Integer> recentAgentCommunication = new HashMap<>();
	protected Map<SimPosition, LowLevelGraphNode> mapData;
	protected IMapDataUpdate updater;
	protected SimulationGUI simGUI;
	protected NewSimGUITest guiTest;
	protected Rectangle simRectangle;
	protected Date agentStartTime;
	protected Date agentEndTime;

	public SimulationAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange,
			FacingDirectionEnum facing, SimulationGUI simGUI) {
		this.id = id;
		this.sensorRange = sensorRange;
		this.communicationRange = communicationRange;
		this.moveDistance = moveDistance;
		this.startPosition = startPosition;
		this.startNode = new AgentNode(new SimPosition(0, 0), true);
		this.facing = facing;
		this.simGUI = simGUI;
		initialize();
	}

	public SimulationAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange,
			FacingDirectionEnum facing, boolean test) {
		this.id = id;
		this.sensorRange = sensorRange;
		this.communicationRange = communicationRange;
		this.moveDistance = moveDistance;
		this.startPosition = startPosition;
		this.startNode = new AgentNode(new SimPosition(0, 0), true);
		this.facing = facing;
		this.test = true;
		initialize();
	}

	protected void initialize() {
		ownMap.add(startNode);
		startNode.setUnexplored(false);
		currentPosition = startNode.getPosition();
		startNode.checkIn(id);
		coordsOfFirstOrigin = new SimPosition(0, 0);
		backupMap = ownMap;
	}

	protected abstract void timeOut(); 
	protected void doTurn() {
//		if (turnCounter == 500){
//			agentIsDone(true);
//			return;
//		}
		scanPositions = buildScanPositions();
		getSimData();
		modifyingMap = true;
		scanSurroundings();
		if (!checkOriginAlignment()) {
			alignOrigin();
		}
		scanForCommunication();
		backupMap = ownMap;
		modifyingMap = false;
		try {
			nextMove();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateStatus();
		backupMap = ownMap;
		turnCounter++;
	}

	protected void getSimData() {
		if (test) {
			updater = guiTest;
		} else {
			updater = simGUI;
		}
		mapData = updater.fetchMapData(this);
		agentsInRange.clear();
		agentsInRange = updater.fetchCommunicationData(this);
	}

	protected abstract void scanSurroundings(); // aka what to do with the map
												// information, like adding
												// AgentNodes to the map

	protected abstract void scanForCommunication();

	protected void mergeMap(SimulationAgent otherAgent, SimPosition otherAgentPosition) {
		List<AgentNode> newMap = new ArrayList<>();
		List<AgentNode> oldMap = copyMap(ownMap);
		List<AgentNode> otherMap = copyMap(otherAgent.getOwnMap());
		List<AgentNode> transformedMap;
		// otherAgentPosition is already in local coordinates
		// otherAgentPosition (in local coords) - otherAgent.getCurrentPosition
		// (in his local coords)
		SimPosition otherOrigin = otherAgentPosition.minus(otherAgent.getCurrentPosition());
		// translate all nodes from other map into our coords
		if (otherOrigin.getX() == 0 && otherOrigin.getY() == 0) {
			System.out.println("Origins identical, skipping transformation");
			transformedMap = otherMap;
		} else {
			transformedMap = new ArrayList<>();
			for (AgentNode node : otherMap) {
				AgentNode newNode = new AgentNode(node);
				newNode.setPosition(otherOrigin.plus(newNode.getPosition()));
				transformedMap.add(newNode);
			}
		}
		// merge the two lists:
		for (AgentNode otherNode : transformedMap) {
			if(!isKnownPosition(otherNode.getPosition())){
				newMap.add(otherNode);
			}
		}
		oldMap.addAll(newMap);
		ownMap = oldMap;
		if (checkOriginAlignment()) {
			alignOrigin();
		}
		rebuildKnownPositions();
		System.out.println("Merge for Agent " + id + " and Agent " + otherAgent.getId());
	}
	
	protected void rebuildKnownPositions(){
		List<SimPosition> rebuild = new ArrayList<>();
		for(AgentNode node: ownMap){
			if(node.isUnexplored())
				rebuild.add(node.getPosition());
		}
	}

	protected abstract void actionsAferOriginChange(); // allows custom actions after
												// merging the map before the
												// modifying flag gets lifted

	protected abstract void nextMove() throws InterruptedException;

	protected List<SimPosition> buildScanPositions() {
		return rangeBuilder(sensorRange, currentPosition);
	}

	private List<SimPosition> rangeBuilder(int range, SimPosition currentPosition) {
		double distance;
		List<SimPosition> result = new ArrayList<>();

		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				distance = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
				if (distance <= range) {
					double x = i + currentPosition.getX();
					double y = j + currentPosition.getY();
					SimPosition newPosition = new SimPosition((int) x, (int) y);
					if (!(i == 0 && j == 0))
						result.add(newPosition);
				}
			}
		}
		return result;
	}

	private boolean checkOriginAlignment() {
		for (AgentNode node : ownMap) {
			if (node.getPosition().getX() < 0 || node.getPosition().getY() < 0) {
				return false;
			}
		}
		return true;
	}

	protected void alignOrigin() {
		int minX = 0, minY = 0, maxX = 0, maxY = 0, newX, newY;
		// looking for lowest x and y positions
		for (AgentNode node : ownMap) {
			if (node.getPosition().getX() < minX)
				minX = node.getPosition().getX();
			if (node.getPosition().getY() < minY)
				minY = node.getPosition().getY();
		}
		// updating positions in MapNodes and rebuildingKnownPositions
		SimPosition newPosition;
		SimPosition newOrigin = new SimPosition(minX, minY);
		originTransformationVektor = newOrigin;
		for(AgentNode node : ownMap){
			newPosition = node.getPosition().minus(newOrigin);
			node.setPosition(newPosition);
		}
		currentPosition = currentPosition.minus(newOrigin);
		currentNode = getNodeFromPosition(currentPosition);
		coordsOfFirstOrigin = coordsOfFirstOrigin.minus(newOrigin);
		currentSizeX = maxX;
		currentSizeY = maxY;
		rebuildKnownPositions();
		actionsAferOriginChange();
	}

	protected void updateRecentAgentComms() {
		List<Integer> keysToDelete = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : recentAgentCommunication.entrySet()) {
			entry.setValue(entry.getValue() - 1);
			if (entry.getValue() <= 0)
				keysToDelete.add(entry.getKey());
		}
		for (int key : keysToDelete) {
			recentAgentCommunication.remove(key);
		}

	}

	protected void agentIsDone(boolean timeout) {
		if (timeout) {
			System.out.println("Agent cought in Loop and timed out");
		} else {
			System.out.println("All reachable Nodes discovered!");
		}
		System.out.println("Agent shutting down!");
		agentEndTime = new Date();
		agentDone = true;
		return;
	}

	protected void updateStatus() {
		int counter = 0;
		for(AgentNode node : ownMap){
			if(node.isTraversable())
				if(!node.isUnexplored())
					counter++;
		}
		status[0] = counter;
		status[1] = turnCounter;
	}

	public AgentNode getNodeFromPosition(SimPosition position) {
		AgentNode resultNode = null;

		for (AgentNode node : ownMap) {
			if (node.getPosition().equals(position)) {
				resultNode = node;
				break;
			}
		}
		return resultNode;
	}

	public boolean isKnownPosition(SimPosition position) {
		for (AgentNode node : ownMap) {
			if (node.getPosition().equals(position))
				return true;
		}
		return false;
	}

	public boolean isKnownPosition(int x, int y) {
		for (AgentNode node : ownMap) {
			if (x == node.getPosition().getX() && y == node.getPosition().getY())
				return true;
		}
		return false;
	}

	protected List<AgentNode> copyMap(List<AgentNode> map){
		List<AgentNode> copy = new ArrayList<>();
		AgentNode newNode;
		for(AgentNode node: map){
			newNode = new AgentNode(node);
			copy.add(newNode);
		}
		return copy;
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
		if (modifyingMap = true) {
			return backupMap;
		}
		return ownMap;
	}

	public List<SimPosition> getScanPositions() {
		return scanPositions;
	}

	public List<SimPosition> getComPositions() {
		return comPositions;
	}

	public SimPosition getCurrentWorldCoord() {
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

	public int[] getStatus() {
		return status;
	}

	public boolean isAgentDone() {
		return agentDone;
	}

	public Date getAgentEndTime() {
		return agentEndTime;
	}

	public Date getAgentStartTime() {
		return agentStartTime;
	}

	public String getAgentType() {
		return agentType;
	}

	public int getTurnCounter() {
		return turnCounter;
	}
}
