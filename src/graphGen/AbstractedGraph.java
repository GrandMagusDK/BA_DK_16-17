package graphGen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AbstractedGraph {
	private List<AbstractedGraphNode> abstractedNodes;
	private List<AbstractedGraphEdge> uniqueEdges;
	private List<LowLevelGraphNode> intersections;
	private LowLevelGraphNode[][] lowLevelGraph;
	private List<LowLevelGraphNode> openSet = new ArrayList<>();
	private List<LowLevelGraphNode> closedSet = new ArrayList<>();
	private LowLevelGraphNode endNode;
	private LowLevelGraphNode startNode;
	
	public AbstractedGraph(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] locatedGraph) {
		this.intersections = intersections;
		this.lowLevelGraph = locatedGraph;
		abstractedNodes = generateAbstractedGraphNodes(intersections, lowLevelGraph);
		uniqueEdges = findUngiqueEdges();
	}
	
	
	private List<LowLevelGraphPath> generatePaths(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] locatedGraph){
		List<LowLevelGraphPath> paths = new ArrayList<>();
		
		for(int i=0; i<intersections.size();i++){
			for(int j=0;i<intersections.size();i++){
				if(j==i){
					continue;
				}
				LowLevelGraphPath path = AStarForDistance(intersections.get(i), intersections.get(j), lowLevelGraph);
				paths.add(path);
			}
		}
		return paths;
	}
	
	private List<LowLevelGraphPath> findConnectedEdges(LowLevelGraphNode intersection, List<LowLevelGraphPath> paths){
		
		List<LowLevelGraphPath> connectedEdges = new ArrayList<>();
		
		for(int i=0;i<paths.size(); i++){
			if(paths.get(i).node1 == intersection){
				if(!paths.get(i).crossesIntersection){
					connectedEdges.add(paths.get(i)); //simple paths give us the directly connected intersections aka edges in the graph
				}
			}
		}
		return connectedEdges;
	}
	
	private List<AbstractedGraphNode> generateAbstractedGraphNodes(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] lowLevelGraph){
		List<LowLevelGraphPath> paths = generatePaths(intersections, lowLevelGraph);
		List<LowLevelGraphPath> connectedEdges;
		AbstractedGraphNode[] newNodes = new AbstractedGraphNode[intersections.size()];
		
		for(int i=0;i<intersections.size();i++){
			connectedEdges = findConnectedEdges(intersections.get(i), paths);
			for(int j=0;j<connectedEdges.size();j++){
				
				AbstractedGraphNode node2 = newNodes[intersections.indexOf(connectedEdges.get(j))];
				double distance = connectedEdges.get(j).distance;
				AbstractedGraphEdge edge = new AbstractedGraphEdge(newNodes[i], node2, distance);
				//this basically rebuilds intersections as AGN in newNodes
				if(!newNodes[i].getConnectedEdges().contains(edge)){
					newNodes[i].addToConnectedEdges(edge);
				}
				if(!newNodes[i].getConnectedIntersections().contains(node2)){
					newNodes[i].addToConnectedIntersections(node2);
				}
			}
			newNodes[i].setGridPosition(intersections.get(i).getX(), intersections.get(i).getY());
		}
		return Arrays.asList(newNodes);
	}
	
	private List<AbstractedGraphEdge> findUngiqueEdges(){
		List<AbstractedGraphEdge> uniqueEdges = new ArrayList<>();
		List<AbstractedGraphNode> outList = new ArrayList<>();
		for(int i = 0; i < abstractedNodes.size(); i++){
			for(int j = 0; j < abstractedNodes.get(i).getConnectedEdges().size(); i++){
				if(!(outList.contains(abstractedNodes.get(i).getConnectedEdges().get(j).getNodes()[1]))){
					uniqueEdges.add(abstractedNodes.get(i).getConnectedEdges().get(j));
				}
			}
			outList.add(abstractedNodes.get(i));
		}
		return uniqueEdges;
	}
	
	//A-Star Section
	
	private LowLevelGraphPath AStarForDistance(LowLevelGraphNode startNode, LowLevelGraphNode endNode, LowLevelGraphNode[][] lowLevelGraph){
		boolean crossesOtherIntersection = false;
		boolean targetFound = false;
		LowLevelGraphNode currentNode;
		LowLevelGraphPath resultPath = null;
		this.endNode = endNode;
		this.startNode = startNode;
		
		startNode.setgScore(0);
		startNode.sethScore(calculateHeuristics(startNode));
		startNode.setfScore(startNode.getgScore() + startNode.gethScore());
		openSet.add(startNode);
		
		while(!targetFound){
			int index = findNextNode(openSet);
			currentNode = openSet.get(index);
			
			List<LowLevelGraphNode> neighbors = currentNode.getNeighbors();
 			 
			for(int i=0;i>neighbors.size();i++){
				if(neighbors.get(i).isTraversable()){
					if(currentNode == endNode){
						neighbors.get(i).setParent(currentNode);
						targetFound = true;
						endNode.setgScore(currentNode.getgScore()+1);
						break;
					}
					else
						addToOpenSet(neighbors.get(i), currentNode);
						if(currentNode.isIntersection())
							crossesOtherIntersection = true;
				}
			}
			moveToClosedSet(currentNode);
		}
		if(crossesOtherIntersection){
			resultPath = new LowLevelGraphPath(startNode, endNode, endNode.getgScore(), true);
		}
		else{
			resultPath = new LowLevelGraphPath(startNode, endNode, endNode.getgScore(), false);
		}
		return resultPath;
	}
	
	private int findNextNode(List<LowLevelGraphNode> openSet){ // search through the openSet for the lowest fScore
		LowLevelGraphNode current = openSet.get(0);
		for(int i = 1; i < openSet.size(); i++){
			//System.out.println(openSet.get(i).gridPosition() + " " + openSet.get(i).toString());
			if(openSet.get(i).getfScore() <= current.getfScore()){
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
		deltaX = Math.abs(node.getX() - endNode.getX());
		deltaY = Math.abs(node.getY() - endNode.getY());
		
		return deltaX + deltaY;
	}
	
	private void addToOpenSet(LowLevelGraphNode currentNode, LowLevelGraphNode node){
		if(openSet.contains(node)){
			checkForBetterPath(currentNode, node);
		}
		else
		{
			openSet.add(node);
			double h = calculateHeuristics(node);
			//System.out.println("HScore = " + h);
			node.sethScore(h);
			node.setgScore(currentNode.getgScore()+1);
			double f = node.getgScore() + h;
			//System.out.println("Fscore = " + f);
			node.setfScore(f);
			node.setParent(currentNode);
		}
		openSet.add(node);
	}
	
	private void moveToClosedSet(LowLevelGraphNode currentNode){
		//Move to closedSet and remove from openSet
		if(openSet.contains(currentNode))
			openSet.remove(currentNode);
		if(!closedSet.contains(currentNode))
			closedSet.add(currentNode);
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
	
	public List<AbstractedGraphNode> getAbstractedNodes() {
		return abstractedNodes;
	}


	public List<LowLevelGraphNode> getIntersections() {
		return intersections;
	}


	public LowLevelGraphNode[][] getLowLevelGraph() {
		return lowLevelGraph;
	}
	
	public List<AbstractedGraphEdge> getUniqueEdges(){
		return uniqueEdges;
	}
}
