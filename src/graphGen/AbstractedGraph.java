package graphGen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AbstractedGraph {
	private List<AbstractedGraphNode> abstractedNodes;
	private List<AbstractedGraphEdge> abstractedEdges;
	private List<LowLevelGraphNode> intersections;
	private LowLevelGraphNode[][] lowLevelGraph;
	private List<LowLevelGraphPath> paths;
	private List<LowLevelGraphPath> directPaths;
	
	private List<LowLevelGraphNode> openSet = new ArrayList<>();
	private List<LowLevelGraphNode> closedSet = new ArrayList<>();
	private LowLevelGraphNode endNode;
	private LowLevelGraphNode startNode;
	
	
	public AbstractedGraph(List<LowLevelGraphNode> intersections, LowLevelGraphNode[][] locatedGraph) {
		this.intersections = intersections;
		this.lowLevelGraph = locatedGraph;
	}

	public void startProcessing(){
		//TODO 
		paths = generatePaths(); // generates paths while avoiding reverse paths
		System.out.println("Number of Paths: " + paths.size());
		logPaths(paths, 0);
		directPaths = findDirectPaths(); // finds all paths that just connect two intersections
		System.out.println("Number of directPaths: " + directPaths.size());
		logPaths(directPaths, 1);
		abstractedNodes = generateAbstractedGraphNodes(); // generates AGN and copies over positions form intersections
		abstractedEdges = generateAbstractedEdges(); // generates AGE and copies over lenght and nodes from directPaths
	}
	
	private List<LowLevelGraphPath> generatePaths(){
		List<LowLevelGraphPath> paths = new ArrayList<>();
		//TODO somehow used the same node for both start and end
		System.out.println(intersections.toString());
		for(int i=0; i<intersections.size();i++){
			for(int j = i + 1; j < intersections.size(); j++){ //this avoids generating reverses of already made paths
				//System.out.println("Nodes: " + i + ", " + j);
				LowLevelGraphPath path = AStarForDistance(intersections.get(i), intersections.get(j));
				paths.add(path);
			}
		}
		return paths;
	}
	
	private List<LowLevelGraphPath> findDirectPaths(){ // kicks out all paths that have crossed more then 1(startNode) intersection
		List<LowLevelGraphPath> directPaths = new ArrayList<>();
		
		for(int i = 0; i < paths.size(); i++){
			if(paths.get(i).getCrossedIntersections() <= 1){
				directPaths.add(paths.get(i));
			}
		}
		
		return directPaths;
	}

	private List<AbstractedGraphNode> generateAbstractedGraphNodes(){
		List<AbstractedGraphNode> newNodes = new ArrayList<>();
		for(int i = 0; i < intersections.size(); i++){
			AbstractedGraphNode newNode = new AbstractedGraphNode(intersections.get(i).getX(), intersections.get(i).getY());
			newNodes.add(newNode);
		}
		
		return newNodes;
	}
	
	private List<AbstractedGraphEdge> generateAbstractedEdges(){
		AbstractedGraphEdge newEdge;
		AbstractedGraphNode node1, node2;
		List<AbstractedGraphEdge> edges = new ArrayList<>();
		for(int i = 0; i < directPaths.size(); i++){
			node1 = findAGNfromLLGN(directPaths.get(i).getNode1());
			node2 = findAGNfromLLGN(directPaths.get(i).getNode2());
			if(node1 != null && node2 != null && node1 != node2){
				newEdge = new AbstractedGraphEdge(node1, node2, directPaths.get(i).getDistance());
				edges.add(newEdge);
			}
		}
		return edges;
	}
	
	private AbstractedGraphNode findAGNfromLLGN(LowLevelGraphNode llgn){
		AbstractedGraphNode agn = null;
		int x = llgn.getX();
		int y = llgn.getY();
		
		for(int i = 0; i < abstractedNodes.size(); i++){
			if(abstractedNodes.get(i).getX() == x && abstractedNodes.get(i).getY() == y){
				agn = abstractedNodes.get(i);
				break;
			}
		}
		return agn;
	}
	
	//A-Star Section
	
	private LowLevelGraphPath AStarForDistance(LowLevelGraphNode startNode, LowLevelGraphNode endNode){
		boolean targetFound = false;
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
 			//System.out.println("Current Node: " + currentNode.getX() + ", " + currentNode.getY());
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
				}
			}
		}
		resultPath = new LowLevelGraphPath(startNode, endNode, endNode.getgScore());
		recreatePath(endNode, startNode, resultPath);
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
	
	private void logPaths(List<LowLevelGraphPath> paths, int pathType){ // pathType 0 = paths, pathType 1 = directPaths
		String out = "";
		String filename = " ";
		for(int i = 0; i < paths.size(); i++){
			out += paths.get(i).toString() + '\n';
		}
		switch(pathType){
		case 0: filename = "pathsLog.log";
		break;
		case 1: filename = "direcPathsLog.log";
		}
		saveToFile(filename, out.getBytes());
	}
	
	boolean saveToFile(String filename, byte[] saveData) {
		try {
			Files.write(Paths.get(filename), saveData);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// public Graph operations
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
	
	public List<AbstractedGraphNode> getNodes() {
		return abstractedNodes;
	}


	public List<LowLevelGraphNode> getIntersections() {
		return intersections;
	}


	public LowLevelGraphNode[][] getLowLevelGraph() {
		return lowLevelGraph;
	}
	
	public List<AbstractedGraphEdge> getEdges(){
		return abstractedEdges;
	}
}
