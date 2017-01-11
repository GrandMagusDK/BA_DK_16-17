package graphGen;

import java.util.List;

public class AbstractedGraphNode{

//	private List<AbstractedGraphNode> connectedIntersections;
//	private List<AbstractedGraphEdge> connectedEdges;
	private int[] gridPosition = new int[2];
	private int x, y;
	
	public AbstractedGraphNode(int[] gridPosition){
		this.gridPosition = gridPosition;
		this.x = gridPosition[0];
		this.y = gridPosition[1];
	}
	
	public AbstractedGraphNode(int x, int y){
		this.gridPosition = new int[]{x, y};
		this.x = x;
		this.y = y;
	}
	
//	public void addToConnectedIntersections(AbstractedGraphNode intersection){
//		if(!connectedIntersections.contains(intersection))
//			connectedIntersections.add(intersection);
//	}
//	
//	public void addToConnectedEdges(AbstractedGraphEdge edge){
//		if(!connectedEdges.contains(edge))
//			connectedEdges.add(edge);
//	}
//	
//	public List<AbstractedGraphNode> getConnectedIntersections() {
//		return connectedIntersections;
//	}
//
//	public void setConnectedIntersections(List<AbstractedGraphNode> connectedIntersections) {
//		this.connectedIntersections = connectedIntersections;
//	}
//
//	public List<AbstractedGraphEdge> getConnectedEdges() {
//		return connectedEdges;
//	}
//
//	public void setConnectedEdges(List<AbstractedGraphEdge> connectedEdges) {
//		this.connectedEdges = connectedEdges;
//	}
	
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
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
}
