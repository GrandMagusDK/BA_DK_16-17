package gui;

import com.sun.org.apache.bcel.internal.generic.LALOAD;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mapEditor.MapEditorGUI;

public class MainGUI extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		Label loadGraphLable = new Label();
		TextField loadGraphTextField = new TextField();
		Button loadGraphButton = new Button();
		Button mapEditorButton = new Button();
		loadGraphLable.setText("Load Graph:");
		
		loadGraphButton.setText("Load");
		loadGraphButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				//TODO
			}
			
		});
		
		mapEditorButton.setText("Open Map-Editor");
		mapEditorButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Platform.runLater(new Runnable() { //Opening MapEditorGUI class
					public void run() {
						try {
							new MapEditorGUI().start(new Stage());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			
		});
		
		GridPane layout = new GridPane();
		
		layout.setAlignment(Pos.TOP_LEFT);
		layout.setHgap(10);
		layout.setVgap(10);
		layout.setPadding(new Insets(25, 25, 25, 25));
		
		layout.add(loadGraphLable, 0, 0, 3, 1);
		layout.add(loadGraphTextField, 0, 1, 2, 1);
		layout.add(loadGraphButton, 3, 1, 2, 1);
		layout.add(mapEditorButton, 0, 3, 4, 1);
		
		Scene scene = new Scene(layout, 420, 210);

		primaryStage.setTitle("Map Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}


}
