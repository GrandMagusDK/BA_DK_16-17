package graphGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mapEditor.GridNode;

public class LowLevelGraphNode extends GridNode implements Serializable{
	
	private static final long serialVersionUID = 6554514388231226491L;
	
	private double gScore = Integer.MAX_VALUE;
	private double hScore = Integer.MAX_VALUE;
	private double fScore = Integer.MAX_VALUE;
	private LowLevelGraphNode parent = null;
	private List<LowLevelGraphNode> neighbors = new ArrayList<>();
	private int numberOfTraversableNeighbors = 0;
	boolean intersection = false;
	
	public LowLevelGraphNode() {
		super();
	}
	
	public LowLevelGraphNode(int x, int y) {
		super(x, y);
	}
	
	public LowLevelGraphNode(GridNode gridNode){
		super(gridNode.getX(), gridNode.getY(), gridNode.isTraversable());
	}
	
	public int getNumberOfTraversableNeighbors() {
		return numberOfTraversableNeighbors;
	}

	public void setNumberOfTraversableNeighbors(int numberOfTraversableNeighbors) {
		this.numberOfTraversableNeighbors = numberOfTraversableNeighbors;
	}

	public boolean isIntersection() {
		return intersection;
	}

	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}

	public List<LowLevelGraphNode> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbor(LowLevelGraphNode neighbor){
		neighbors.add(neighbor);
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

	public LowLevelGraphNode getParent() {
		return parent;
	}

	public void setParent(LowLevelGraphNode parent) {
		this.parent = parent;
	}
}
