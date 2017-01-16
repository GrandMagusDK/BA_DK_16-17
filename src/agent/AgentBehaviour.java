package agent;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;

public abstract class AgentBehaviour {

	AbstractedGraph abstractedGraph;
    LowLevelGraphNode currentPosition;
	
	public AgentBehaviour(AbstractedGraph abstractedGraph){
		this.abstractedGraph =  abstractedGraph;
	}
	
	abstract SimulationPathLow findPath();
	
	abstract AbstractedGraphNode chooseTarget();
	
	public AbstractedGraph getAbstractedGraph(){
		return abstractedGraph;
	}
	
	public LowLevelGraphNode[][] getLowLevelGraphNodes(){
		return abstractedGraph.getLowLevelGraph();
	}
}