package graphGen;

import java.util.ArrayList;
import java.util.List;

public class AbstractedGraphEdge {

	private List<AbstractedGraphNode> nodes = new ArrayList<>();
	private double distance;
	private boolean crossesIntersection = false;

	public AbstractedGraphEdge(AbstractedGraphNode node1, AbstractedGraphNode node2, double distance) {
		nodes.add(node1);
		nodes.add(node2);
		this.distance = distance;
	}

	public List<AbstractedGraphNode> getNodes() {
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
