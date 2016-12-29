package mapEditor;

public class GridNode {

	private int x, y;
	private boolean traversable = true;
	
	
	public GridNode(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public GridNode(int x, int y, boolean traversable){
		this.x = x;
		this.y = y;
		traversable = this.traversable;
	}
	
	public String toString(){
		String result = "";
		result = "Node: " + x +";"+ y + ";";
		if(traversable){
			result += "1";
		}else{
			result += "0";	
		}
		return result;
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
	
	public boolean isTraversable() {
		return traversable;
	}
	
	public void setTraversable(boolean traversable){
		this.traversable = traversable;
	}

	public void toggleTraversable() {
		traversable = !traversable;
	}
}
