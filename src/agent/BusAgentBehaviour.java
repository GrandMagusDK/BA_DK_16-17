package agent;

import java.util.List;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;

public class BusAgentBehaviour extends AgentBehaviour{
	
    SimPosition currentPosition;
	
	public BusAgentBehaviour(AbstractedGraph abstractedGraph){
		super(abstractedGraph);
	}
	
	SimulationCompletePath findPath(SimPosition startPosition, SimPosition endPosition){
		AStarForAgents aStar = new AStarForAgents(abstractedGraph);
		return aStar.findPath(startPosition, endPosition);
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
