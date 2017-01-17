package agent;

import java.util.ArrayList;
import java.util.List;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;

public class BusAgentBehaviour extends AgentBehaviour{
	
    SimPosition currentPosition;
    List<SimPosition> busRoute;
    
	public BusAgentBehaviour(AbstractedGraph abstractedGraph){
		super(abstractedGraph);
		initializeBusRoute();
	}
	
	SimulationCompletePath findPath(SimPosition startPosition, SimPosition endPosition){
		AStarForAgents aStar = new AStarForAgents(abstractedGraph);
		return aStar.findPath(startPosition, endPosition);
	}
	
	AbstractedGraphNode chooseTarget(SimPosition currentPosition){
		this.currentPosition = currentPosition;
		int index = busRoute.indexOf(abstractedGraph.getNode(currentPosition)) +1; //index +1, next one in line
		index = index  % (busRoute.size()); // 
		AbstractedGraphNode nextNode = abstractedGraph.getNode(busRoute.get(index));
		
		return nextNode;
	}
	
	private void initializeBusRoute(){ //this is basically a round trip simulation a bus' route
		busRoute = new ArrayList<>();
		busRoute.add(new SimPosition(1, 1));
		busRoute.add(new SimPosition(9, 1));
		busRoute.add(new SimPosition(20, 1));
		busRoute.add(new SimPosition(28,  1));
		
		busRoute.add(new SimPosition(28,  9));
		busRoute.add(new SimPosition(20, 9));
		busRoute.add(new SimPosition(9, 9));
		busRoute.add(new SimPosition(1, 9));
	}
	
	public AbstractedGraph getAbstractedGraph(){
		return abstractedGraph;
	}
	
	public LowLevelGraphNode[][] getLowLevelGraphNodes(){
		return abstractedGraph.getLowLevelGraph();
	}
	
}
