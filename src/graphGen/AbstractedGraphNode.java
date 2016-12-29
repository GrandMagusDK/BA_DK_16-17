package graphGen;

import java.util.List;

public class AbstractedGraphNode{

	private List<AbstractedGraphNode> connectedIntersections;
	private List<AbstractedGraphEdge> connectedEdges;
	private int[] gridPosition = new int[2];
	
	public AbstractedGraphNode(int[] gridPosition){
		
	}
	
	public AbstractedGraphNode(int[] gridPosition, List<AbstractedGraphEdge> connectedEdges) {
		this.connectedEdges = connectedEdges;
		readIntersectionsFromEdges();
	}

	public AbstractedGraphNode(int[] gridPosition, List<AbstractedGraphEdge> connectedEdges, List<AbstractedGraphNode> connectedIntersections) {
		this.connectedIntersections = connectedIntersections;
		this.connectedEdges = connectedEdges;
	}

	public void readIntersectionsFromEdges() {
		for(int i=0;i<connectedEdges.size();i++){
			AbstractedGraphNode node = connectedEdges.get(i).getNodes()[0];
			if(node == this){
				node = connectedEdges.get(i).getNodes()[1];
			}else if(node == this){
				continue;
			}
			if(!connectedIntersections.contains(node)){
				connectedIntersections.add(node);
			}
		}
	}
	
	public void addToConnectedIntersections(AbstractedGraphNode intersection){
		if(!connectedIntersections.contains(intersection))
			connectedIntersections.add(intersection);
	}
	
	public void addToConnectedEdges(AbstractedGraphEdge edge){
		if(!connectedEdges.contains(edge))
			connectedEdges.add(edge);
	}
	
	public List<AbstractedGraphNode> getConnectedIntersections() {
		return connectedIntersections;
	}

	public void setConnectedIntersections(List<AbstractedGraphNode> connectedIntersections) {
		this.connectedIntersections = connectedIntersections;
	}

	public List<AbstractedGraphEdge> getConnectedEdges() {
		return connectedEdges;
	}

	public void setConnectedEdges(List<AbstractedGraphEdge> connectedEdges) {
		this.connectedEdges = connectedEdges;
	}
	
	public int[] getGridPosition(){
		return gridPosition;
	}
	
	public void setGridPosition(int[] gridPosition){
		this.gridPosition = gridPosition;
	}
	
	public void setGridPosition(int x, int y){
		this.gridPosition[0] = x;
		this.gridPosition[1] = y;
	}
}
