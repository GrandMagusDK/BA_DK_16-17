package graphGen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractedGraph {
	private List<AbstractedGraphNode> abstractedNodes;
	//private List<AbstractedGraphEdge> uniqueEdges;
	private List<LowLevelGraphNode> intersections;
	private LowLevelGraphNode[][] lowLevelGraph;
	private List<LowLevelGraphNode> openSet = new ArrayList<>();
	private List<LowLevelGraphNode> closedSet = new ArrayList<>();
	private LowLevelGraphNode endNode;
	private LowLevelGraphNode startNode;
	private List<LowLevelGraphPath> paths;
	private List<LowLevelGraphPath> uniqueEdges;
	
	public AbstractedGraph(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] locatedGraph) {
		this.intersections = intersections;
		this.lowLevelGraph = locatedGraph;
	}
	
	public void startProcessing(){
		paths = generatePaths(intersections, lowLevelGraph);
		//TODO generatePaths now does not generate duplicate paths. This allows to make finding connected edges easier
		uniqueEdges = findUngiqueEdges();
		abstractedNodes = generateAbstractedGraphNodes(intersections, lowLevelGraph);
		
	}
	
	
	private List<LowLevelGraphPath> generatePaths(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] locatedGraph){
		List<LowLevelGraphPath> paths = new ArrayList<>();
		
		for(int i=0; i<intersections.size();i++){
			for(int j = i + 1; i < intersections.size(); i++){ //this avoids generating reverses of already made paths
				LowLevelGraphPath path = AStarForDistance(intersections.get(i), intersections.get(j), lowLevelGraph);
				paths.add(path);
			}
		}
		return paths;
	}
	
	private List<LowLevelGraphPath> findConnectedEdges(LowLevelGraphNode intersection, List<LowLevelGraphPath> paths){
		
		List<LowLevelGraphPath> connectedEdges = new ArrayList<>();
		
		for(int i=0;i<paths.size(); i++){
			if(paths.get(i).getNode1() == intersection){
				if(!paths.get(i).isCrossingIntersection()){
					connectedEdges.add(paths.get(i)); //simple paths give us the directly connected intersections aka edges in the graph
				}
			}
		}
		return connectedEdges;
	}
	
	private List<AbstractedGraphNode> generateAbstractedGraphNodes(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] lowLevelGraph){
		List<LowLevelGraphPath> paths = generatePaths(intersections, lowLevelGraph);
		List<LowLevelGraphPath> connectedEdges;
		AbstractedGraphNode otherNode = null;
		AbstractedGraphNode[] newNodes = initializeNewNodes(intersections);
		
		for(int i=0;i<intersections.size();i++){
			connectedEdges = findConnectedEdges(intersections.get(i), paths);
			for(int j=0;j<connectedEdges.size();j++){
				if(connectedEdges.get(j).getNode1() != intersections.get(i)){
					int index = intersections.indexOf(connectedEdges.get(j).getNode1());
					otherNode = newNodes[index];
				} else if(connectedEdges.get(j).getNode2() != intersections.get(i)){
					int index = intersections.indexOf(connectedEdges.get(j).getNode2());
					otherNode = newNodes[index];
				} else{
					System.err.println("Error in generateAbstractGraphNodes");
				}
				
				double distance = connectedEdges.get(j).getDistance();
				AbstractedGraphEdge edge = new AbstractedGraphEdge(newNodes[i], otherNode, distance);
				//this basically rebuilds intersections as AGN in newNodes
				if(!newNodes[i].getConnectedEdges().contains(edge)){
					newNodes[i].addToConnectedEdges(edge);
				}
				if(!newNodes[i].getConnectedIntersections().contains(otherNode)){
					newNodes[i].addToConnectedIntersections(otherNode);
				}
			}
		}
		return Arrays.asList(newNodes);
	}
	
	private AbstractedGraphNode[] initializeNewNodes(List<LowLevelGraphNode> intersectionsSize){
		AbstractedGraphNode[] newNodes = new AbstractedGraphNode[intersections.size()];
		
		for(int i = 0; i < intersections.size(); i++){
			int[] gridPos = new int[2];
			gridPos[0] = intersections.get(i).getX();
			gridPos[1] = intersections.get(i).getY();
			AbstractedGraphNode newNode = new AbstractedGraphNode(gridPos);
			newNodes[i] = newNode;
		}
		
		return newNodes;
	}
	
	private List<LowLevelGraphPath> findUngiqueEdges(){ // TODO kicks out all paths that have crossed more then 1(startNode) intersection
		List<LowLevelGraphPath> uniqueEdges = new ArrayList<>();
		List<LowLevelGraphNode> outList = new ArrayList<>();
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
		boolean targetFound = false;
		int crossesOtherIntersection = 0;
		LowLevelGraphNode currentNode;
		LowLevelGraphPath resultPath = null;
		this.endNode = endNode;
		this.startNode = startNode;
		System.out.println("AStar for Nodes: " + startNode.getX() + ", " + startNode.getY() + " and " + endNode.getX() + ", " + endNode.getY());
		
		startNode.setgScore(0);
		startNode.sethScore(calculateHeuristics(startNode));
		startNode.setfScore(startNode.getgScore() + startNode.gethScore());
		openSet.clear();
		closedSet.clear();
		openSet.add(startNode);
		
		while(!targetFound){
			int index = findNextNode(openSet);
			currentNode = openSet.get(index);
			moveToClosedSet(currentNode);
			List<LowLevelGraphNode> neighbors = currentNode.getNeighbors();
 			System.out.println("Current Node: " + currentNode.getX() + ", " + currentNode.getY());
			for(int i=0;i < neighbors.size();i++){
				if(currentNode.getX() == endNode.getX() && currentNode.getY() == endNode.getY()){
					neighbors.get(i).setParent(currentNode);
					targetFound = true;
					endNode.setgScore(currentNode.getgScore()+1);
					System.out.println("End Node found");
					break;
				}
				else if(neighbors.get(i).isTraversable()){
					addToOpenSet(neighbors.get(i), currentNode);
					if(currentNode.isIntersection())
							crossesOtherIntersection++;
				}
			}
		}
		String pathOrder = recreatePath(endNode, startNode);
		if(crossesOtherIntersection <= 1){
			resultPath = new LowLevelGraphPath(startNode, endNode, endNode.getgScore(), true);
		}
		else{
			resultPath = new LowLevelGraphPath(startNode, endNode, endNode.getgScore(), false);
			System.out.println(pathOrder);
		}
		resultPath.setPathString(pathOrder);
		return resultPath;
	}
	
	private int findNextNode(List<LowLevelGraphNode> openSet){ // search through the openSet for the lowest fScore
		System.out.println("OpenSet Size: " + openSet.size());
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
		deltaX = Math.abs(node.getX() - endNode.getX());
		deltaY = Math.abs(node.getY() - endNode.getY());
		
		return deltaX + deltaY;
	}
	
	private void addToOpenSet(LowLevelGraphNode node, LowLevelGraphNode currentNode){
		if(closedSet.contains(node)){
			return;
		}
		else if(openSet.contains(node)){
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
	
	private String recreatePath(LowLevelGraphNode endNode, LowLevelGraphNode startNode){
		LowLevelGraphNode node = endNode;
		int i = 0;
		String path = "Path: /n";
		path += "EndNode ";
		while(i < 1000)
		{
			path += node.getX() + " " + node.getY() + " -> ";
			if(node.getParent() == startNode)
			{
				path += "StartNode " + startNode.getX() + " " + startNode.getY();
				break;
			}
			node = node.getParent();
			i++;
		}
		return path;
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
