package agent;

import java.util.List;

import graphGen.AbstractedGraphNode;

public class SimulationCompletePath {
	
	private List<AbstractedGraphNode> abstractNodeOrder;
	private List<SimulationPathLow> lowPaths;
	
	public SimulationCompletePath(List<AbstractedGraphNode> abstractNodeOrder, List<SimulationPathLow> lowPaths){
		this.abstractNodeOrder = abstractNodeOrder;
		this.lowPaths = lowPaths;
	}
	
	public List<AbstractedGraphNode> getAbstractNodeOrder() {
		return abstractNodeOrder;
	}
	
	public List<SimulationPathLow> getLowPaths() {
		return lowPaths;
	}
	
	@Override
	public String toString(){
		String result = "Complete Path: \n";
		result += "Top Path: ";
		for(int i = 0; i < abstractNodeOrder.size(); i++){
			result += "[" + abstractNodeOrder.get(i).getX() + ", " + abstractNodeOrder.get(i).getY() + "]";
			if(i < abstractNodeOrder.size() - 1){
				result += " -> ";
			}
		}
		result += '\n';
		for(int i = 0; i < lowPaths.size(); i++){
			result += lowPaths.get(i).toString();
		}
		return result;
	}
}
