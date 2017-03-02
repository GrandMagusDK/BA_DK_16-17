package agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import graphGen.LowLevelGraphNode;

public class SimpleAgent extends SimulationAgent{
	
	FacingDirectionEnum facing;
	boolean running = true;
	
	public SimpleAgent(int id, SimPosition startPosition){
		super(id, startPosition, 1, 4, 2);
		this.facing = FacingDirectionEnum.TOP;
	}
	
	public SimpleAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing){
		super(id, startPosition, moveDistance, communicationRange, sensorRange);
		this.facing = facing;
	}
	
	public SimpleAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing, boolean test){
		super(id, startPosition, moveDistance, communicationRange, sensorRange, true);
		this.facing = facing;
	}
	
	@Override
	public void run() {
		// TODO
		System.out.println("SimpleAgent Thread running!");
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
		// TODO
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
	
	private void updateMapNodeMerge(AgentNode newNode, SimPosition position){
		AgentNode node = getNodeFromPosition(position);
		if(node.isUnexploredPathMarker()){
			node.setUnexploredPathMarker(newNode.isUnexploredPathMarker());
		}
	}
	
	@Override
	protected void scanForCommunication() {
		// TODO Auto-generated method stub
		for(Map.Entry<SimulationAgent, SimPosition> entry : agentsInRange.entrySet()){
			mergeMap(entry.getKey(), entry.getValue());
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
			turnRight();
			Thread.sleep(1000);
			nextMove();
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
	
	private void turnRight(){ 
		System.out.println("Turning Right");
		switch(facing){
		case TOP : facing = FacingDirectionEnum.RIGHT;
		System.out.println("Now facing Right");
		break;
		case RIGHT : facing = FacingDirectionEnum.BOTTOM;
		System.out.println("Now facing Bottom");
		break;
		case BOTTOM : facing = FacingDirectionEnum.LEFT;
		System.out.println("Now facing Left");
		break;
		case LEFT : facing = FacingDirectionEnum.TOP;
		System.out.println("Now facing Top");
		}
		System.out.println("Turning Right");
	}
	
	private void turnLeft(){
		switch(facing){
		case TOP : facing = FacingDirectionEnum.LEFT;
		break;
		case RIGHT : facing = FacingDirectionEnum.BOTTOM;
		break;
		case BOTTOM : facing = FacingDirectionEnum.RIGHT;
		break;
		case LEFT : facing = FacingDirectionEnum.TOP;
		}
	}
	
	public SimPosition getCurrentPosition(){
		return currentPosition;
	}
	
	public FacingDirectionEnum getFacing() {
		return facing;
	}

	public boolean isRunning() {
		return running;
	}

	
}
