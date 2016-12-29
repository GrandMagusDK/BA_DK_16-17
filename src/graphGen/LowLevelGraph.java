package graphGen;

import java.util.ArrayList;
import java.util.List;

public class LowLevelGraph {
	int sizeX, sizeY;
	
	private LowLevelGraphNode[][] lowLevelGraph;
	private LowLevelGraphNode[][] locatedGraph;

	public LowLevelGraph(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.lowLevelGraph = generateLowLevelGraphNodes(sizeX, sizeY);
	}
	
	public LowLevelGraph(LowLevelGraphNode[][] existingGrid){
		this.sizeX = existingGrid.length;
		this.sizeY = existingGrid[0].length;
		this.lowLevelGraph = existingGrid;
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
					if(lowLevelGraph[i][j+1].isTraversable()){
						counter ++;
					}
				}
				//left
				if(j-1 <= 0){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i][j-1]);
					if(lowLevelGraph[i][j+1].isTraversable()){
						counter ++;
					}
				}
				//top
				if(i-1 <= 0){
					lowLevelGraph[i][j].addNeighbor(lowLevelGraph[i-1][j]);
					if(lowLevelGraph[i][j+1].isTraversable()){
						counter ++;
					}
				}
				
				lowLevelGraph[i][j].setNumberOfTraversableNeighbors(counter);
			}
		}
	}
	
	private List<LowLevelGraphNode> LocateIntersectionsSimple(){
		locatedGraph = lowLevelGraph;
		List<LowLevelGraphNode> intersections  = new ArrayList<LowLevelGraphNode>();
		findNeighbors();
		
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeY; j++){
				if(locatedGraph[i][j].getNumberOfTraversableNeighbors() > 2){
					locatedGraph[i][j].setIntersection(true);
					intersections.add(locatedGraph[i][j]);
				}
			}
		}
		return intersections;
	}
	
	public LowLevelGraphNode[][] getLowLevelGraph() {
		return lowLevelGraph;
	}
	
	public LowLevelGraphNode[][] getLocatedGraph() {
		return locatedGraph;
	}

}
