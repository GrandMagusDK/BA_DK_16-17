package agent;

import java.util.List;

import graphGen.LowLevelGraphNode;

public interface IMapDataUpdate {

	public List<LowLevelGraphNode> fetchMapData(SimulationAgent agent);
	public List<SimulationAgent> fetchCommunicationData(SimulationAgent agent);
}
