package agent;

public class AgentNode {
	private boolean traversable;
	private boolean unexploredPathMarker;
	private boolean intersection;
	private SimPosition worldPosition; //this is just for conveniance
	private SimPosition position;
	
	public AgentNode(){}
	public AgentNode(SimPosition worldPosition, boolean traversable){
		this.worldPosition = worldPosition;
		this.traversable = traversable;
	}
	
	public boolean isUnexploredPathMarker() {
		return unexploredPathMarker;
	}
	public void setUnexploredPathMarker(boolean unexploredPathMarker) {
		this.unexploredPathMarker = unexploredPathMarker;
	}
	public boolean isIntersection() {
		return intersection;
	}
	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}
	public boolean isTraversable() {
		return traversable;
	}
	public void setTraversable(boolean traversable){
		this.traversable = traversable;
	}
	public SimPosition getWorldPosition() {
		return worldPosition;
	}
	public void setWorldPosition(SimPosition worldPosition) {
		this.worldPosition = worldPosition;
	}
	public SimPosition getCurrentPosition() {
		return position;
	}
	public void setCurrentPosition(SimPosition currentPosition) {
		this.position = currentPosition;
	}
	
}
