package graphGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mapEditor.GridNode;

public class LowLevelGraph implements Serializable{
	
	private static final long serialVersionUID = 6307365074491949253L;

	int sizeX, sizeY;
	
	private LowLevelGraphNode[][] lowLevelGraph;
	private LowLevelGraphNode[][] locatedGraph;
	
	List<LowLevelGraphNode> intersections;

	public LowLevelGraph(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.lowLevelGraph = generateLowLevelGraphNodes(sizeX, sizeY);
		makeLocatedGraph();
	}
	
	public LowLevelGraph(LowLevelGraphNode[][] existingGraph){
		this.sizeX = existingGraph.length;
		this.sizeY = existingGraph[0].length;
		this.lowLevelGraph = existingGraph;
		makeLocatedGraph();
	}
	
	public LowLevelGraph(GridNode[][] existingGrid){
		this.sizeX = existingGrid.length;
		this.sizeY = existingGrid[0].length;
		this.lowLevelGraph = generateLowLevelGraphNodesFromExistingGrid(sizeX, sizeY, existingGrid);
		makeLocatedGraph();
	}
	
	private LowLevelGraphNode[][] generateLowLevelGraphNodes(int x, int y) {
		LowLevelGraphNode[][] grid = new LowLevelGraphNode[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grid[i][j] = new LowLevelGraphNode(i, j);
			}
		}
		return grid;
	}
	
	private LowLevelGraphNode[][] generateLowLevelGraphNodesFromExistingGrid(int x, int y, GridNode[][] existingGrid) {
		LowLevelGraphNode[][] grid = new LowLevelGraphNode[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grid[i][j] = new LowLevelGraphNode(existingGrid[i][j]);
			}
		}
		return grid;
	}
	
	private void makeLocatedGraph(){
		locatedGraph = lowLevelGraph;
		LocateIntersectionsSimple();
	}
	
	private void findNeighbors(){
		int counter = 0;
		for(int i = 0; i < sizeX; i++){			//column
			for(int j = 0; j < sizeY; j++){		//row
				//right
				if(j+1 < sizeY ){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i][j+1]);
					if(lowLevelGraph[i][j+1].isTraversable()){
						counter ++;
					}
				}
				//bottom
				if(i+1 < sizeX){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i+1][j]);
					if(lowLevelGraph[i+1][j].isTraversable()){
						counter ++;
					}
				}
				//left
				if(j-1 >= 0){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i][j-1]);
					if(lowLevelGraph[i][j-1].isTraversable()){
						counter ++;
					}
				}
				//top
				if(i-1 >= 0){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i-1][j]);
					if(lowLevelGraph[i-1][j].isTraversable()){
						counter ++;
					}
				}
				
				lowLevelGraph[i][j].setNumberOfTraversableNeighbors(counter);
			}
		}
	}
	
	private void LocateIntersectionsSimple(){
		intersections  = new ArrayList<LowLevelGraphNode>();
		findNeighbors();
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeY; j++){
				if(locatedGraph[i][j].getNumberOfTraversableNeighbors() > 2){
					locatedGraph[i][j].setIntersection(true);
					intersections.add(locatedGraph[i][j]);
					//TODO Nearly counts all nodes as intersections?
				}
			}
		}
	}
	
	public LowLevelGraphNode[][] getLowLevelGraph() {
		return lowLevelGraph;
	}
	
	public LowLevelGraphNode[][] getLocatedGraph() {
		return locatedGraph;
	}
	
	public List<LowLevelGraphNode> getIntersections() {
		return intersections;
	}
	
	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public String toStringSimple(){
		Character lineEnd = '\n';
		StringBuilder builder = new StringBuilder();
		builder.append("Size: " + sizeX + ", " + sizeY + lineEnd);
		builder.append("Number of Intersections: " + intersections.size());
		return builder.toString();
	}
}
