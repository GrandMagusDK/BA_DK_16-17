package agent;

import java.util.List;
import java.util.Map;

import graphGen.LowLevelGraphNode;

public interface IMapDataUpdate {

	public Map<SimPosition, LowLevelGraphNode> fetchMapData(SimulationAgent agent);
	public List<SimulationAgent> fetchCommunicationData(SimulationAgent agent);
}
