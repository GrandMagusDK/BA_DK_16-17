package agent;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;

public class AgentBehaviour {

	AbstractedGraph abstractedGraph;
	LowLevelGraphNode currentPosition;
	
	public AgentBehaviour(AbstractedGraph abstractedGraph){
		this.abstractedGraph =  abstractedGraph;
	}
	
	SimulationPath findPath(){
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
