package testStuff;

import agent.SimPosition;
import agent.SimpleAgent;
import agent.SimulationAgent;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UIUpdateThread extends Thread{
	Rectangle[][] recs;
	SimPosition agentPos;
	Rectangle agentRec;
	SimulationAgent agent;
	
	public UIUpdateThread(Rectangle[][] recs, Rectangle agentRec, SimulationAgent agent) {
		this.recs = recs;
		this.agentPos = agentPos;
		this.agent = agent;
		this.agentRec = agentRec;
		System.out.println("UIUpdateThread initialized.");
	}
	@Override
	public void run() {
		System.out.println("UIUpdateThread running.");
		while(true){
			System.out.println("UIUpdate Thread still running");
			agentPos = agent.getCurrentWorldCoord();
			Platform.runLater(new Runnable() {
                @Override
                public void run() {
                	System.out.println("Hey");
        			agentRec.setTranslateX(agentPos.getX() - agentRec.getX());
        			agentRec.setTranslateY(agentPos.getY() - agentRec.getY());
                	//updateRecColor(agent);
        			//System.out.println("UI Updated! Agent " + agent.getId() +": " + agentPos.getX() + ", " + agentPos.getY());
                }
            });
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			e.printStackTrace();
			}
		}
	}
	
	private void updateRecColor(SimulationAgent agent){
		SimpleAgent simAgent;
		SimPosition position;
		for(int i = 0; i<recs.length; i++){
			for(int j = 0; j<recs.length; j++){
				simAgent = (SimpleAgent) agent;
				position = new SimPosition(agent.getCoordsOfFirstOrigin().getX() - i, agent.getCoordsOfFirstOrigin().getY() - j);
				if(simAgent.isKnownPosition(position)){
					if(!agent.getNodeFromPosition(position).isUnexploredPathMarker()){
						recs[i][j].setFill(Color.WHITE);
					}
					else{
						recs[i][j].setFill(Color.BLUE);
					}
				}
			}
		}
	}

	
}
