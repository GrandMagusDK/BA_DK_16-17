package testStuff;

public class SimTest implements MapDataUpdater{

	static int timer = 0;
	
	public static void doStuff() throws InterruptedException{
		while(timer < 1000){
			timer++;
			Thread.sleep(1000);
			System.out.println("Timer: " + timer);
		}
	}
	
	public int getStatus(){
		return timer;
	}
	
	public static void main(String[] args) {
		AgentTest a1 = new AgentTest(1, 1);
		AgentTest a2 = new AgentTest(2, 2);
		AgentTest a3 = new AgentTest(3, 3);
		new Thread(a1).start();
		new Thread(a2).start();
		new Thread(a3).start();
		try {
			doStuff();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
