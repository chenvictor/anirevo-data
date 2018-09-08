package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DragCell;
import cvic.anirevo.model.anirevo.ArGuest;
import cvic.anirevo.model.anirevo.GuestManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

public class TabGuestsNavController implements ArGuest.ChangeListener{

    @FXML
    private ListView<ArGuest> listViewGuestsNav;

    private GuestsNavListener mListener;

    public void setListener(GuestsNavListener listener) {
        mListener = listener;
        if (!listViewGuestsNav.getItems().isEmpty()) {
            listViewGuestsNav.getSelectionModel().select(0);
            mListener.itemSelected(listViewGuestsNav.getItems().get(0));
        }
    }

    public void initialize() {
        listViewGuestsNav.setContextMenu(getEmptyCtxMenu());
        listViewGuestsNav.setCellFactory(lv -> {
            DragCell<ArGuest> cell = new DragCell<ArGuest>(){
                @Override
                protected void updateItem(ArGuest item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        item.setListener(TabGuestsNavController.this);
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    mListener.itemSelected(cell.getItem());
                }
            });
            cell.setContextMenu(getCellCtxMenu(cell));
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (cell.isEmpty()) {
                    event.consume();
                }
            });
            cell.prefWidthProperty().bind(listViewGuestsNav.prefWidthProperty());
            cell.setMaxWidth(Control.USE_PREF_SIZE);
            return cell;
        });
        listViewGuestsNav.setItems(GuestManager.getInstance().getGuests());
    }

    private ContextMenu getEmptyCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Guest");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addGuest(0);
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
            removeGuest(cell.getIndex());
        });
        addBefore.setOnAction(event -> {
            addGuest(cell.getIndex());
        });
        addAfter.setOnAction(event -> {
            addGuest(cell.getIndex() + 1);
        });
        return menu;
    }

    private void removeGuest(int idx) {
        listViewGuestsNav.getItems().remove(idx);
    }

    private void addGuest(int idx) {
        ArGuest guest = new ArGuest("untitled");
        guest.setTitle("...");
        listViewGuestsNav.getItems().add(idx, guest);
        listViewGuestsNav.getSelectionModel().select(idx);
        if (mListener != null) {
            mListener.itemSelected(guest);
        }
    }

    @Override
    public void update() {
        listViewGuestsNav.refresh();
    }

    public interface GuestsNavListener {

        void itemSelected(ArGuest guest);

    }

}
