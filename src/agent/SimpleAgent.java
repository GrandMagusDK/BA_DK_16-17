package agent;

import java.util.List;

public class SimpleAgent extends SimulationAgent{
	
	FacingDirectionEnum facing;
	
	public SimpleAgent(int id, SimPosition startPosition){
		super(id, startPosition, 1, 4, 2);
		this.facing = FacingDirectionEnum.TOP;
	}
	
	public SimpleAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange, FacingDirectionEnum facing){
		super(id, startPosition, moveDistance, communicationRange, sensorRange);
		this.facing = facing;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected List<SimPosition> buildScanPositions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<SimPosition> buildCommunicationScanPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void scanSurroundings() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void scanForCommunication() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AgentNode nextMove() {
		// TODO Auto-generated method stub
		return null;
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
	
	private void turnRight(){ 
		switch(facing){
		case TOP : facing = FacingDirectionEnum.RIGHT;
		break;
		case RIGHT : facing = FacingDirectionEnum.BOTTOM;
		break;
		case BOTTOM : facing = FacingDirectionEnum.LEFT;
		break;
		case LEFT : facing = FacingDirectionEnum.TOP;
		}
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
	
	public String getPublicInformation(){
		//TODO
		return null;
	}
}
