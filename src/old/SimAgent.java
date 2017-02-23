package old;

import agent.SimPosition;
import agent.SimulationCompletePath;
import graphGen.AbstractedGraphNode;

public class SimAgent {
	String name;
	private BusAgentBehaviour behaviour;
	private SimPosition currentPosition;
	private SimPosition targetPosition;
	private SimPosition nextNodePosition;
	private AbstractedGraphNode targetNode;
	private SimulationCompletePath simPath;
	private int currentLowPathIndex = 0;
	private int currentNodeIndex = 0;
	private int nodeCounter = 0;
	private double stepLength = 1;
	private double angle;
	private double nodeResolution = 1;
	
	public SimAgent() {}
	public SimAgent(BusAgentBehaviour behaviour, SimPosition startPosition){
		this.behaviour = behaviour;
		this.currentPosition = startPosition;
	}
	public void initialize(){
		targetNode = behaviour.chooseTarget(currentPosition);
		targetPosition = new SimPosition(targetNode.getX(), targetNode.getY());
		simPath = behaviour.findPath(currentPosition, targetPosition);
		System.out.println(simPath.toString());
		calculateAngle();
		nodeCounter = 0;
		getNextNodePosition();
	}
	
	public void doCycle(){
		//TODO
		if(currentPosition.getX() == targetNode.getX() && currentPosition.getY() == targetNode.getY()){
			initialize();
			return;
		}
		calculateNewPosition(stepLength * nodeResolution);
	}
	
	public void generatePublicData(){
		//TODO
	}
	
	public void detectCollision(){
		//TODO
	}
	
	public void handleCollision(){
		//TODO
	}
	
	private void calculateNewPosition(double moveLength){
		//TODO
		double lengthToNextNode = Math.sqrt(Math.pow(nextNodePosition.getX() - currentPosition.getX(), 2) + Math.pow(nextNodePosition.getY() - currentPosition.getY(), 2)); 
		
		if(lengthToNextNode < stepLength){ // hit a node
			
			if(nextNodePosition == targetPosition){ //hit target, stop there
				currentPosition = targetPosition;
				return;
			}
			
			currentPosition = nextNodePosition;
			getNextNodePosition();
			moveLength = stepLength - lengthToNextNode;
			nodeCounter++;
			
			if(moveLength > nodeResolution){ // if you would step over another NoteCenter call recursively
				calculateNewPosition(moveLength);
				return;
			}
		}

		double[] deltas = calculateCoordDeltas(moveLength);
		double nextX = currentPosition.getX() + deltas[0];
		double nextY = currentPosition.getY() + deltas[1];
		int newX = (int) Math.round(nextX/nodeResolution);
		int newY = (int) Math.round(nextY/nodeResolution);
		
		currentPosition.setX(newX);
		currentPosition.setY(newY);
	}
	
	private void getNextNodePosition(){ 
		int x = simPath.getLowPaths().get(currentLowPathIndex).getNodeOrder().get(currentNodeIndex).getX();
		int y = simPath.getLowPaths().get(currentLowPathIndex).getNodeOrder().get(currentNodeIndex).getY();
		nextNodePosition = new SimPosition(x, y);
		
		currentLowPathIndex++;
		currentNodeIndex++;
		if(currentNodeIndex == simPath.getLowPaths().get(currentLowPathIndex).getNodeOrder().size()){
			currentLowPathIndex++;
			currentNodeIndex = 0;
		}
	}
	
	private void calculateAngle(){
		double alpha = 0;
		double deltaX = targetNode.getX() - currentPosition.getX();
		double deltaY = targetNode.getY() - currentPosition.getY();
		double lenght = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		
		alpha = Math.toDegrees(Math.asin(deltaY / lenght));
		angle = alpha;
	}
	
	private double[] calculateCoordDeltas(double moveLength){
		double deltaX = Math.cos(angle) * moveLength;
		double deltaY = Math.sin(angle) * moveLength;
		
		return new double[] {deltaX, deltaY}; 
	}
	
	public SimPosition getCurrentPosition(){
		return currentPosition;
	}
	
	public String getPublicInformation(){
		//TODO
		return null;
	}
	
	public void setStepLenght(double nodesPerCycle){
		if(nodesPerCycle < 5){
			this.stepLength = nodesPerCycle;
		}
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
