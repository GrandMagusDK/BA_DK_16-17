package agent;

import java.util.Date;
import java.util.Random;

import gui.SimulationGUI;
import testStuff.NewSimGUITest;

public class RandomAgent extends SimpleAgent{
	
	public RandomAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, facing, simGUI);
		agentType = "Random Agent";
	}
	
	public RandomAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing, SimulationGUI simGUI){
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, simGUI);
		agentType = "Random Agent";
	}
	
	@Override
	public void run() {
		System.out.println("SimpleAgent Thread running!");
		agentStartTime = new Date();
		while (!agentDone) {
			doTurn();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void timeOut() {
		//not used
	}
	
	@Override
	protected void nextMove() throws InterruptedException {
		if (!unexploredPathsLeft()) {
			agentIsDone(false);
			return;
		}
		findNeighbors(currentNode);
		SimPosition newPosition;
		AgentNode newNode = null;
		if(getNodeFromPosition(currentPosition).isIntersection()){
			newPosition = chooseRandomNeighbour();
		}
		else{
			newPosition = chooseNextPosition(translateFacingToInt(facing));
		}
		
		if(newPosition != null){
			newNode = getNodeFromPosition(newPosition);
			if(newNode.isTraversable()){
				if(newNode.checkIn(id)){
					newNode.setUnexplored(false);
					facing = changeFacing(newPosition);
					currentPosition = newPosition;
					currentNode = getNodeFromPosition(currentPosition);
					newNode.checkOut(id);
					System.out.println("[RA " + id + "] New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY() + "[" + turnCounter + "]");
					return;
				}
			}
		}
		handleWall();
	}
	
	protected void handleWall() throws InterruptedException{
		turnRight();
	}
	
	private boolean isValidNode(SimPosition position){
		AgentNode node = getNodeFromPosition(position);
		if(node != null){
			if(node.isTraversable()){
				return true;
			}
		}
		return false;
	}
	
	protected SimPosition chooseRandomNeighbour(){
		Random rnd = new Random();
		int x = currentPosition.getX(), y = currentPosition.getY();
		int facingInt = translateFacingToInt(facing);
		int randomInt = rnd.nextInt(4);
		SimPosition newPosition = null;
		if(randomInt != ((facingInt + 2) % 4)){
			switch(randomInt){
			case 0: if(isValidNode(new SimPosition(x, y - 1)))
				newPosition = new SimPosition(x, y - 1);
				break;
			case 1: if(isValidNode(new SimPosition(x + 1, y)))
				newPosition = new SimPosition(x + 1, y);
				break;
			case 2: if(isValidNode(new SimPosition(x, y + 1)))
				newPosition = new SimPosition(x, y + 1);
				break;
			case 3: if(isValidNode(new SimPosition(x - 1, y)))
				newPosition = new SimPosition(x - 1, y);
			}
		}
		else{
			return chooseRandomNeighbour();
		}
		return newPosition;
	}

}
