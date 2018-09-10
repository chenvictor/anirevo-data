package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DragCell;
import cvic.anirevo.editor.TabInteractionHandler.NavigationController;
import cvic.anirevo.model.anirevo.ArLocation;
import cvic.anirevo.model.anirevo.LocationManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

public class TabLocationsNavController extends NavigationController implements ArLocation.UpdateListener{

    @FXML
    private ListView<ArLocation> listViewLocationsNav;

    public void initialize() {
        listViewLocationsNav.setContextMenu(getBaseCtxMenu());
        listViewLocationsNav.setCellFactory(lv -> {
            DragCell<ArLocation> cell = new DragCell<ArLocation>() {
                @Override
                protected void updateItem(ArLocation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getPurpose());
                        item.setListener(TabLocationsNavController.this);
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    //select
                    updateContent(cell.getItem());
                }
            });
            cell.setContextMenu(getCtxMenu(cell));
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (cell.isEmpty()) {
                    event.consume();
                }
            });
            cell.prefWidthProperty().bind(listViewLocationsNav.prefWidthProperty());
            cell.setMaxWidth(Control.USE_PREF_SIZE);
            return cell;
        });
        listViewLocationsNav.setItems(LocationManager.getInstance().getLocations());
    }

    private ContextMenu getCtxMenu(ListCell cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, addBefore, addAfter);

        remove.setOnAction(event -> {
            removeLoc(cell.getIndex());
        });
        addBefore.setOnAction(event -> {
            addLoc(cell.getIndex());
        });
        addAfter.setOnAction(event -> {
            addLoc(cell.getIndex() + 1);
        });
        return menu;
    }

    private ContextMenu getBaseCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Location");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addLoc(0);
        });
        return menu;
    }

    private void removeLoc(int idx) {
        listViewLocationsNav.getItems().remove(idx);
    }

    private void addLoc(int idx) {
        ArLocation newLoc = new ArLocation("untitled");
        newLoc.setLocation("untitled");
        listViewLocationsNav.getItems().add(idx, newLoc);
        listViewLocationsNav.getSelectionModel().select(idx);
        updateContent(newLoc);
    }

    @Override
    public void update() {
        listViewLocationsNav.refresh();
    }
}
