package agent;

import java.util.List;

public interface IMapDataUpdate {

	public List<AgentNode> fetchMapData(int agentID, List<AgentNode> requestedNodes);
}
