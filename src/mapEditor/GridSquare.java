package mapEditor;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridSquare extends Rectangle {
	private GridNode node;

	public GridSquare(GridNode node, int size) {
		this.node = node;
		this.setHeight(size);
		this.setWidth(size);
		this.setFill(Color.WHITE);
	}
	
	void makeTraverable(){
		node.setTraversable(true);
		checkColor();
	}
	
	void makeIntraversable(){
		node.setTraversable(false);
		checkColor();
	}
	
	void checkColor(){
		if (node.isTraversable()) {
			this.setFill(Color.WHITE);
		} else {
			this.setFill(Color.BLACK);
		}
	}
	

	public GridNode getNode() {
		return node;
	}

	public void setNode(GridNode node) {
		this.node = node;
	}
	
	public String toString(){
		String s = node.getX() + "/" + node.getY();
		if(node.isTraversable()){
			s = s + "/" + "true";
		}
		else{
			s = s + "/" + "false";
		}
		return s;
		
	}
	
	public String toString2(){
		return node.toString();
	}
}
