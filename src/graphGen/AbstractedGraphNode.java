package graphGen;

import java.util.List;

public class AbstractedGraphNode{

	private List<AbstractedGraphNode> neighbours;
	private double gScore = Integer.MAX_VALUE;
	private double hScore = Integer.MAX_VALUE;
	private double fScore = Integer.MAX_VALUE;
	private AbstractedGraphNode parent = null;
	
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
	
	public void addToNeighbours(AbstractedGraphNode intersection){
		if(!neighbours.contains(intersection))
			neighbours.add(intersection);
	}
	
	public List<AbstractedGraphNode> getNeighbours() {
		return neighbours;
	}

	public void setConnectedIntersections(List<AbstractedGraphNode> connectedIntersections) {
		this.neighbours = connectedIntersections;
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
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public double getgScore() {
		return gScore;
	}

	public void setgScore(double gScore) {
		this.gScore = gScore;
	}

	public double gethScore() {
		return hScore;
	}

	public void sethScore(double hScore) {
		this.hScore = hScore;
	}

	public double getfScore() {
		return fScore;
	}

	public void setfScore(double fScore) {
		this.fScore = fScore;
	}

	public AbstractedGraphNode getParent() {
		return parent;
	}

	public void setParent(AbstractedGraphNode parent) {
		this.parent = parent;
	}
}
