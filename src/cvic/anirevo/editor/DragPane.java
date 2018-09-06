package cvic.anirevo.editor;

import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.Objects;

public class DragPane extends TitledPane {

    private static final String DRAG_TYPE = "PANE";

    private Accordion parent;

    private Label labelTitle;

    public DragPane(Accordion parent) {
        setCursor(Cursor.DEFAULT);
        this.parent = parent;
        initLabel();
        //Using label title so inner drags can be processed separately
        labelTitle.setOnDragDetected(event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(DRAG_TYPE + String.valueOf(getIndex()));
            dragboard.setContent(content);
            collapseAll();
            event.consume();
        });

        setOnDragOver(event -> {
            if (dragValid(event)) {
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

            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                String string = dragboard.getString();
                string = string.replace(DRAG_TYPE, ""); //remove type
                int draggedIdx = Integer.parseInt(string);
                int thisIdx = getIndex();
                ObservableList<TitledPane> items = parent.getPanes();
                TitledPane temp = items.get(draggedIdx);
                items.remove(draggedIdx);
                items.add(thisIdx, temp);

                success = true;
            }
            event.setDropCompleted(success);
            onDragSuccessful();
            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    protected void onDragSuccessful() {

    }

    private void initLabel() {
        labelTitle = new Label("untitled");
        labelTitle.setPrefWidth(getPrefWidth());
        setGraphic(labelTitle);
    }

    public void setTitle(String title) {
        labelTitle.setText(title);
    }

    public String getTitle() {
        return labelTitle.getText();
    }

    private void collapseAll() {
        TitledPane expanded = parent.getExpandedPane();
        if (expanded != null) {
            expanded.setExpanded(false);
        }
    }

    private boolean dragValid(DragEvent event) {
        return event.getGestureSource() != this && event.getDragboard().hasString() && event.getDragboard().getString().contains(DRAG_TYPE);
    }

    public int getIndex() {
        return parent.getPanes().indexOf(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DragPane dragPane = (DragPane) o;
        return Objects.equals(labelTitle, dragPane.labelTitle);
    }

    @Override
    public int hashCode() {

        return Objects.hash(labelTitle);
    }
}
