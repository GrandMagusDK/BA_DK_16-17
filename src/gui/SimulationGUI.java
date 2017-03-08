package gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import agent.FacingDirectionEnum;
import agent.IMapDataUpdate;
import agent.IntelligentAgent;
import agent.RandomAgent;
import agent.SimpleAgent;
import agent.SimulationAgent;
import agent.SimPosition;
import graphGen.AbstractedGraph;
import graphGen.FullGraph;
import graphGen.LowLevelGraph;
import graphGen.LowLevelGraphNode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SimulationGUI extends Application implements IMapDataUpdate {
	protected GUITimer timer;
	protected SimulationGUI self = this;
	protected TextField sensorTextField;
	protected TextField comTexField;
	protected TextField startPosXTextField;
	protected TextField startPosYTextField;
	protected ComboBox<String> agentTypeComboBox;
	protected ComboBox<String> agentFacingComboBox;
	protected ListView<String> listCurrentGraph;
	protected ListView<String> listViewAgents;
	protected List<SimPosition> scannedPositions = new ArrayList<>();
	protected double squareSize;
	protected double simWidth;
	protected double simHeight;
	protected double edgeWidth;
	protected int resolution;
	protected int agentRecSize;
	protected LowLevelGraph lowGraph;
	protected AbstractedGraph abstractedGraph;
	protected FullGraph fullGraph;
	protected boolean calledWithFullGraph = false;
	protected boolean calledWithLowLevelGraph = false;
	protected List<SimulationAgent> agents = new ArrayList<>();
	protected GridPane simGrid;
	protected Rectangle[][] nodeSquares;
	protected int agentCounter = 0;
	protected int refreshCounter = 0;
	protected int numberOfTraversableSquares;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// This will never be used but is required by Application
	}

	public void start(Stage primaryStage, FullGraph fullGraph) {
		this.fullGraph = fullGraph;
		calledWithFullGraph = true;
		try {
			buildProcessingScreen(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(Stage primaryStage, LowLevelGraph graph) {
		this.lowGraph = graph;
		calledWithLowLevelGraph = true;

		try {
			buildProcessingScreen(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildProcessingScreen(Stage primaryStage) throws Exception {
		// Panes
		listCurrentGraph = new ListView<String>();
		listViewAgents = new ListView<String>();
		VBox vBoxRight = new VBox();
		VBox vBoxLeft = new VBox();
		HBox hBox = new HBox();
		GridPane grid = new GridPane();
		// TextFields
		sensorTextField = new TextField();
		comTexField = new TextField();
		startPosXTextField = new TextField();
		startPosYTextField = new TextField();
		startPosXTextField.setText("Start X");
		startPosYTextField.setText("Start Y");
		// ComboBoxes
		agentTypeComboBox = new ComboBox<>();
		agentFacingComboBox = new ComboBox<>();
		ObservableList<String> optionsType = FXCollections.observableArrayList("Simple Agent", "Intelligent Agnet");
		ObservableList<String> optionsFacing = FXCollections.observableArrayList("Top", "Right", "Bottom", "Left");
		agentTypeComboBox.setItems(optionsType);
		agentTypeComboBox.setPromptText("Agent Type");
		agentFacingComboBox.setItems(optionsFacing);
		agentFacingComboBox.setPromptText("Facing Direction");
		// Buttons
		Button buttonnextStep = new Button();
		Button addButton = new Button();
		Button addTestSimpleAgentButton = new Button();
		Button addTestIntelligentAgentButton = new Button();

		if (calledWithLowLevelGraph) {
			fillListCurrentLLG();
			buttonnextStep.setText("Process to Graph");
		} else if (calledWithFullGraph) {
			fillListCurrentFG();
			buttonnextStep.setText("Continue to Simulation");
		}

		buttonnextStep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (calledWithLowLevelGraph) {
					processToGraph();
				}
				saveGraph("tempGraph", fullGraph);
				buildSimScene(primaryStage);
			}
		});
		addButton.setText("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String type = agentTypeComboBox.getValue();
				String facingText = agentFacingComboBox.getValue();
				int x = Integer.parseInt(startPosXTextField.getText());
				int y = Integer.parseInt(startPosYTextField.getText());
				FacingDirectionEnum facing = getFacing(facingText);
				SimPosition position = new SimPosition(x, y);
				if (type == "Simple Agent") {
					SimpleAgent agent = new SimpleAgent(agentCounter, position, facing, self);
					agents.add(agent);
					listViewAgents.getItems()
							.add("Simple Agent: ID: " + agentCounter + " , Startposition: " + x + ", " + y);
					agentCounter++;
				} else if (type == "Random Agnet") {
					RandomAgent agent = new RandomAgent(agentCounter, position, facing, self);
					agents.add(agent);
					listViewAgents.getItems()
							.add("Random Agent: ID: " + agentCounter + " , Startposition: " + x + ", " + y);
					agentCounter++;
				} else if (type == "Intelligent Agnet") {
					IntelligentAgent agent = new IntelligentAgent(agentCounter, position, facing, self);
					agents.add(agent);
					listViewAgents.getItems()
							.add("Intelligent Agent: ID: " + agentCounter + " , Startposition: " + x + ", " + y);
					agentCounter++;
				}
			}
		});

		addTestIntelligentAgentButton.setText("Test Intelligent");
		addTestIntelligentAgentButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				IntelligentAgent agent = new IntelligentAgent(agentCounter, new SimPosition(0, 0),
						FacingDirectionEnum.RIGHT, self);
				agents.add(agent);
				listViewAgents.getItems()
						.add("Intelligent Agent: ID: " + agentCounter + " , Startposition: " + 0 + ", " + 0);
				agentCounter++;
			}
		});

		addTestSimpleAgentButton.setText("Test Simple");
		addTestSimpleAgentButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SimpleAgent agent = new SimpleAgent(agentCounter, new SimPosition(0, 0), FacingDirectionEnum.RIGHT,
						self);
				agents.add(agent);
				listViewAgents.getItems()
						.add("Simple Agent: ID: " + agentCounter + " , Startposition: " + 0 + ", " + 0);
				agentCounter++;
			}
		});

		// LeftSide
		vBoxLeft.getChildren().add(listCurrentGraph);
		vBoxLeft.getChildren().add(buttonnextStep);
		// Right Side
		grid.add(agentTypeComboBox, 0, 1);
		grid.add(agentFacingComboBox, 2, 1);
		grid.add(startPosXTextField, 0, 2);
		grid.add(startPosYTextField, 2, 2);
		grid.add(addButton, 0, 3);
		grid.add(addTestSimpleAgentButton, 2, 3);
		grid.add(addTestIntelligentAgentButton, 4, 3);
		vBoxRight.getChildren().add(grid);
		vBoxRight.getChildren().add(listViewAgents);

		hBox.getChildren().add(vBoxLeft);
		hBox.getChildren().add(vBoxRight);
		Scene scene = new Scene(hBox);
		primaryStage.setTitle("Simulation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void processToGraph() {
		abstractedGraph = new AbstractedGraph(lowGraph.getIntersections(), lowGraph.getLocatedGraph());
		abstractedGraph.startProcessing();
		fullGraph = new FullGraph(abstractedGraph.getNodes(), abstractedGraph.getEdges(), lowGraph);
	}

	private void fillListCurrentFG() {
		String endLine = "\n";
		ObservableList<String> items = FXCollections.observableArrayList();
		items.add("Size: " + fullGraph.getLowLevelGraph().getSizeX() + " x " + fullGraph.getLowLevelGraph().getSizeY()
				+ endLine);
		items.add("Intersections: " + endLine);
		items.add("Number of Intersections: " + fullGraph.getNodes().size() + endLine);

		for (int i = 0; i < fullGraph.getNodes().size(); i++) {
			items.add("" + i + ": " + fullGraph.getNodes().get(i).getX() + ", " + fullGraph.getNodes().get(i).getY()
					+ endLine);
		}
		listCurrentGraph.setItems(items);
	}

	private void fillListCurrentLLG() {
		String endLine = "\n";
		ObservableList<String> items = FXCollections.observableArrayList();
		items.add("Size: " + lowGraph.getSizeX() + " x " + lowGraph.getSizeY() + endLine);
		items.add("Intersections: " + endLine);
		items.add("Number of Intersections: " + lowGraph.getIntersections().size() + endLine);

		for (int i = 0; i < lowGraph.getIntersections().size(); i++) {
			items.add("" + i + ": " + lowGraph.getIntersections().get(i).getX() + ", "
					+ lowGraph.getIntersections().get(i).getY() + endLine);
		}
		listCurrentGraph.setItems(items);
	}

	//
	// Scene: Simulation
	//
	private void buildSimScene(Stage primaryStage) {

		int sizeX = fullGraph.getLowLevelGraph().getSizeX();
		int sizeY = fullGraph.getLowLevelGraph().getSizeY();
		nodeSquares = new Rectangle[sizeX][sizeY];

		resolution = 20;
		agentRecSize = (int) (0.5 + Math.sqrt(Math.pow(resolution, 2) / 2));
		HBox hBox = new HBox();
		Group simGroup = new Group();
		Group controlsGroup = new Group();
		GridPane controlsGrid = new GridPane();
		simGrid = new GridPane();
		Button saveGraphButton = new Button();
		Button startButton = new Button();
		TextField saveGraphTextField = new TextField();

		saveGraphButton.setText("Save Graph");
		saveGraphButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = saveGraphTextField.getText();
				if (name == "" || name == null) {
					System.out.println("Name error for saving Graph");
					return;
				}
				saveGraph(name, fullGraph);
			}
		});

		startButton.setText("Start"); // starts Agents
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				for (SimulationAgent agent : agents) {
					new Thread(agent).start();
				}
				timer.start();
			}
		});

		controlsGrid.add(saveGraphTextField, 0, 1);
		controlsGrid.add(saveGraphButton, 1, 1);
		controlsGrid.add(startButton, 0, 0);
		controlsGroup.getChildren().add(controlsGrid);

		buildGridView();
		buildAgentRectangles();
		simGroup.getChildren().add(simGrid);
		hBox.getChildren().add(simGroup);
		hBox.getChildren().add(controlsGroup);

		timer = new GUITimer();

		Scene scene = new Scene(hBox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	//
	// Methods
	//
	private class GUITimer extends AnimationTimer {

		@Override
		public void handle(long arg0) {
			for (SimulationAgent agent : agents) {
				updateAgentPositions(agent);
			}
			updateNodeColor();
			if (numberOfTraversableSquares == scannedPositions.size()) {
				System.out.println("Map completed");
			}
		}
	}

	private void buildGridView() { // builds the base grid for the Simulation
		numberOfTraversableSquares = 0;
		Rectangle newRec;
		for (int i = 0; i < fullGraph.getLowLevelGraph().getLocatedGraph().length; i++) {
			for (int j = 0; j < fullGraph.getLowLevelGraph().getLocatedGraph().length; j++) {
				newRec = new Rectangle(resolution, resolution);
				if (fullGraph.getLowLevelGraph().getLocatedGraph()[i][j].isTraversable()) {
					newRec.setFill(Color.BISQUE);
					numberOfTraversableSquares++;
				} else {
					newRec.setFill(Color.GRAY);
				}
				nodeSquares[i][j] = newRec;
				simGrid.add(newRec, i, j);
			}
		}
	}

	private void buildAgentRectangles() {
		Rectangle rec;
		for (SimulationAgent agent : agents) {
			rec = new Rectangle(agentRecSize, agentRecSize);
			rec.setRotate(45);
			rec.setArcHeight(agentRecSize / 10);
			rec.setArcWidth(agentRecSize / 10);
			rec.setFill(chooseAgentColor(agent.getId()));
			agent.setSimRectangle(rec);
			simGrid.add(agent.getSimRectangle(), agent.getStartPosition().getX(), agent.getStartPosition().getY());
		}
	}

	private Color chooseAgentColor(int id) {
		switch (id % 9) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.BLUEVIOLET;
		case 3:
			return Color.BROWN;
		case 4:
			return Color.DARKCYAN;
		case 5:
			return Color.DARKBLUE;
		case 6:
			return Color.RED;
		case 7:
			return Color.CHOCOLATE;
		case 8:
			return Color.GREEN;
		case 9:
			return Color.AQUA;
		default:
			return Color.BLACK;
		}
	}

	public void updateNodeColor() {
		synchronized (scannedPositions) {
			for (SimPosition position : scannedPositions) {
				if (fullGraph.getLowLevelGraph().getLocatedGraph()[position.getX()][position.getY()].isTraversable()) {
					nodeSquares[position.getX()][position.getY()].setFill(Color.WHITE);
				}
			}
		}
	}

	public void updateAgentPositions(SimulationAgent agent) {
		SimPosition newPosition = new SimPosition(agent.getCurrentWorldCoord().getX(),
				agent.getCurrentWorldCoord().getY());
		moveAgent(newPosition, agent.getSimRectangle());
	}

	private void moveAgent(SimPosition newPosition, Rectangle agentRectangle) {
		simGrid.getChildren().remove(agentRectangle);
		simGrid.add(agentRectangle, newPosition.getX(), newPosition.getY());
	}

	private void saveGraph(String name, FullGraph abstractedGraph) {
		// TODO Seemingly works final test needs visualization.
		System.out.println("Saving Graph");
		try {
			FileOutputStream fileOut = new FileOutputStream(name + ".abg");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(abstractedGraph);
			out.close();
			fileOut.close();
			System.out.println("Finished Saving Graph!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<SimPosition, LowLevelGraphNode> fetchMapData(SimulationAgent agent) {
		SimPosition translated;
		Map<SimPosition, LowLevelGraphNode> result = new HashMap<>();
		for (SimPosition position : agent.getScanPositions()) {
			translated = translatePositionForAgents(position, agent.getStartPosition());
			if (translated != null) {
				result.put(position,
						fullGraph.getLowLevelGraph().getLocatedGraph()[translated.getX()][translated.getY()]);
				if (!isScannedPosition(translated))
					synchronized (scannedPositions) {
						scannedPositions.add(translated);
					}
			} else {
				result.put(position, null);
			}
		}
		return result;
	}

	public boolean isScannedPosition(SimPosition position) {
		synchronized (scannedPositions) {
			List<SimPosition> scanned = new ArrayList<>(scannedPositions);
			for (SimPosition pos : scanned) {
				if (pos.equals(position))
					return true;
			}
		}
		return false;
	}

	public List<SimulationAgent> fetchCommunicationData(SimulationAgent callingAgent) {
		List<SimulationAgent> resultAgents = new ArrayList<>();
		double x, y, distance;
		for (SimulationAgent agent : agents) {
			if (agent != callingAgent) {
				x = agent.getCurrentWorldCoord().getX() - callingAgent.getCurrentWorldCoord().getX();
				y = agent.getCurrentWorldCoord().getY() - callingAgent.getCurrentWorldCoord().getY();
				distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				if (((int) distance + 0.5) <= callingAgent.getCommunicationRange()) {
					resultAgents.add(agent);
				}
			}
		}
		return resultAgents;
	}

	private SimPosition translatePositionForAgents(SimPosition position, SimPosition agentStartPosition) {
		SimPosition newPos = null;
		int newX = position.getX() + agentStartPosition.getX();
		int newY = position.getY() + agentStartPosition.getY();
		if (newX >= 0 && newY >= 0) {
			if (newX < lowGraph.getSizeX() && newY < lowGraph.getSizeY()) {
				newPos = new SimPosition(newX, newY);
			}
		}
		return newPos;
	}

	private FacingDirectionEnum getFacing(String direction) {
		FacingDirectionEnum facing;
		switch (direction) {
		case "Top":
			facing = FacingDirectionEnum.TOP;
			break;
		case "Right":
			facing = FacingDirectionEnum.RIGHT;
			break;
		case "Bottom":
			facing = FacingDirectionEnum.BOTTOM;
			break;
		case "Left":
			facing = FacingDirectionEnum.LEFT;
			break;
		default:
			facing = FacingDirectionEnum.TOP;
		}
		return facing;
	}
}
