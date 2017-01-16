package agent;

import java.util.List;
import graphGen.AbstractedGraphNode;

public class SimulationPathTop {

	private AbstractedGraphNode startNode, endNode;
	private List<AbstractedGraphNode> nodeOrder;
		
	public SimulationPathTop(AbstractedGraphNode startNode, AbstractedGraphNode endNode, List<AbstractedGraphNode> nodeOrder){
		this.startNode = startNode;
		this.endNode = endNode;
		this.nodeOrder = nodeOrder;
	}
	
	public AbstractedGraphNode getStartNode() {
		return startNode;
	}
		
	public AbstractedGraphNode getEndNode() {
		return endNode;
	}

	public List<AbstractedGraphNode> getNodeOrder() {
		return nodeOrder;
	}
}
