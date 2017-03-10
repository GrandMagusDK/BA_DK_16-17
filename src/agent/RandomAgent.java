package agent;

import java.util.Random;

import gui.SimulationGUI;

public class RandomAgent extends SimpleAgent{
	private AgentNode currentNode;
	
	public RandomAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, facing, simGUI);
		currentNode = getNodeFromPosition(currentPosition);
	}
	
	public RandomAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing, SimulationGUI simGUI){
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, simGUI);
		currentNode = getNodeFromPosition(currentPosition);
	}
	
	@Override
	protected void nextMove() throws InterruptedException {
		findNeighbors(currentNode);
		SimPosition newPosition;
		AgentNode newNode = null;
		if(currentNode.getNeighbors().size() > 2){
			newPosition = chooseRandomNeighbour();
		}
		else{
			newPosition = lookAhead();
		}
		
		if(newPosition != null){
			newNode = getNodeFromPosition(newPosition);
			if(newNode.isTraversable()){
				if(newNode.checkIn(id)){
					currentPosition = newPosition;
					newNode.checkOut(id);
					System.out.println("New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY());
					return;
				}
			}
		}
		handleWall();
	}
	
	protected void handleWall() throws InterruptedException{
		turnRight();
	}
	
	private void findNeighbors(AgentNode node) {
		int x = node.getPosition().getX();
		int y = node.getPosition().getY();
		// right
		if (isKnownPosition(new SimPosition(x, y+1))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x, y+1)));
		}
		// bottom
		if (isKnownPosition(new SimPosition(x+1, y))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x+1, y)));
		}
		// left
		if (isKnownPosition(new SimPosition(x, y-1))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x, y-1)));
		}
		// top
		if (isKnownPosition(new SimPosition(x-1, y))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x-1, y)));
		}
	}
	
	protected SimPosition chooseRandomNeighbour(){
		Random rnd = new Random();
		int size = currentNode.getNeighbors().size();
		int randomInt = rnd.nextInt(size-1);
		SimPosition position = currentNode.getNeighbors().get(randomInt).getPosition();
		return position;
	}

}
