package agent;

public class SimPosition {

	private int x, y;

	public SimPosition(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(SimPosition position){
		if(this.x == position.getX() && this.y == position.getY())
			return true;
		return false;
	}
	
}
