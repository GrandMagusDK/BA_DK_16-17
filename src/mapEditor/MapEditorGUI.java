package mapEditor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import graphGen.LowLevelGraph;
import graphGen.LowLevelGraphNode;
import gui.SimulationGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MapEditorGUI extends Application {
	int sizeX = 0;
	int sizeY = 0;
	int squareSize = 30;
	GridNode[][] grid;

	String fileName;
	String loadFileName;

	@Override
	public void start(Stage primaryStage) throws Exception {
		TextField textFieldX = new TextField();
		TextField textFieldY = new TextField();
		TextField textFieldFileName = new TextField();
		Button buttonGenGrid = new Button();
		Button buttonLoad = new Button();
		Button buttonProcessToGraph = new Button();
		Label labelGenerate = new Label();
		Label labelLoad = new Label();
		labelGenerate.setText("Enter collumn and row count: (values 10-50)");
		labelLoad.setText("Or Enter filename to load existing Map");
		
		//button GenerateGrid
		buttonGenGrid.setText("Generate Grid");
		buttonGenGrid.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (Pattern.matches("[0-9]+", textFieldX.getText())) {
					sizeX = Integer.parseInt(textFieldX.getText());
					
					if (Pattern.matches("[0-9]+", textFieldY.getText())) {
						sizeY = Integer.parseInt(textFieldY.getText());
						
						if (sizeX >= 10 && sizeX <= 100 && sizeY >= 10 && sizeY <= 100) {
							if(sizeY >= 30 || sizeX >= 50)
								squareSize = 20;
							if(sizeY >= 60)
								squareSize = 10;
							grid = generateGridNodes(sizeX, sizeY, true);
							buildGridScene(primaryStage, grid);
						}
					}
				}
				else{
					showErrorAlert("Invalid Boundaries(1-50)");
					return;
				}
			}

		});
		
		//button Load
		buttonLoad.setText("Load");
		buttonLoad.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if(textFieldFileName.getText() != null || textFieldFileName.getText() != ""){
					loadFileName = textFieldFileName.getText();
					if(!(loadFileName.contains(".txt"))){
						loadFileName += ".txt";
					}
					GridNode[][] parsedGrid = parseLoadedData(loadFromFile(loadFileName));
					buildGridScene(primaryStage, parsedGrid);
				}
				else
				{
					showErrorAlert("Enter Filename");
					return;
				}
			}
		});
		
		buttonProcessToGraph.setText("Process to Graph");
		buttonProcessToGraph.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				LowLevelGraph lowGraph = new LowLevelGraph(grid);
			}
			//TODO
		});

		GridPane layout = new GridPane();

		layout.setAlignment(Pos.TOP_LEFT);
		layout.setHgap(10);
		layout.setVgap(10);
		layout.setPadding(new Insets(25, 25, 25, 25));

		layout.add(labelGenerate, 0, 0, 2, 1);
		layout.add(textFieldX, 0, 1);
		layout.add(textFieldY, 1, 1);
		layout.add(buttonGenGrid, 0, 2);
		layout.add(labelLoad, 0, 3, 2, 1);
		layout.add(textFieldFileName, 0, 4, 2, 1);
		layout.add(buttonLoad, 2, 4, 2 , 1);

		Scene scene = new Scene(layout, 420, 210);

		primaryStage.setTitle("Map Editor");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	void buildGridScene(Stage primaryStage, GridNode[][] gridNodes) {
		int sizeX = gridNodes.length;
		int sizeY = gridNodes[0].length;
		grid = gridNodes;
		
		VBox rootPane = new VBox();
		GridPane gridLayout = new GridPane();
		GridPane buttonPane = new GridPane();
		
		Button buttonSave = new Button();
		Button buttonClear = new Button();
		Button buttonBack = new Button();
		Button buttonConvert = new Button();
	
		gridLayout.setAlignment(Pos.TOP_LEFT);
		gridLayout.setPadding(new Insets(26, 26, 26, 26));
		gridLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(25, 25, 25, 25))));
		gridLayout.setHgap(1);
		gridLayout.setVgap(1);
		GridSquare square;
		Group group = new Group();
		
		MouseGesturesSquare mg = new MouseGesturesSquare();

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				square = new GridSquare(gridNodes[i][j], squareSize);
				mg.makePaintable(square);
				gridLayout.add(square, i, j);
				square.checkColor();
			}
		}
		group.getChildren().add(gridLayout);

		TextField fileNameField = new TextField();
		if(fileName != null){
			fileNameField.setText(fileName);
		}
		buttonSave.setText("Save");
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String s = fileNameField.getText();
				if (s != "" || s != "File Name") {
					fileName = fileNameField.getText();
					saveToFile(fileName+".txt", saveCustomGrid(true));
				} else {
					showErrorAlert("Enter Filename");
					return;
				}
			}

		});
		
		buttonClear.setText("Clear");
		buttonClear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				fileNameField.clear();
				int maxIndex = gridLayout.getChildren().size();
				GridSquare buffer;
				for(int i = 0; i < maxIndex; i++){
					if(gridLayout.getChildren().get(i).getClass().equals(GridSquare.class)){
						buffer = (GridSquare)gridLayout.getChildren().get(i);
						buffer.getNode().setTraversable(true);
						buffer.checkColor();
					}
				}
				
			}
		});
		
		buttonBack.setText("Back");
		buttonBack.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					start(primaryStage);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		});
		
		buttonConvert.setText("Convert to Graph");
		buttonConvert.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				LowLevelGraph graph = new LowLevelGraph(grid);
				try {
					FileOutputStream fileOut = new FileOutputStream("tempgraph.tmp");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(graph);
					out.close();
					fileOut.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//TODO Seemingly works final test needs deserialization. Also move to Save Function in SimGUI, not needed here.
				Platform.runLater(new Runnable() { //Opening MapEditorGUI class
					public void run() {
						try {
							new SimulationGUI().start(new Stage(), graph);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		buttonPane.setAlignment(Pos.TOP_CENTER);
		buttonPane.add(buttonBack, 0, 0, 2, 1);
		buttonPane.add(buttonClear, 2, 0, 2, 1);
		buttonPane.add(fileNameField, 4, 0, 10, 1);
		buttonPane.add(buttonSave, 15, 0, 2, 1);
		buttonPane.add(buttonConvert, 8-2, 2, 4, 1);
		
		rootPane.getChildren().add(group); //contains gridlayout
		rootPane.getChildren().add(buttonPane);
		
		double sceneWidth = 50 + (squareSize+1) * sizeX;
		double sceneHeight = 50 + (squareSize+1) * sizeY + 65;
		
		Scene scene = new Scene(rootPane, sceneWidth, sceneHeight);
		primaryStage.setScene(scene);
	}

	public GridNode[][] generateGridNodes(int x, int y, boolean createEmpty) {
		GridNode[][] grid = new GridNode[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grid[i][j] = new GridNode(i, j, createEmpty);
			}
		}
		return grid;
	}

	byte[] saveCustomGrid(boolean traversable) {
		Character lineEnd = '\n';
		StringBuilder builder = new StringBuilder();
		builder.append(fileName + lineEnd);
		builder.append("Size:" + lineEnd);
		builder.append(String.valueOf(sizeX) + lineEnd);
		builder.append(String.valueOf(sizeY) + lineEnd);
		builder.append("Body:" + lineEnd);
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (grid[i][j].isTraversable() == traversable) {
					builder.append(grid[i][j].getX() + " " + grid[i][j].getY() + lineEnd);
				}
			}
		}
		builder.append("END");
		String saveData = builder.toString();
		return saveData.getBytes(Charset.forName("UTF-8"));
	}

	byte[] saveCustomGridNoneTraversable() {
		Character lineEnd = '\n';
		StringBuilder builder = new StringBuilder();
		builder.append(fileName + lineEnd);
		builder.append("Size:" + lineEnd);
		builder.append(String.valueOf(sizeX) + lineEnd);
		builder.append(String.valueOf(sizeY) + lineEnd);
		builder.append("Body:" + lineEnd);
		System.out.println(builder.toString());
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (!(grid[i][j].isTraversable())) {
					builder.append(grid[i][j].getX() + " " + grid[i][j].getY() + lineEnd);
				}
			}
		}
		builder.append("END");
		String saveData = builder.toString();
		return saveData.getBytes(Charset.forName("UTF-8"));
	}
	
	
	
	public GridNode[][] parseLoadedData(List<String> data){
		GridNode[][] grid;
		int sizeX,sizeY = 0;
		//Header
		if(data.get(0) != null || data.get(0) != ""){
			fileName = data.get(0);
		}
		else
		{
			showErrorAlert("Parsing Error: Filename");
			return null;
		}
		
		if(data.get(1).contains("Size")){
			if(Pattern.matches("[0-9]+", data.get(2))){
				sizeX = Integer.parseInt(data.get(2));
				
				if(Pattern.matches("[0-9]+", data.get(3))){
					sizeY = Integer.parseInt(data.get(3));
				}
			}
			else
			{
				showErrorAlert("Parsing Error: Wrong Size Header Format");
				return null;
			}
			
			//Body
			if(data.get(4).contains("Body")){
				int counter = 0;
				String[] buffer;
				grid = generateGridNodes(sizeX,sizeY, false);
				if(grid[0][0].isTraversable())
					System.out.println("Grid Empty");
				while(!(data.get(counter+5).contains("END"))){
					buffer = data.get(counter+5).split(" ");
					if(Pattern.matches("[0-9]+", buffer[0]) && Pattern.matches("[0-9]+", buffer[1])){
						//set all parsed coordinates traversable.
						grid[Integer.parseInt(buffer[0])][Integer.parseInt(buffer[1])].setTraversable(true);
					}
					counter++;
				}
				return grid;
			}
			else
			{
				showErrorAlert("Parsing Error: No begin of Body.");
				return null;
			}
		}
		else
		{
			showErrorAlert("Parsing Error: Incorrect Size Header.");
			return null;
		}
		
	}
	
	private void showErrorAlert(String error){
		Alert alert = new Alert(AlertType.ERROR, error, ButtonType.OK);
		alert.showAndWait();
		return;
	}

	boolean saveToFile(String filename, byte[] saveData) {
		try {
			Files.write(Paths.get(filename), saveData);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	List<String> loadFromFile(String filename){
		List<String> data;
		try {
			data = Files.readAllLines(Paths.get(filename));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("File Read Error");
			return null;
		}
		return data;
	}
}
