package agent;

import java.util.List;

import graphGen.LowLevelGraphNode;

public class SimulationPath {
	private LowLevelGraphNode startNode, endNode;
	private List<LowLevelGraphNode> nodeOrder;
	
	public LowLevelGraphNode getStartNode() {
		return startNode;
	}

	public SimulationPath(LowLevelGraphNode startNode, LowLevelGraphNode endNode, List<LowLevelGraphNode> nodeOrder){
		this.startNode = startNode;
		this.endNode = endNode;
		this.nodeOrder = nodeOrder;
	}
	
	public LowLevelGraphNode getEndNode() {
		return endNode;
	}

	public List<LowLevelGraphNode> getNodeOrder() {
		return nodeOrder;
	}
}
