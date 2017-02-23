package testStuff;

import agent.SimPosition;

public class AgentTest implements Runnable{
	
	int id;
	int step;
	int current = 0;
	boolean running = false;
	MapDataUpdater updater;
	
	public AgentTest(int id, int step){
		this.id = id;
		this.step = step;
	}
	
	private void doShit() throws InterruptedException{
		Thread.sleep(step*1000);
		updater = new SimTest();
		current = register(updater);
		System.out.println("Agent " + id + ": " + current);
	}
	
	public int register(MapDataUpdater updater) {
        return updater.getStatus();
    }
	
	public void pause(){
		running = false;
	}
	
	public void resume(){
		running = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		running = true;
		try {
			while(running)
			doShit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
