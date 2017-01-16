package agent;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;

public class BusAgentBehaviour extends AgentBehaviour{

	AbstractedGraph abstractedGraph;
    LowLevelGraphNode currentPosition;
	
	public BusAgentBehaviour(AbstractedGraph abstractedGraph){
		super(abstractedGraph);
	}
	
	SimulationPathLow findPath(){
		//TODO
		return null;
	}
	
	AbstractedGraphNode chooseTarget(){
		//TODO
		return null;
	}
	
	public AbstractedGraph getAbstractedGraph(){
		return abstractedGraph;
	}
	
	public LowLevelGraphNode[][] getLowLevelGraphNodes(){
		return abstractedGraph.getLowLevelGraph();
	}
	
}
