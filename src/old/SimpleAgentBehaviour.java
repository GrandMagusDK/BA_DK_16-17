package old;

import graphGen.AbstractedGraphNode;

public class SimpleAgentBehaviour extends AgentBehaviour{

	FacingDirectionEnum facing;
	
	public SimpleAgentBehaviour(double stepLength, double communicationRange, double sensorRange) {
		super(stepLength, communicationRange, sensorRange);
		facing = FacingDirectionEnum.TOP;
	}
	
	public SimpleAgentBehaviour(double stepLength, double communicationRange, double sensorRange, FacingDirectionEnum facing) {
		super(stepLength, communicationRange, sensorRange);
		this.facing = facing;
	}

	@Override
	public void handleEndOfPath() {
		// TODO Auto-generated method stub
		turnRight();
	}
	
	public void encounterIntersection(){
		
	}

	@Override
	AbstractedGraphNode chooseTarget(SimPosition currentPosition) {
		// TODO Auto-generated method stub
		return null;
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

	public FacingDirectionEnum getFacing() {
		return facing;
	}

	public void setFacing(FacingDirectionEnum facing) {
		this.facing = facing;
	}

}
