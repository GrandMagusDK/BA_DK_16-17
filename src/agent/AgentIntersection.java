package agent;

public class AgentIntersection {
	private SimPosition position;
	private IntersectionWayEnum top, bottom, left, right;

	public AgentIntersection(SimPosition position, boolean top, boolean right, boolean bottom, boolean left) {
		this.position = position;
		if (top)
			this.top = IntersectionWayEnum.UNEXPLORED;
		if (right)
			this.right = IntersectionWayEnum.UNEXPLORED;
		if (bottom)
			this.bottom = IntersectionWayEnum.UNEXPLORED;
		if (left)
			this.left = IntersectionWayEnum.UNEXPLORED;
	}

	public SimPosition findUnexploredDirection(FacingDirectionEnum facing){
		int x = position.getX();
		int y = position.getY();
		switch(facing){
		case TOP: 
			if(right == IntersectionWayEnum.UNEXPLORED){
				x++;
			}else if(top == IntersectionWayEnum.UNEXPLORED){
				y--;
			}else if(left == IntersectionWayEnum.UNEXPLORED){
				x--;
			}else{
				return null;
			}
			break;
		case RIGHT:
			if(bottom == IntersectionWayEnum.UNEXPLORED){
				y++;
			}else if(right == IntersectionWayEnum.UNEXPLORED){
				x++;
			}else if(top == IntersectionWayEnum.UNEXPLORED){
				y--;
			}else{
				return null;
			}
			break;
		case BOTTOM:
			if(left == IntersectionWayEnum.UNEXPLORED){
				x--;
			}else if(bottom == IntersectionWayEnum.UNEXPLORED){
				y++;
			}else if(right == IntersectionWayEnum.UNEXPLORED){
				x++;
			}else{
				return null;
			}
			break;
		case LEFT:
			if(top == IntersectionWayEnum.UNEXPLORED){
				y--;
			}else if(left == IntersectionWayEnum.UNEXPLORED){
				x--;
			}else if(bottom == IntersectionWayEnum.UNEXPLORED){
				y++;
			}else{
				return null;
			}
		}
		return new SimPosition(x, y);
	}

	public SimPosition getPosition() {
		return position;
	}
	
	public void setPosition(SimPosition position) {
		this.position = position;
	}

	public IntersectionWayEnum getTop() {
		return top;
	}

	public IntersectionWayEnum getBottom() {
		return bottom;
	}

	public IntersectionWayEnum getLeft() {
		return left;
	}

	public IntersectionWayEnum getRight() {
		return right;
	}

	public void setBottom(IntersectionWayEnum botom) {
		this.bottom = botom;
	}

	public void setTop(IntersectionWayEnum top) {
		this.top = top;
	}

	public void setLeft(IntersectionWayEnum left) {
		this.left = left;
	}

	public void setRight(IntersectionWayEnum right) {
		this.right = right;
	}
}
