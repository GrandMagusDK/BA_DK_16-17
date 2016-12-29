package graphGen;

import java.util.ArrayList;
import java.util.List;

import mapEditor.GridNode;

public class LowLevelGraphNode extends GridNode{
	private double gScore = Integer.MAX_VALUE;
	private double hScore = Integer.MAX_VALUE;
	private double fScore = Integer.MAX_VALUE;
	boolean intersection = false;
	private LowLevelGraphNode parent = null;
	private List<LowLevelGraphNode> neighbors = new ArrayList<LowLevelGraphNode>();
	private int numberOfTraversableNeighbors = 0;
	
	public LowLevelGraphNode(int x, int y) {
		super(x, y);
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
