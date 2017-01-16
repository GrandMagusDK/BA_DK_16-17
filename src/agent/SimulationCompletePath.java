package agent;

import java.util.List;

import graphGen.AbstractedGraphNode;

public class SimulationCompletePath {
	
	private List<AbstractedGraphNode> abstractNodeOrder;
	private List<SimulationPathLow> lowPaths;
	
	public SimulationCompletePath(List<AbstractedGraphNode> abstractNodeOrder, List<SimulationPathLow> lowPaths){
		this.abstractNodeOrder = abstractNodeOrder;
		this.lowPaths = lowPaths;
	}
	
	public List<AbstractedGraphNode> getAbstractNodeOrder() {
		return abstractNodeOrder;
	}
	public List<SimulationPathLow> getLowPaths() {
		return lowPaths;
	}
}
