package agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;

public class IntelligentAgent extends SimulationAgent {

	private int waitCounter = 0;
	private int CommunicationCoolDown = 10;
	private boolean movingToUnexploredPoint = false;
	private List<AgentNode> pathToClosestUnknownPath = new ArrayList<>();
	private List<AgentNode> unknownPathNodes = new ArrayList<>();
	private AgentNode tempIntraversable;

	public IntelligentAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, 1, 3, 2, facing, simGUI);
		agentType = "Intelligent Agent";
	}

	public IntelligentAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange,
			int sensorRange, FacingDirectionEnum facing, boolean test) {
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, test);
		agentType = "Intelligent Agent";
	}

	@Override
	public void run() {
		System.out.println("Intelligent Agent Thread running!");
		agentStartTime = new Date();
		while (!agentDone) {
			doTurn();
			try {
				Thread.sleep(30); // actual value 250, lower for testing
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void timeOut() {
		//no used
	}
	
	@Override
	protected void scanSurroundings() {
		List<SimPosition> keysToDelete = new ArrayList<>();
		for (Map.Entry<SimPosition, LowLevelGraphNode> entry : mapData.entrySet()) {
			if (entry.getValue() == null)
				keysToDelete.add(entry.getKey());
		}
		for (SimPosition position : keysToDelete) {
			mapData.remove(position);
		}

		Set<SimPosition> positions = mapData.keySet();
		List<SimPosition> newPositions = new ArrayList<>();
		for (SimPosition position : positions) {
			if (!isKnownPosition(position))
				newPositions.add(position);
		}
		for (SimPosition pos : newPositions) {
			if (!isKnownPosition(pos)) {
				AgentNode newNode = new AgentNode();
				newNode.setPosition(pos);
				newNode.setTraversable(mapData.get(pos).isTraversable());
				newNode.setIntersection(mapData.get(pos).isIntersection());
				if (newNode.isTraversable()) {
					newNode.setUnexplored(true);
					if (!exploredNodes.contains(newNode))
						unknownPathNodes.add(newNode);
				}
				ownMap.add(newNode);
			}
		}
	}
	// TODO maybe something only specific agent types can see
	// private void updateMapNodeSensor(LowLevelGraphNode lowNode, SimPosition
	// position) {
	// AgentNode node = getNodeFromPosition(position);
	//
	//
	// }

	@Override
	protected void scanForCommunication() {
		SimPosition otherAgentPositionLocal;
		if (agentsInRange.isEmpty()) {
			System.out.println("no agents in range");
			return;
		}
		for (SimulationAgent otherAgent : agentsInRange) {
			if (recentAgentCommunication.containsKey(otherAgent.getId())) {
				continue;
			} else if (otherAgent.getOwnMap().size() > 50) {
				recentAgentCommunication.put(otherAgent.getId(), CommunicationCoolDown);
				otherAgentPositionLocal = otherAgent.getCurrentWorldCoord().minus(getCurrentWorldCoord());
				otherAgentPositionLocal = otherAgentPositionLocal.plus(getCurrentPosition());
				mergeMap(otherAgent, otherAgentPositionLocal);
			}
		}
	}

	@Override
	protected void nextMove() throws InterruptedException {
		// If already moving towards a Node use this.
		if (movingToUnexploredPoint) {
			movingToUnexploredPoint();
			return;
		}
		// Normal movement if Node ahead is traversable and unexplored
		SimPosition newPosition = lookAhead();
		AgentNode nextNode;
		if (newPosition != null) {
			nextNode = getNodeFromPosition(newPosition);
			if (nextNode.isTraversable() && nextNode.isUnexplored()) {
				moveTo(newPosition);
				return;
			}
		}
		// Hit a wall or Node ahead is already explored. Choose nearest
		// unexploredPath
		double x, y, distance;
		nextNode = chooseNearestUnexploredNode();
		if (nextNode == null) {
			agentIsDone(false);
			return;
		}
		if (nextNode.getPosition().equals(currentPosition)) {
			unknownPathNodes.remove(nextNode);
			nextMove();
			return;
		}
		System.out.println("Closest unexplored Node: x = " + nextNode.getPosition().getX() + ", y = "
				+ nextNode.getPosition().getY());
		x = nextNode.getPosition().getX() - currentPosition.getX();
		y = nextNode.getPosition().getY() - currentPosition.getY();
		distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		if (distance <= 1) {// if distance is 1 then node is adjacent, no need
							// to pathfind
			moveTo(nextNode.getPosition());
			return;
		} else {
			findPathToUnExploredNode(nextNode);
			movingToUnexploredPoint = true;
			nextMove(); // this creates no slow down in movement. If this wasn't
						// here Agent would wait to next cycle to move
		}
	}

	private void moveTo(SimPosition newPosition) throws InterruptedException {
		changeFacing(newPosition);
		AgentNode currentNode = getNodeFromPosition(currentPosition);
		if (currentNode.checkIn(id)) {
			currentNode.setUnexplored(false);
			if (unknownPathNodes.contains(currentNode))
				unknownPathNodes.remove(currentNode);
			if (!exploredNodes.contains(currentNode))
				exploredNodes.add(getNodeFromPosition(currentPosition));
			getNodeFromPosition(currentPosition).checkOut(id);
			currentPosition = newPosition;
			System.out.println(
					"[IA " + id + "] New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY());
			return;
		}
		System.out.println("Agent " + id + " could not check-in to Node: " + currentNode.toString());
		handleOccupiedTarget(currentNode);
	}

	private void handleOccupiedTarget(AgentNode node) throws InterruptedException {
		// TODO
		System.out.println("Agent " + id + " encountered occupied Node");
		if (waitCounter > 5) {
			waitCounter++;
			nextMove();
		}
		waitCounter = 0;
		// temporarily mark that space as intraversable, then pathfind
		tempIntraversable = node;
		tempIntraversable.setTraversable(false);
		AgentNode newNode = chooseNearestUnexploredNode();
		findPathToUnExploredNode(newNode);
	}

	protected void movingToUnexploredPoint() throws InterruptedException {
		AgentNode nextNode = null;
		if (!pathToClosestUnknownPath.isEmpty()) {
			nextNode = pathToClosestUnknownPath.get(0);
			if (nextNode != null) {
				moveTo(nextNode.getPosition());
			}
			pathToClosestUnknownPath.remove(0);
			if (pathToClosestUnknownPath.isEmpty()) {
				movingToUnexploredPoint = false;
				if (tempIntraversable != null) {
					tempIntraversable.setTraversable(true);
					tempIntraversable = null;
				}
			}
		} else {
			movingToUnexploredPoint = false;
		}
	}

	private AgentNode chooseNearestUnexploredNode() {
		AgentNode newNode = null;
		double result = Integer.MAX_VALUE, x, y, distance;
		for (AgentNode node : ownMap) {
			if (node.isUnexplored()) {
				if (node.isTraversable()) {
					x = node.getPosition().getX() - currentPosition.getX();
					y = node.getPosition().getY() - currentPosition.getY();
					distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
					if (distance < result && distance != 0) {
						result = distance;
						newNode = node;
					}
				}
			}
		}
		// for (AgentNode node : unknownPathNodes) {
		// if (node.isTraversable()) {
		// x = node.getPosition().getX() - currentPosition.getX();
		// y = node.getPosition().getY() - currentPosition.getY();
		// distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		// if (distance < result && distance != 0) {
		// result = distance;
		// newNode = node;
		// }
		// }
		// }
		return newNode;
	}

	private SimPosition lookAhead() {
		SimPosition position = currentPosition;
		int x = currentPosition.getX();
		int y = currentPosition.getY();
		switch (facing) {
		case TOP:
			y -= moveDistance;
			break;
		case RIGHT:
			x += moveDistance;
			break;
		case BOTTOM:
			y += moveDistance;
			break;
		case LEFT:
			x -= moveDistance;
		}
		position = new SimPosition(x, y);
		if (x >= 0 && y >= 0) {
			if (isKnownPosition(position))
				return position;
		}
		return null;
	}

	private void changeFacing(SimPosition targetPosition) {
		int x = targetPosition.getX() - currentPosition.getX();
		int y = targetPosition.getY() - currentPosition.getY();
		if (x == 1)
			facing = FacingDirectionEnum.RIGHT;
		else if (x == -1)
			facing = FacingDirectionEnum.LEFT;
		else if (y == 1)
			facing = FacingDirectionEnum.BOTTOM;
		else if (y == -1)
			facing = FacingDirectionEnum.TOP;
	}

	private void findPathToUnExploredNode(AgentNode nextNode) {
		PathfindingForAgents pathfinding = new PathfindingForAgents(this);
		List<AgentNode> path = pathfinding.findPath(getNodeFromPosition(currentPosition), nextNode);
		if (path == null) {
			System.out.println("Returned path is NULL for Agent " + id);
		} else
			pathToClosestUnknownPath = path;
	}

	private void rebuildUnExploredNodes() {
		unknownPathNodes.clear();
		for (AgentNode node : ownMap) {
			if (node.isUnexplored() && node.isTraversable()) {
				unknownPathNodes.add(node);
			} else {
				exploredNodes.add(node);
			}
		}
	}

	@Override
	protected void actionsAferOriginChange() {
		rebuildUnExploredNodes();
	}
}
