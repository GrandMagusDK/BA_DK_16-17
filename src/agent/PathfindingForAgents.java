package agent;

import java.util.ArrayList;
import java.util.List;

public class PathfindingForAgents {
	private SimulationAgent agent;
	private List<AgentNode> openSet;
	private List<AgentNode> closedSet;
	private AgentNode startNode;
	private AgentNode endNode;

	public PathfindingForAgents(SimulationAgent agent) {
		this.agent = agent;
	}
	
	public List<AgentNode> findPath(AgentNode currentAgentNode, AgentNode targetNode) {
		boolean targetFound = false;
		endNode = targetNode;
		AgentNode currentNode;
		openSet = new ArrayList<>();
		closedSet = new ArrayList<>();
		List<AgentNode> path = new ArrayList<>();
		clearNodeValues();
		startNode = currentAgentNode;

		System.out.println("Finding Path for Agent " + agent.getId() + " for Nodes: "
				+ agent.getCurrentWorldCoord().getX() + ", " + agent.getCurrentWorldCoord().getY() + " and "
				+ targetNode.getPosition().getX() + ", " + targetNode.getPosition().getY());

		startNode.setgScore(0);
		startNode.sethScore(calculateHeuristics(startNode));
		startNode.setfScore(startNode.getgScore() + startNode.gethScore());
		openSet.add(startNode);

		while (!targetFound) {
			int index = findNextNode(openSet);
			if(index == -1){
				path = null;
				return path;
			}
			currentNode = openSet.get(index);
			moveToClosedSet(currentNode);
			findNeighbors(currentNode);
			List<AgentNode> neighbors = currentNode.getNeighbors();
			System.out.println("Current Node: " + currentNode.toString());
			//printCurrentPath(currentNode);
			if (currentNode.getPosition().equals(endNode.getPosition())) {
				targetFound = true;
				System.out.println("End Node found");
				break;
			}
			for (AgentNode neighbour : neighbors) {
				if (neighbour.isTraversable()) {
					addToOpenSet(neighbour, currentNode);
				}
			}
		}
		path = reconstructPath();
		return path;
	}

	private int findNextNode(List<AgentNode> openSet) {
		if(openSet.isEmpty()){
			System.out.println("Open set is empty");
			return -1;
		}
		AgentNode current = openSet.get(0);
		if (openSet.size() == 1) {
			return openSet.indexOf(current);
		}
		for (int i = 0; i < openSet.size(); i++) {
			if (openSet.get(i).getfScore() <= current.getfScore() && openSet.get(i) != current) {
				current = openSet.get(i);
			}
		}
		return openSet.indexOf(current);
	}

	private void addToOpenSet(AgentNode target, AgentNode currentNode) {
		if (closedSet.contains(target)) {
			return;
		}
		if (openSet.contains(target)) {
			checkForBetterPath(currentNode, target);
		} else {
			openSet.add(target);
			double h = calculateHeuristics(target);
			target.sethScore(h);
			target.setgScore(currentNode.getgScore() + 1);
			double f = target.getgScore() + h;
			target.setfScore(f);
			target.setParent(currentNode);
		}
	}

	private void checkForBetterPath(AgentNode currentNode, AgentNode target) {
		System.out.println("checkForBetterPath called for node " + target.getID());
		double xdif = target.getPosition().getX() - currentNode.getPosition().getX();
		double ydif = target.getPosition().getY() - currentNode.getPosition().getY();
		double distance = 0;
		if (xdif + ydif == 2) {
			distance = Math.sqrt(2);
		} else if (xdif + ydif == 1) {
			distance = 1;
		} else {
			distance = Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
		}
		if (target.getgScore() < (currentNode.getgScore() + distance)) {
			target.setParent(currentNode);
			target.setgScore((currentNode.getgScore() + distance));
			target.setfScore(target.getgScore() + target.gethScore());
		}
	}

	private void moveToClosedSet(AgentNode currentNode) {
		if (openSet.contains(currentNode))
			openSet.remove(currentNode);
		if (!closedSet.contains(currentNode))
			closedSet.add(currentNode);
	}

	private double calculateHeuristics(AgentNode node) {
		// Manhattan Distance
		int deltaX, deltaY;
		deltaX = Math.abs(node.getPosition().getX() - endNode.getPosition().getX());
		deltaY = Math.abs(node.getPosition().getY() - endNode.getPosition().getY());
		return deltaX + deltaY;
	}

	private List<AgentNode> reconstructPath() {
		List<AgentNode> path = new ArrayList<>();
		AgentNode currentNode = endNode;
		int i = 0;
		System.out.println("Found path: ");
		while (i < 250) {
			if (currentNode == startNode) {
				return path;
			} else {
				System.out.println(currentNode.toStringFull());
				path.add(0, currentNode);
				currentNode = currentNode.getParent();
				if(currentNode == null)
					break;
			}
			i++;
		}
		System.out.println("Left reconstruct loop, returning null for path.");
		return null;
	}

	private void findNeighbors(AgentNode node) {
		int x = node.getPosition().getX();
		int y = node.getPosition().getY();
		// right
		if (agent.isKnownPosition(new SimPosition(x, y+1))) {
			node.addNeighbor(agent.getNodeFromPosition(new SimPosition(x, y+1)));
		}
		// bottom
		if (agent.isKnownPosition(new SimPosition(x+1, y))) {
			node.addNeighbor(agent.getNodeFromPosition(new SimPosition(x+1, y)));
		}
		// left
		if (agent.isKnownPosition(new SimPosition(x, y-1))) {
			node.addNeighbor(agent.getNodeFromPosition(new SimPosition(x, y-1)));
		}
		// top
		if (agent.isKnownPosition(new SimPosition(x-1, y))) {
			node.addNeighbor(agent.getNodeFromPosition(new SimPosition(x-1, y)));
		}
	}
	
	private void clearNodeValues(){
		for(AgentNode agent : agent.getOwnMap()){
			agent.clearValues();
		}
	}
	
	private void printCurrentPath(AgentNode currentNode){
		AgentNode node = currentNode;
		System.out.println("Current Path: ");
		while(node.getParent() != null){
			if(node == startNode){
				System.out.println(node.toStringFull());
				break;
			}
			System.out.println(node.toStringFull() + " -> ");
			node = node.getParent();
		}
	}
}
