package agent;

import java.util.ArrayList;
import java.util.List;

public class SimAgent implements Runnable{
	private static int id;
	private AgentBehaviour behaviour;
	private SimPosition currentPosition;
	private AgentNode startNode;
	private List<AgentNode> ownGrid;
	
	public SimAgent() {}
	public SimAgent(AgentBehaviour behaviour, int id){
		this.id = id;
		this.behaviour = behaviour;
		ownGrid = new ArrayList<>();
		initialize();
	}
	
	public void initialize(){
		currentPosition = new SimPosition(0, 0);
		startNode = new AgentNode(currentPosition, true);
		ownGrid.add(startNode);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void doTurn(){
		//TODO
		
		
	}
	
	private void checkSurroundings(){
		
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
	
	public SimPosition getCurrentPosition(){
		return currentPosition;
	}
	
	public String getPublicInformation(){
		//TODO
		return null;
	}
	
	public int getID(){
		return id;
	}
	
}
