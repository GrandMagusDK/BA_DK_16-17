package agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;

public class ItelligentAgent extends SimulationAgent{
	
	boolean running = true;

	public ItelligentAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI){
		super(id, startPosition, 1, 4, 2, facing, simGUI);
	}
	
	public ItelligentAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange,
			FacingDirectionEnum facing, boolean test) {
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, test);
	}

	@Override
	public void run() {
		System.out.println("Intelligent Agent Thread running!");
		while(running){
			doTurn();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void scanSurroundings() {
		List<SimPosition> keysToDelete = new ArrayList<>();
		for(Map.Entry<SimPosition, LowLevelGraphNode> entry : mapData.entrySet()){
			if(entry.getValue() == null)
				keysToDelete.add(entry.getKey());
		}
		for(SimPosition position : keysToDelete){
			mapData.remove(position);
		}

		Set<SimPosition> positions = mapData.keySet();
		List<SimPosition> newPositions = new ArrayList<>();
		for(SimPosition position : positions){
			if(knownMapPositions.contains(position)){
				updateMapNodeSensor(mapData.get(position), position);
			}
			else{
				newPositions.add(position);
			}
		}
		for(SimPosition pos : newPositions){
			AgentNode newNode = new AgentNode();
			newNode.setPosition(pos);
			newNode.setTraversable(mapData.get(pos).isTraversable());
			newNode.setUnexploredPathMarker(true);
			newNode.setIntersection(mapData.get(pos).isIntersection());
			ownMap.add(newNode);
			knownMapPositions.add(pos);
		}
	}
	
	private void updateMapNodeSensor(LowLevelGraphNode lowNode, SimPosition position){
		AgentNode node = getNodeFromPosition(position);
		//TODO
	}

	@Override
	protected void scanForCommunication() {
		SimPosition otherAgentPosition;
		int x,y;
		for(SimulationAgent otherAgent : agentsInRange){
			otherAgentPosition = otherAgent.getCurrentWorldCoord();
			x = otherAgentPosition.getX() - getCurrentWorldCoord().getX();
			y = otherAgentPosition.getY() - getCurrentWorldCoord().getY();
			otherAgentPosition = new SimPosition(currentPosition.getX() + x, currentPosition.getY() + y);
			mergeMap(otherAgent, otherAgentPosition);
		}
	}

	@Override
	protected void nextMove() throws InterruptedException {
		SimPosition newPosition = lookAhead();
		if(newPosition != null){
			if(getNodeFromPosition(newPosition).isTraversable()){
				currentPosition = newPosition;
				System.out.println("New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY());
			}
		}
		else{
			//TODO
		}
	}

	private SimPosition lookAhead(){
		SimPosition position = currentPosition;
		int x = currentPosition.getX();
		int y = currentPosition.getY();
		switch(facing){
		case TOP : y -= moveDistance;
		break;
		case RIGHT : x += moveDistance;
		break;
		case BOTTOM : y += moveDistance;
		break;
		case LEFT : x -= moveDistance;
		}
		position = new SimPosition(x, y);
		if(x >= 0 && y >= 0){
			if(isKnownPosition(position))
				return position;
		}
		return null;
	}
}
