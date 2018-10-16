package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DragCell;
import cvic.anirevo.editor.TabInteractionHandler.NavigationController;
import cvic.anirevo.model.ArShow;
import cvic.anirevo.model.ViewingRoomManager;
import cvic.anirevo.model.ViewingRoomManager.ViewingRoom;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.util.Timer;
import java.util.TimerTask;

public class TabViewingRoomsNavController extends NavigationController implements ArShow.TitleChangeListener {

    @FXML
    private ChoiceBox<CalendarDate> choiceBoxDate;

    @FXML
    private ChoiceBox<ViewingRoom> choiceBoxRoom;

    @FXML
    private TextField textFieldStartHour;

    @FXML
    private ListView<ArShow> listShows;

    private CalendarDate mDate;

    private Timer timer;

    public void initialize() {
        timer = new Timer();
        listShows.setContextMenu(getBaseCtxMenu());
        listShows.setCellFactory(lv -> {
            DragCell<ArShow> cell = new DragCell<ArShow>() {
                @Override
                protected void updateItem (ArShow show, boolean empty) {
                    super.updateItem(show, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(show.getTitle());
                        show.setListener(TabViewingRoomsNavController.this);
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
            return cell;
        });
        choiceBoxDate.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> dateSelected(newValue));
        choiceBoxRoom.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> roomSelected(choiceBoxRoom.getValue()));
        choiceBoxDate.setItems(DateManager.getInstance().getDates());
        choiceBoxDate.getSelectionModel().select(0);
    }

    private ContextMenu getBaseCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Show");

        menu.getItems().add(add);
        add.setOnAction(event -> addShow(0));
        return menu;
    }

    private ContextMenu getCtxMenu(ListCell cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, addBefore, addAfter);

        remove.setOnAction(event -> removeShow(cell.getIndex()));
        addBefore.setOnAction(event -> addShow(cell.getIndex()));
        addAfter.setOnAction(event -> addShow(cell.getIndex() + 1));
        return menu;
    }

    private void dateSelected (CalendarDate newValue) {
        if (newValue == null) {
            return;
        }
        mDate = newValue;
        if (mDate.getShowStartHour() == null) {
            textFieldStartHour.setText("");
        } else {
            textFieldStartHour.setText(mDate.getShowStartHour());
        }
        choiceBoxRoom.setItems(ViewingRoomManager.getInstance().getViewingRooms(mDate));
        choiceBoxRoom.getSelectionModel().select(0);
    }

    private void roomSelected(ViewingRoom newValue) {
        if (newValue != null) {
            listShows.setItems(newValue.getShows());
        }
    }

    private void addShow (int index) {
        ArShow show = new ArShow("untitled");
        listShows.getItems().add(index, show);
        listShows.getSelectionModel().select(index);
        updateContent(show);
    }

    private void removeShow (int index) {
        listShows.getItems().remove(index);
        if (listShows.getItems().size() != 0) {
            updateContent(listShows.getItems().get(index));
        }
    }

    @FXML
    private void startHourChanged() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDate.setShowStartHour(textFieldStartHour.getText());
            }
        }, 500);
    }

    @Override
    public void update() {
        listShows.refresh();
    }

    @Override
    public void titleChanged(String newTitle) {
        listShows.refresh();
    }
}
