package graphGen;

public class LowLevelGraphPath {

	private LowLevelGraphNode node1, node2;
	private double distance;
	private int crossedIntersections;
	private String pathString;
	
	public LowLevelGraphPath(LowLevelGraphNode node1, LowLevelGraphNode node2, double distance) {
		this.node1 = node1;
		this.node2 = node2;
		this.distance = distance;
	}

	public void setNode1(LowLevelGraphNode node1) {
		this.node1 = node1;
	}

	public void setNode2(LowLevelGraphNode node2) {
		this.node2 = node2;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getCrossedIntersections() {
		return crossedIntersections;
	}

	public void setCrossesIntersections(int crossedIntersections) {
		this.crossedIntersections = crossedIntersections;
	}

	public String getPathString() {
		return pathString;
	}

	public void setPathString(String pathString) {
		this.pathString = pathString;
	}

	public LowLevelGraphNode getNode1() {
		return node1;
	}

	public LowLevelGraphNode getNode2() {
		return node2;
	}

	public double getDistance() {
		return distance;
	}
	
	@Override
	public String toString(){
		return pathString;
	}
}
