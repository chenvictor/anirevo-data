package cvic.anirevo.editor.tabs;

import cvic.anirevo.model.MapVenue;
import cvic.anirevo.editor.DragCell;
import cvic.anirevo.editor.TabInteractionHandler.NavigationController;
import cvic.anirevo.model.VenueManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

public class TabMapNavController extends NavigationController implements MapVenue.NameChangeListener {

    @FXML
    private ListView<MapVenue> listViewMapNav;

    public void initialize() {
        listViewMapNav.setContextMenu(getEmptyCtxMenu());
        listViewMapNav.setCellFactory(lv -> {
            DragCell<MapVenue> cell = new DragCell<MapVenue>(){
                @Override
                protected void updateItem(MapVenue item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        item.setListener(TabMapNavController.this);
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    updateContent(cell.getItem());
                }
            });
            cell.setContextMenu(getCellCtxMenu(cell));
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (cell.isEmpty()) {
                    event.consume();
                }
            });
            cell.prefWidthProperty().bind(listViewMapNav.prefWidthProperty());
            cell.setMaxWidth(Control.USE_PREF_SIZE);
            return cell;
        });
        listViewMapNav.setItems(VenueManager.getInstance().getVenues());
    }

    private ContextMenu getEmptyCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Venue");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addLevel(0);
        });
        return menu;
    }

    private ContextMenu getCellCtxMenu(ListCell cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, addBefore, addAfter);

        remove.setOnAction(event -> {
            removeLevel(cell.getIndex());
        });
        addBefore.setOnAction(event -> {
            addLevel(cell.getIndex());
        });
        addAfter.setOnAction(event -> {
            addLevel(cell.getIndex() + 1);
        });
        return menu;
    }

    private void removeLevel(int idx) {
        listViewMapNav.getItems().remove(idx);
    }

    private void addLevel(int idx) {
        MapVenue venue = new MapVenue("untitled");
        listViewMapNav.getItems().add(idx, venue);
        listViewMapNav.getSelectionModel().select(idx);
        updateContent(venue);
    }

    @Override
    public void update() {
        listViewMapNav.refresh();
    }

    @Override
    public void venueNameChanged() {
        update();
    }
}
