package graphGen;

public class LowLevelGraphPath {

	LowLevelGraphNode node1, node2;
	double distance;
	boolean crossesIntersection;
	
	public LowLevelGraphPath(LowLevelGraphNode node1, LowLevelGraphNode node2, double distance, boolean crossesIntersection) {
		this.node1 = node1;
		this.node2 = node2;
		this.distance = distance;
	}
}
