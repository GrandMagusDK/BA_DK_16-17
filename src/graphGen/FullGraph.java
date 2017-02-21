package graphGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import agent.SimPosition;

public class FullGraph implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8717883847186641672L;
	List<AbstractedGraphNode> abstractedNodes;
	List<AbstractedGraphEdge> abstractedEdges;
	LowLevelGraph lowLevelGraph;

	public FullGraph(List<AbstractedGraphNode> abstractedNodes, List<AbstractedGraphEdge> abstractedEdges, LowLevelGraph lowLevelGraph){
		this.abstractedNodes = abstractedNodes;
		this.abstractedEdges = abstractedEdges;
		this.lowLevelGraph = lowLevelGraph;
	}
	
	public AbstractedGraphEdge getEdge(AbstractedGraphNode node1, AbstractedGraphNode node2){
		AbstractedGraphEdge edge = null;
		for(int i = 0; i < abstractedEdges.size(); i++){
			if(abstractedEdges.get(i).getNodes().contains(node1) && abstractedEdges.get(i).getNodes().contains(node2)){
				edge = abstractedEdges.get(i);
				break;
			}
		}
		return edge;
	}
	
	public List<AbstractedGraphNode> getConnectedNodes(AbstractedGraphNode node){
		List<AbstractedGraphNode> nodes = new ArrayList<>();
		for(int i = 0; i < abstractedEdges.size(); i++){
			if(abstractedEdges.get(i).getNodes().contains(node)){
				for(int j = 0; j < abstractedEdges.get(i).getNodes().size(); j++){
					if(abstractedEdges.get(i).getNodes().get(j) != node){
						nodes.add(abstractedEdges.get(i).getNodes().get(j));
					}
				}
			}
		}
		
		return nodes;
	}
	
	public AbstractedGraphNode getNode(SimPosition position){
		AbstractedGraphNode result = null;
		for(int i = 0; i < abstractedNodes.size(); i++){
			if(abstractedNodes.get(i).getX() == position.getX() && abstractedNodes.get(i).getY() == position.getY() ){
				result = abstractedNodes.get(i);
				break;
			}
		}
		return result;
	}
	
	public List<AbstractedGraphNode> getNodes() {
		return abstractedNodes;
	}

	public LowLevelGraph getLowLevelGraph() {
		return lowLevelGraph;
	}
	
	public List<AbstractedGraphEdge> getEdges(){
		return abstractedEdges;
	}
}
