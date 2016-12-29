package mapEditor;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseGesturesSquare {

	public void makePaintable(GridSquare square){
		square.setOnMousePressed( onMousePressedEventHandler);
		square.setOnDragDetected( onDragDetectedEventHandler);
		square.setOnMouseDragEntered( onMouseDragEnteredEventHandler);
	}
	
	EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
		GridSquare square = (GridSquare) event.getSource();
		if( event.isPrimaryButtonDown()) {
            square.makeIntraversable();
        } else if( event.isSecondaryButtonDown()) {
            square.makeTraverable();
        }
	};
	
	EventHandler<MouseEvent> onDragDetectedEventHandler = event -> {

		GridSquare square = (GridSquare) event.getSource();
        square.startFullDrag();

    };
    
    EventHandler<MouseEvent> onMouseDragEnteredEventHandler = event -> {
    	
    	GridSquare square = (GridSquare) event.getTarget();
    	if( event.isPrimaryButtonDown()) {
            square.makeIntraversable();
        } else if( event.isSecondaryButtonDown()) {
            square.makeTraverable();
        }       
    };
}
