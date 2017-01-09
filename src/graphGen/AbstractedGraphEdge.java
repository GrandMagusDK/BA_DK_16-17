package graphGen;

public class AbstractedGraphEdge {

	private AbstractedGraphNode[] nodes = new AbstractedGraphNode[2];
	private double distance;
	private boolean crossesIntersection = false;
	private LowLevelGraphPath lowLevelPath;

	public AbstractedGraphEdge(AbstractedGraphNode node1, AbstractedGraphNode node2, double distance) {
		nodes[0] = node1;
		nodes[1] = node2;
		this.distance = distance;
	}

	public AbstractedGraphNode[] getNodes() {
		return nodes;
	}

	public double getDistance() {
		return distance;
	}
	
	public boolean isCrossesIntersection() {
		return crossesIntersection;
	}

	public void setCrossesIntersection(boolean crossesIntersection) {
		this.crossesIntersection = crossesIntersection;
	}
}
