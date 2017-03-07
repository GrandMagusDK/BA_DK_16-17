package agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;

public class IntelligentAgent extends SimulationAgent {

	private int waitCounter = 0;
	private boolean running = true;
	private boolean movingToUnexploredPoint = false;
	private List<AgentNode> pathToClosestUnknownPath = new ArrayList<>();
	private List<AgentNode> unknownPathNodes = new ArrayList<>();
	private List<AgentNode> exploredNodes = new ArrayList<>();
	private AgentNode tempIntraversable;

	public IntelligentAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, 1, 4, 2, facing, simGUI);
	}

	public IntelligentAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange,
			int sensorRange, FacingDirectionEnum facing, boolean test) {
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, test);
	}

	@Override
	public void run() {
		System.out.println("Intelligent Agent Thread running!");
		while (running) {
			doTurn();
			try {
				Thread.sleep(250); // actual value 250, lower for testing
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
			if (isKnownPosition(position)) {
				// updateMapNodeSensor(mapData.get(position), position);
			} else {
				newPositions.add(position);
			}
		}
		for (SimPosition pos : newPositions) {
			AgentNode newNode = new AgentNode();
			newNode.setPosition(pos);
			newNode.setTraversable(mapData.get(pos).isTraversable());
			newNode.setIntersection(mapData.get(pos).isIntersection());
			if (newNode.isTraversable()) {
				newNode.setUnexploredPathMarker(true);
				if (!exploredNodes.contains(newNode))
					unknownPathNodes.add(newNode);
			}
			ownMap.add(newNode);
			knownMapPositions.add(pos);
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
		SimPosition otherAgentPosition;
		int x, y;
		for (SimulationAgent otherAgent : agentsInRange) {
			otherAgentPosition = otherAgent.getCurrentWorldCoord();
			x = otherAgentPosition.getX() - getCurrentWorldCoord().getX();
			y = otherAgentPosition.getY() - getCurrentWorldCoord().getY();
			otherAgentPosition = new SimPosition(currentPosition.getX() + x, currentPosition.getY() + y);
			mergeMap(otherAgent, otherAgentPosition);
		}
	}

	@Override
	protected void nextMove() throws InterruptedException {
		// If already moving towards a Node use this.
		if (movingToUnexploredPoint) {
			movingToUnexploredPoint();
			return;
		}
		// Normal movement
		SimPosition newPosition = lookAhead();
		if (newPosition != null) {
			if (getNodeFromPosition(newPosition).isTraversable()) {
				moveTo(newPosition);
				return;
			}
		}
		// Hit a wall, way forward is barred. Choose nearest unexploredPath
		double x, y, distance;
		AgentNode nextNode = chooseNearestUnexploredNode();
		if (nextNode == null) {
			if (unknownPathNodes.isEmpty()) {
				System.out.println("All reachable Nodes discovered! Map complete.");
				System.out.println("Agent shutting down!");
				running = false;
				return;
			}
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
			if (unknownPathNodes.contains(currentNode)) {
				currentNode.setUnexploredPathMarker(false);
				unknownPathNodes.remove(currentNode);
			}
			if (!exploredNodes.contains(currentNode)) {
				exploredNodes.add(getNodeFromPosition(currentPosition));
			}
			getNodeFromPosition(currentPosition).checkOut(id);
			currentPosition = newPosition;
			System.out.println(
					"New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY());
			return;
		}
		System.out.println("Agent " + id + " could not check-in to Node: " + currentNode.toString());
		handleOccupiedTarget(currentNode);
	}

	private void handleOccupiedTarget(AgentNode node) throws InterruptedException {
		// TODO
		if (waitCounter > 5) {
			waitCounter++;
			nextMove();
		}
		waitCounter = 0;
		// TODO temporarily mark that space as intraversable and pathfind to
		// nearest unexplored. After that turn the space back to traversable
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
		}
	}

	private AgentNode chooseNearestUnexploredNode() {
		AgentNode newNode = null;
		double result = Integer.MAX_VALUE, x, y, distance;
		for (AgentNode node : unknownPathNodes) {
			x = node.getPosition().getX() - currentPosition.getX();
			y = node.getPosition().getY() - currentPosition.getY();
			distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			if (distance < result && distance != 0) {
				result = distance;
				newNode = node;
			}
		}
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
		if (path.get(0) == null) {
			System.out.println("path is null!");

		}
		pathToClosestUnknownPath = path;
	}

	private void rebuildUnExploredNodes() {
		unknownPathNodes.clear();
		for (AgentNode node : ownMap) {
			if (node.isUnexploredPathMarker()) {
				unknownPathNodes.add(node);
			} else {
				exploredNodes.add(node);
			}
		}
	}

	@Override
	protected void actionsAferMerge() {
		rebuildUnExploredNodes();
	}
}
