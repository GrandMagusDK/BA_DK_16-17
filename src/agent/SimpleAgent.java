package agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;

public class SimpleAgent extends SimulationAgent {
	protected int waitCounter = 0;
	protected int CommunicationCoolDown = 5;
	protected List<AgentIntersection> intersections = new ArrayList<>();
	private List<AgentNode> unknownPathNodes = new ArrayList<>();

	public SimpleAgent(int id, SimPosition startPosition, FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, 1, 3, 2, facing, simGUI);
		currentNode = getNodeFromPosition(currentPosition);
		currentNode.setUnexplored(false);
		agentType = "Simple Agent";
	}

	public SimpleAgent(int id, SimPosition startPosition, int moveDistance, int communicationRange, int sensorRange,
			FacingDirectionEnum facing, SimulationGUI simGUI) {
		super(id, startPosition, moveDistance, communicationRange, sensorRange, facing, simGUI);
		currentNode = getNodeFromPosition(currentPosition);
		currentNode.setUnexplored(false);
		agentType = "Simple Agent";
	}

	@Override
	public void run() {
		System.out.println("SimpleAgent Thread running!");
		agentStartTime = new Date();
		while (!agentDone) {
			doTurn();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void timeOut() {
		if(turnCounter >= 900){
			agentIsDone(true);
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

	// private void updateMapNodeSensor(LowLevelGraphNode lowNode, SimPosition
	// position){
	// AgentNode node = getNodeFromPosition(position);
	// //TODO
	// }

	@Override
	protected void scanForCommunication() {
		SimPosition otherAgentPositionLocal;
		if (agentsInRange.isEmpty())
			return;
		for (SimulationAgent otherAgent : agentsInRange) {
			if (recentAgentCommunication.containsKey(otherAgent.getId())) {
				continue;
			} else if (otherAgent.getOwnMap().size() > 20) {
				recentAgentCommunication.put(otherAgent.getId(), CommunicationCoolDown);
				otherAgentPositionLocal = otherAgent.getCurrentWorldCoord().minus(getCurrentWorldCoord());
				otherAgentPositionLocal = otherAgentPositionLocal.plus(getCurrentPosition());
				mergeMap(otherAgent, otherAgentPositionLocal);
			}
		}
	}

	@Override
	protected void nextMove() throws InterruptedException {
		SimPosition newPosition = null;
		AgentNode newNode = null;
		if (!unexploredPathsLeft()) {
			agentIsDone(false);
			return;
		}
		if (isKnownIntersecion(currentPosition)) {
			newPosition = getIntersectionFromPosition(currentPosition).findUnexploredDirection(facing);
			if (newPosition == null) {
				newPosition = chooseNextPosition(translateFacingToInt(facing));
			}
		} else {
			newPosition = chooseNextPosition(translateFacingToInt(facing));
		}
		newNode = getNodeFromPosition(newPosition);
		if (newNode.isIntersection()) {
			if (!isKnownIntersecion(newPosition)) {
				AgentIntersection intersection = generateIntersection(newPosition);
				intersections.add(intersection);
			} else {
				setIntersectionPathEnter(getIntersectionFromPosition(newPosition), facing);
			}
		}
		moveTo(newNode);
	}

	protected boolean unexploredPathsLeft() {
		for (AgentNode node : ownMap) {
			if (node.isUnexplored())
				return true;
		}
		return false;
	}

	protected void moveTo(AgentNode nextNode) throws InterruptedException {
		AgentIntersection intersection = getIntersectionFromPosition(currentPosition);
		if (nextNode.checkIn(id)) {
			facing = changeFacing(nextNode.getPosition());
			if (isKnownIntersecion(currentPosition))
				setIntersectionPathExit(intersection, facing);
			getNodeFromPosition(currentPosition).checkOut(id);
			nextNode.setUnexplored(false);
			currentPosition = nextNode.getPosition();
			System.out.println(
					"[SA " + id + "] New position for Agent " + id + ": " + currentPosition.getX() + ", " + currentPosition.getY());
			return;
		} else {
			handleOccupiedTarget();
			return;
		}
	}

	protected void handleOccupiedTarget() throws InterruptedException {
		// Wait for 2 turns then turn around
		if (waitCounter < 2) {
			waitCounter++;
			return;
		}
		waitCounter = 0;
		turnRight();
		turnRight();
		nextMove();
	}

	protected SimPosition chooseNextPosition(int facing) {
		SimPosition position;
		position = checkSide((facing + 1) % 4);
		if (position != null)
			if (getNodeFromPosition(position).isTraversable())
				return position;
		position = checkSide(facing);
		if (position != null)
			if (getNodeFromPosition(position).isTraversable())
				return position;
		position = checkSide((facing + 3) % 4);
		if (position != null)
			if (getNodeFromPosition(position).isTraversable())
				return position;
		position = checkSide((facing + 2) % 4);
		if (position != null)
			if (getNodeFromPosition(position).isTraversable())
				return position;
		return null;
	}

	protected SimPosition checkSide(int facing) {
		int x = currentPosition.getX();
		int y = currentPosition.getY();
		FacingDirectionEnum newFacing = translateIntToFacing(facing);
		switch (newFacing) {
		case TOP:
			y--;
			break;
		case RIGHT:
			x++;
			break;
		case BOTTOM:
			y++;
			break;
		case LEFT:
			x--;
		}
		SimPosition newPosition = new SimPosition(x, y);
		if (x >= 0 && y >= 0) {
			if (isKnownPosition(newPosition))
				return newPosition;
		}
		return null;
	}

	protected void turnRight() {
		switch (facing) {
		case TOP:
			facing = FacingDirectionEnum.RIGHT;
			break;
		case RIGHT:
			facing = FacingDirectionEnum.BOTTOM;
			break;
		case BOTTOM:
			facing = FacingDirectionEnum.LEFT;
			break;
		case LEFT:
			facing = FacingDirectionEnum.TOP;
		}
	}

	protected FacingDirectionEnum changeFacing(SimPosition targetPosition) {
		FacingDirectionEnum facing = null;
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
		System.out.println("Now facing: " + facing.toString());
		return facing;
	}

	protected void findNeighbors(AgentNode node) {
		int x = node.getPosition().getX();
		int y = node.getPosition().getY();
		// bottom
		if (isKnownPosition(new SimPosition(x, y + 1))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x, y + 1)));
		}
		// right
		if (isKnownPosition(new SimPosition(x + 1, y))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x + 1, y)));
		}
		// top
		if (isKnownPosition(new SimPosition(x, y - 1))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x, y - 1)));
		}
		// left
		if (isKnownPosition(new SimPosition(x - 1, y))) {
			node.addNeighbor(getNodeFromPosition(new SimPosition(x - 1, y)));
		}
	}

	protected void setIntersectionPathEnter(AgentIntersection intersection, FacingDirectionEnum facing) {
		switch (facing) {
		case TOP:
			intersection.setBottom(IntersectionWayEnum.EXPLORED);
			break;
		case RIGHT:
			intersection.setLeft(IntersectionWayEnum.EXPLORED);
			break;
		case BOTTOM:
			intersection.setTop(IntersectionWayEnum.EXPLORED);
			break;
		case LEFT:
			intersection.setRight(IntersectionWayEnum.EXPLORED);
		}
	}

	protected void setIntersectionPathExit(AgentIntersection intersection, FacingDirectionEnum facing) {
		switch (facing) {
		case TOP:
			intersection.setTop(IntersectionWayEnum.EXPLORED);
			break;
		case RIGHT:
			intersection.setRight(IntersectionWayEnum.EXPLORED);
			break;
		case BOTTOM:
			intersection.setBottom(IntersectionWayEnum.EXPLORED);
			break;
		case LEFT:
			intersection.setTop(IntersectionWayEnum.EXPLORED);
		}
	}

	protected AgentIntersection generateIntersection(SimPosition position) {
		int x = position.getX();
		int y = position.getY();
		boolean top = false, left = false, right = false, bottom = false;
		// bottom
		if (isKnownPosition(new SimPosition(x, y + 1))) {
			bottom = true;
		}
		// right
		if (isKnownPosition(new SimPosition(x + 1, y))) {
			right = true;
		}
		// top
		if (isKnownPosition(new SimPosition(x, y - 1))) {
			top = true;
		}
		// left
		if (isKnownPosition(new SimPosition(x - 1, y))) {
			left = true;
		}
		AgentIntersection intersection = new AgentIntersection(position, top, right, bottom, left);
		setIntersectionPathEnter(intersection, facing);
		return intersection;
	}

	protected AgentIntersection getIntersectionFromPosition(SimPosition position) {
		for (AgentIntersection intersection : intersections) {
			if (position.equals(intersection.getPosition())) {
				return intersection;
			}
		}
		return null;
	}

	protected boolean isKnownIntersecion(SimPosition position) {
		for (AgentIntersection intersection : intersections) {
			if (position.equals(intersection.getPosition())) {
				return true;
			}
		}
		return false;
	}

	protected int translateFacingToInt(FacingDirectionEnum facing) {
		switch (facing) {
		case TOP:
			return 0;
		case RIGHT:
			return 1;
		case BOTTOM:
			return 2;
		case LEFT:
			return 3;
		}
		return -1;
	}

	protected FacingDirectionEnum translateIntToFacing(int facing) {
		switch (facing) {
		case 0:
			return FacingDirectionEnum.TOP;
		case 1:
			return FacingDirectionEnum.RIGHT;
		case 2:
			return FacingDirectionEnum.BOTTOM;
		case 3:
			return FacingDirectionEnum.LEFT;
		}
		return null;
	}

	public SimPosition getCurrentPosition() {
		return currentPosition;
	}

	@Override
	protected void actionsAferOriginChange() {
		SimPosition newPositon;
		for (AgentIntersection intersection : intersections) {
			newPositon = intersection.getPosition().minus(originTransformationVektor);
			intersection.setPosition(newPositon);
		}
	}
}
