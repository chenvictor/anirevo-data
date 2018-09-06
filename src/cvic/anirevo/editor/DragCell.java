package cvic.anirevo.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragCell<T> extends ListCell<T> {

    private static final String DRAG_TYPE = "CELL";

    protected DragCell() {
        setOnDragDetected(event -> {
            if (isEmpty()) {
                return;
            }
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(DRAG_TYPE + String.valueOf(getIndex()));
            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (!isEmpty() && dragValid(event)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragEntered(event -> {
            if (dragValid(event)) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (dragValid(event)) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            if (isEmpty()) {
                return;
            }

            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                String string = dragboard.getString();
                string = string.replace(DRAG_TYPE, ""); //remove type
                int draggedIdx = Integer.parseInt(string);
                int thisIdx = getIndex();
                ObservableList<T> items = getListView().getItems();
                T temp = items.get(draggedIdx);
                items.remove(draggedIdx);
                items.add(thisIdx, temp);

                getListView().refresh();
                getListView().getSelectionModel().select(thisIdx);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    private boolean dragValid(DragEvent event) {
        return event.getGestureSource() != this && event.getDragboard().hasString() && event.getDragboard().getString().contains(DRAG_TYPE);
    }
}
