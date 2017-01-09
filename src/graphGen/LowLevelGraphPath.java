package graphGen;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

public class LowLevelGraphPath {

	private LowLevelGraphNode node1, node2;
	private double distance;
	private boolean crossesIntersection;
	private String pathString;
	
	public LowLevelGraphPath(LowLevelGraphNode node1, LowLevelGraphNode node2, double distance, boolean crossesIntersection) {
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

	public boolean isCrossingIntersection() {
		return crossesIntersection;
	}

	public void setCrossesIntersection(boolean crossesIntersection) {
		this.crossesIntersection = crossesIntersection;
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
