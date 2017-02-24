package old;

import graphGen.AbstractedGraphNode;

public abstract class AgentBehaviour {
	
	double stepLenght, communicationRange, sensorRange;
	
	public AgentBehaviour(double stepLength, double communicationRange, double SensorRange){
		this.stepLenght = stepLength;
		this.communicationRange = communicationRange;
		this.sensorRange = sensorRange;
	}
	
	
	
	abstract void handleEndOfPath();
	
	abstract AbstractedGraphNode chooseTarget(SimPosition currentPosition); 
	
}
