package agent;

import java.util.ArrayList;
import java.util.List;

public class AgentNode {
	private boolean traversable;
	private boolean unexploredPathMarker;
	private boolean intersection;
	private boolean isOccupied = false;
	private SimPosition worldPosition; //this is just for conveniance
	private SimPosition position = null;

	private double gScore = Integer.MAX_VALUE;
	private double hScore = Integer.MAX_VALUE;
	private double fScore = Integer.MAX_VALUE;
	private AgentNode parent = null;
	private List<AgentNode> neighbors = new ArrayList<>();
	
	private int id;
	private int numberOfAgentsOnOneSpace = 2;
	private int numberOfAgents = 0;
	private List<Integer> occupyingAgents = new ArrayList<>();
	private static int counter = 0;
	
	public AgentNode(){
		id = counter;
		counter++;
	}
	public AgentNode(SimPosition position, boolean traversable){
		this.position = position;
		this.traversable = traversable;
		id = counter;
		counter++;
	}
	
	public void clearValues() {
		gScore = Integer.MAX_VALUE;
		hScore = Integer.MAX_VALUE;
		fScore = Integer.MAX_VALUE;
		parent = null;
	}
	
	public boolean checkIn(int agentID){
		if(numberOfAgents< numberOfAgentsOnOneSpace){
			numberOfAgents++;
			if(numberOfAgents == numberOfAgentsOnOneSpace)
				isOccupied = true;
			return true;
		}
		return false;
	}
	
	public void checkOut(int agentID){
		numberOfAgents--;
		if(numberOfAgents < numberOfAgentsOnOneSpace)
			isOccupied = false;
	}
	public boolean isUnexploredPathMarker() {
		return unexploredPathMarker;
	}
	public void setUnexploredPathMarker(boolean unexploredPathMarker) {
		this.unexploredPathMarker = unexploredPathMarker;
	}
	public boolean isIntersection() {
		return intersection;
	}
	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}
	public boolean isTraversable() {
		return traversable;
	}
	public void setTraversable(boolean traversable){
		this.traversable = traversable;
	}
	public SimPosition getWorldPosition() {
		return worldPosition;
	}
	public void setWorldPosition(SimPosition worldPosition) {
		this.worldPosition = worldPosition;
	}
	public SimPosition getPosition() {
		return position;
	}
	public void setPosition(SimPosition currentPosition) {
		this.position = currentPosition;
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
	public AgentNode getParent() {
		return parent;
	}
	public void setParent(AgentNode parent) {
		this.parent = parent;
	}
	public List<AgentNode> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(List<AgentNode> neighbors) {
		this.neighbors = neighbors;
	}
	public void addNeighbor(AgentNode node) {
		if(!neighbors.contains(node))
			neighbors.add(node);
	}
	public int getID(){
		return id;
	}
	public boolean isOccupied() {
		return isOccupied;
	}
	public String toString(){
		return "AgentNode: ID: " + id + ", X: " + position.getX() + ", Y: " + position.getY();
	} 
	public String toStringFull(){
		String s = "ID: " + id + ", X: " + position.getX() + ", Y: " + position.getY();
		if(parent != null)
			s = s + ", Parent ID: " + parent.getID();
		else
			s = s + ", Parent ID: NULL";
		return s;
	}
}
