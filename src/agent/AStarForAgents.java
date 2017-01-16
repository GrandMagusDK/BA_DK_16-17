package agent;

import java.util.ArrayList;
import java.util.List;

import graphGen.AbstractedGraph;
import graphGen.AbstractedGraphEdge;
import graphGen.AbstractedGraphNode;
import graphGen.LowLevelGraphNode;
import graphGen.LowLevelGraphPath;

public class AStarForAgents {
	
	private AbstractedGraph abstractedGraph;
	private SimPosition startPosition;
	private SimPosition endPosition;
	//Low
	private List<LowLevelGraphNode> openSetLow = new ArrayList<>();
	private List<LowLevelGraphNode> closedSetLow = new ArrayList<>();
	private LowLevelGraphNode startNodeLow;
	private LowLevelGraphNode endNodeLow;
	//Top
	private AbstractedGraphNode startNodeTop;
	private AbstractedGraphNode endNodeTop;
	private List<AbstractedGraphNode> openSetTop = new ArrayList<>();
	private List<AbstractedGraphNode> closedSetTop = new ArrayList<>();
	
	public AStarForAgents(AbstractedGraph abstractedGraph) {
		this.abstractedGraph = abstractedGraph;
	}
	
	public SimulationCompletePath findPath(SimPosition startPosition, SimPosition endPosition){
		SimulationCompletePath completePath = null;
		SimulationPathTop topPath = findPathTop();
		
		//TODO findLow Paths between each AGN from topPath and compile them into completePath
		
		return completePath;
	}
	
	//Top Pathfinding
	private SimulationPathTop findPathTop(){
		startNodeTop = null;
		endNodeTop = null;
		for(int i = 0; i < abstractedGraph.getNodes().size(); i++){
			if(abstractedGraph.getNodes().get(i).getX() == startPosition.getX() && abstractedGraph.getNodes().get(i).getX() == startPosition.getY()){
				startNodeTop = abstractedGraph.getNodes().get(i);
			}
			if(abstractedGraph.getNodes().get(i).getX() == endPosition.getX() && abstractedGraph.getNodes().get(i).getX() == endPosition.getX()){
				endNodeTop = abstractedGraph.getNodes().get(i);
			}
		}
		return aStarTop();
	}
	
	private SimulationPathTop aStarTop(){
		//TODO
		boolean targetFound = false;
		AbstractedGraphNode currentNode;
		SimulationPathTop resultPath = null;
		
		startNodeTop.setgScore(0);
		startNodeTop.sethScore(calculateTopHeuristics(startNodeTop));
		startNodeTop.setfScore(startNodeTop.getgScore() + startNodeTop.gethScore());
		openSetTop.clear();
		closedSetLow.clear();
		openSetTop.add(startNodeTop);
		
		while(!targetFound){
			int index = findNextTopNode();
			currentNode = openSetTop.get(index);
			List<AbstractedGraphNode> neighbours = currentNode.getNeighbours();
			for(int i = 0; i < neighbours.size(); i++){
				if(currentNode == endNodeTop){
					neighbours.get(i).setParent(currentNode);
					targetFound = true;
					AbstractedGraphEdge edge = abstractedGraph.getEdge(currentNode, neighbours.get(i));
					endNodeTop.setgScore(currentNode.getgScore() + edge.getDistance());
					break;
				}
				else{
					addToOpenSetTop(currentNode, neighbours.get(i));
				}
			}
		}
		//TODO 
		resultPath = reconstructTopPath();
		return resultPath;
	}
	
	private int findNextTopNode(){
		AbstractedGraphNode nextNode = openSetTop.get(0);
		if(openSetTop.size() == 1){
			return openSetTop.indexOf(nextNode);
		}
		for(int i = 0; i < openSetTop.size(); i++){
			if(openSetTop.get(i).getfScore() <= nextNode.getgScore()){
				nextNode = openSetTop.get(i);
			}
		}
		return openSetTop.indexOf(nextNode);
	}
	
	private void addToOpenSetTop(AbstractedGraphNode currentNode, AbstractedGraphNode newNode){
		if(closedSetTop.contains(newNode)){
			return;
		}
		else if(openSetTop.contains(newNode)){
			//TODO
			checkForBetterTopPath(currentNode, newNode);
		}
		else{
			AbstractedGraphEdge edge = abstractedGraph.getEdge(currentNode, newNode);
			
			newNode.setgScore(newNode.getParent().getgScore() + edge.getDistance());
			newNode.sethScore(calculateTopHeuristics(newNode));
			newNode.setfScore(newNode.getgScore() + newNode.gethScore());
			newNode.setParent(currentNode);
			openSetTop.add(newNode);
		}
	}
	
	private void checkForBetterTopPath(AbstractedGraphNode currentNode, AbstractedGraphNode node){
		AbstractedGraphEdge edge = abstractedGraph.getEdge(currentNode, node);
		if(currentNode.getgScore() + edge.getDistance() < node.getgScore()){
			node.setgScore(currentNode.getgScore() + edge.getDistance());
			node.setfScore(node.getgScore() + node.gethScore());
			node.setParent(currentNode);
		}
	}
	
	private int calculateTopHeuristics(AbstractedGraphNode node){
		//Manhattan Distance
		int deltaX, deltaY;
		deltaX = Math.abs(node.getX() - endNodeLow.getX());
		deltaY = Math.abs(node.getY() - endNodeLow.getY());
		
		return deltaX + deltaY;
	}
	
	private SimulationPathTop reconstructTopPath(){
		SimulationPathTop path;
		List<AbstractedGraphNode> order = new ArrayList<>();
		//TODO
		AbstractedGraphNode currentNode = endNodeTop;
		order.add(endNodeTop);
		int i = 0;
		while(i < 1000){
			if(currentNode.getParent() == startNodeTop){
				order.add(currentNode);
				path = new SimulationPathTop(startNodeTop, endNodeTop, order);
				return path;
			}
			else{
				order.add(currentNode.getParent());
				currentNode = currentNode.getParent();
			}
		}
		return null;
	}
	
	//Low Pathfinding
	private LowLevelGraphPath findPathLow(SimPosition startPosition, SimPosition endPosition){
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		
		return aStarLow();
	}
	
	private LowLevelGraphPath aStarLow(){
		boolean targetFound = false;
		LowLevelGraphNode currentNode;
		LowLevelGraphPath resultPath = null;
		
		System.out.println("AStar for Nodes: " + startNodeLow.getX() + ", " + startNodeLow.getY() + " and " + endNodeLow.getX() + ", " + endNodeLow.getY());
		
		startNodeLow.setgScore(0);
		startNodeLow.sethScore(calculateHeuristics(startNodeLow));
		startNodeLow.setfScore(startNodeLow.getgScore() + startNodeLow.gethScore());
		openSetLow.clear();
		closedSetLow.clear();
		openSetLow.add(startNodeLow);
		
		while(!targetFound){
			int index = findNextNode(openSetLow);
			currentNode = openSetLow.get(index);
			moveToClosedSet(currentNode);
			List<LowLevelGraphNode> neighbors = currentNode.getNeighbors();
 			//System.out.println("Current Node: " + currentNode.getX() + ", " + currentNode.getY());
			for(int i=0;i < neighbors.size();i++){
				if(currentNode.getX() == endNodeLow.getX() && currentNode.getY() == endNodeLow.getY()){
					neighbors.get(i).setParent(currentNode);
					targetFound = true;
					endNodeLow.setgScore(currentNode.getgScore()+1);
					System.out.println("End Node found");
					break;
				}
				else if(neighbors.get(i).isTraversable()){
					addToOpenSet(neighbors.get(i), currentNode);
				}
			}
		}
		resultPath = new LowLevelGraphPath(startNodeLow, endNodeLow, endNodeLow.getgScore());
		recreatePath(endNodeLow, startNodeLow, resultPath);
		return resultPath;
	}
	
	private int findNextNode(List<LowLevelGraphNode> openSet){ // search through the openSet for the lowest fScore
		//System.out.println("OpenSet Size: " + openSet.size());
		LowLevelGraphNode current = openSet.get(0);
		if(openSet.size() == 1){
			return openSet.indexOf(current);
		}
		for(int i = 0; i < openSet.size(); i++){
			//System.out.println(openSet.get(i).gridPosition() + " " + openSet.get(i).toString());
			if(openSet.get(i).getfScore() <= current.getfScore() && openSet.get(i) != current){
				current = openSet.get(i);
			}
			// the <= means that later found nodes with the same score are preferred
		}
		//System.out.println("Chosen: " + current.gridPosition() + " " + current.toString());
		return openSet.indexOf(current);
	}
	
	private int calculateHeuristics(LowLevelGraphNode node){
		//Manhattan Distance
		int deltaX, deltaY;
		deltaX = Math.abs(node.getX() - endNodeLow.getX());
		deltaY = Math.abs(node.getY() - endNodeLow.getY());
		
		return deltaX + deltaY;
	}
	
	private void addToOpenSet(LowLevelGraphNode node, LowLevelGraphNode currentNode){
		if(closedSetLow.contains(node)){
			return;
		}
		else if(openSetLow.contains(node)){
			checkForBetterPath(currentNode, node);
		}
		else
		{
			openSetLow.add(node);
			double h = calculateHeuristics(node);
			//System.out.println("HScore = " + h);
			node.sethScore(h);
			node.setgScore(currentNode.getgScore()+1);
			double f = node.getgScore() + h;
			//System.out.println("Fscore = " + f);
			node.setfScore(f);
			node.setParent(currentNode);
		}
	}
	
	private void moveToClosedSet(LowLevelGraphNode currentNode){
		//Move to closedSet and remove from openSet
		if(openSetLow.contains(currentNode))
			openSetLow.remove(currentNode);
		if(!closedSetLow.contains(currentNode))
			closedSetLow.add(currentNode);
	}
	
	private void checkForBetterPath(LowLevelGraphNode active, LowLevelGraphNode target){ //check if the current node is a better way to reach the node in question.
		double xdif = target.getX() - active.getX();
		double ydif = target.getY() - active.getY();
		double distance = 0;
		if(xdif+ydif==2){
			distance = Math.sqrt(2);
		}else if(xdif+ydif==1){
			distance = 1;
		}else{
			distance = Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
		}
		if(target.getgScore() < (active.getgScore() + distance)){
			target.setParent(active);
			target.setgScore((active.getgScore() + distance));
			target.setfScore(target.getgScore() + target.gethScore());
		}
	}
	
	private void recreatePath(LowLevelGraphNode endNode, LowLevelGraphNode startNode, LowLevelGraphPath path){
		LowLevelGraphNode node = endNode;
		int i = 0;
		int crossedIntersections = 0;
		String pathString = "EndNode: " + endNode.getX() + " " + endNode.getY() + ", StartNode: " + startNode.getX() + " "  + startNode.getY() + '\n';
		while(i < 1000)
		{
			if(node.isIntersection()){
				crossedIntersections++;
			}
			pathString += node.getX() + " " + node.getY() + " -> ";
			if(node.getParent() == startNode)
			{
				pathString += startNode.getX() + " " + startNode.getY() + " END" + '\n';
				pathString += "Crossed Intersections: " + crossedIntersections + '\n';
				break;
			}
			node = node.getParent();
			i++;
		}
		path.setCrossesIntersections(crossedIntersections);
		path.setPathString(pathString);
	}
	
}
