package gui;

import graphGen.LowLevelGraph;
import javafx.application.Application;
import javafx.stage.Stage;

public class SimulationGUI extends Application{

	public void start(Stage primaryStage, LowLevelGraph graph) throws Exception {
		// TODO Auto-generated method stub
		if(graph != null){
			System.out.println("WORKS omg");
			System.out.println(graph.toStringSimple());
		}else{
			System.out.println("Worked but no graph");
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
