package agent;

import java.util.List;

import graphGen.LowLevelGraphNode;

public class SimulationPathLow {
	private LowLevelGraphNode startNode, endNode;
	private List<LowLevelGraphNode> nodeOrder;
	
	public SimulationPathLow(LowLevelGraphNode startNode, LowLevelGraphNode endNode, List<LowLevelGraphNode> nodeOrder){
		this.startNode = startNode;
		this.endNode = endNode;
		this.nodeOrder = nodeOrder;
	}
	
	public LowLevelGraphNode getStartNode() {
		return startNode;
	}
	
	public LowLevelGraphNode getEndNode() {
		return endNode;
	}

	public List<LowLevelGraphNode> getNodeOrder() {
		return nodeOrder;
	}
	@Override
	public String toString(){
		String result = "";
		for(int i = 0 ; i < nodeOrder.size(); i++){
			result += "[" + nodeOrder.get(i).getX() + ", " + nodeOrder.get(i).getY() + "]";
			if(nodeOrder.get(i) != endNode){
				result += " -> ";
			}
		}
		result += '\n';
		return result;
	}
}
