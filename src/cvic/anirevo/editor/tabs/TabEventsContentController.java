package cvic.anirevo.editor.tabs;

import cvic.anirevo.model.anirevo.ArEvent;
import cvic.anirevo.model.anirevo.ArLocation;
import cvic.anirevo.model.anirevo.LocationManager;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.model.calendar.EventTime;
import cvic.anirevo.parser.EventTimeParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.awt.Point;
import java.awt.MouseInfo;
import java.util.*;

public class TabEventsContentController implements TabEventsNavController.EventsNavListener {

    private ArEvent mEvent;

    @FXML
    private TextField textFieldTitle, textFieldLocation;

    @FXML
    private ChoiceBox<Integer> choiceAge;

    @FXML
    private ChoiceBox<Integer> choiceBoxLocation;

    @FXML
    private TextArea textAreaDescription;

    @FXML
    private ListView<CalendarEvent> listViewTimeBlocks;

    private ObservableList<CalendarEvent> timeblocks = FXCollections.observableArrayList();

    private Timer timer;

    public void initialize() {
        timer = new Timer();
        SortedList<CalendarEvent> sortedEvents = new SortedList<>(timeblocks);
        sortedEvents.setComparator(Comparator.comparing(CalendarEvent::toString));
        listViewTimeBlocks.setItems(sortedEvents);
        initChoiceBox();
        initListView();
        sync();
        textFieldTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldLocation.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textAreaDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
    }

    private void initChoiceBox() {
        choiceAge.getItems().addAll(0, 13, 18);
        choiceAge.getSelectionModel().select(0);
        choiceAge.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        choiceAge.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == 0) {
                    return "NONE";
                }
                return String.valueOf(object) + "+";
            }

            @Override
            public Integer fromString(String string) {
                switch (string) {
                    case "13+":
                        return 13;
                    case "18+":
                        return 18;
                    default:
                        return 0;
                }
            }
        });
        for (int i = 0; i < LocationManager.getInstance().getLocations().size(); i++) {
            choiceBoxLocation.getItems().add(i);
        }
        choiceBoxLocation.getItems().add(-1);
        choiceBoxLocation.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == -1) {
                    return "Other";
                }
                return LocationManager.getInstance().getLocation(object).getPurpose();
            }

            @Override
            public Integer fromString(String string) {
                if (string.equals("Other")) {
                    return -1;
                }
                return LocationManager.getInstance().getLocations().indexOf(LocationManager.getInstance().getLocation(string));
            }
        });
        choiceBoxLocation.getSelectionModel().select(0);
        choiceBoxLocation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int val = choiceBoxLocation.getSelectionModel().getSelectedItem();
            if (val == -1) {
                textFieldLocation.setText("");
                textFieldLocation.requestFocus();
                textFieldLocation.setDisable(false);
            } else {
                textFieldLocation.setText(LocationManager.getInstance().getLocation(val).getPurpose());
                textFieldLocation.setDisable(true);
            }
            changed();
        });
    }

    private void initListView() {
        listViewTimeBlocks.setContextMenu(getEmptyTimeblockCtxMenu());
        listViewTimeBlocks.setCellFactory(lv -> {
            ListCell<CalendarEvent> cell = new ListCell<CalendarEvent>() {
                @Override
                protected void updateItem(CalendarEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    listViewTimeBlocks.getSelectionModel().select(-1);
                }
            });
            cell.setContextMenu(getTimeblockCtxMenu(cell));
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (cell.isEmpty()) {
                    event.consume();
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    listViewTimeBlocks.getContextMenu().show(cell, p.getX(), p.getY());
                }
            });
            return cell;
        });
    }

    private ContextMenu getTimeblockCtxMenu(ListCell<CalendarEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem edit = new MenuItem("Edit");
        MenuItem add = new MenuItem("Add");
        menu.getItems().addAll(remove, edit, add);

        remove.setOnAction(event -> {
            timeblocks.remove(cell.getItem());
        });
        edit.setOnAction(event -> {
            editTimeblock(cell.getItem());
            cell.getListView().refresh();
        });
        add.setOnAction(event -> {
            addTimeblock();
        });
        return menu;
    }

    private ContextMenu getEmptyTimeblockCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addTimeblock();
        });
        return menu;
    }

    private void editTimeblock(CalendarEvent block) {
        CalendarEvent newInfo = timeblockInput(block);
        if (newInfo != null) {
            block.setDate(newInfo.getDate());
            block.setStart(newInfo.getStartTime());
            block.setEnd(newInfo.getEndTime());
        }
    }

    private void addTimeblock() {
        CalendarEvent timeblock = timeblockInput(null);
        if (timeblock != null) {
            timeblocks.add(timeblock);
            listViewTimeBlocks.getSelectionModel().select(timeblock);
        }
    }

    private CalendarEvent timeblockInput(CalendarEvent defaultOptions) {
        if (defaultOptions == null) {
            defaultOptions = new CalendarEvent(null);   //null placeholder event
        }
        CalendarDate date = timeblockDatePrompt(defaultOptions.getDate());
        if (date == null) {
            return null;
        }
        EventTime start = eventTimePrompt(defaultOptions.getStartTime());
        if (start == null) {
            return null;
        }
        EventTime end = eventTimePrompt(defaultOptions.getEndTime());
        if (end == null) {
            return null;
        }
        CalendarEvent timeblock = new CalendarEvent(mEvent);
        timeblock.setDate(date);
        timeblock.setStart(start);
        timeblock.setEnd(end);
        return timeblock;
    }

    private CalendarDate timeblockDatePrompt(CalendarDate defaultOption) {
        List<CalendarDate> options = getDateOptions();
        ChoiceDialog<CalendarDate> dialog = new ChoiceDialog<>(defaultOption, options);
        dialog.setHeaderText(null);
        dialog.setTitle("Timeblock");
        dialog.setContentText("Date:");
        Optional<CalendarDate> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    private EventTime eventTimePrompt(EventTime defaultOption) {
        String def = "";
        if (defaultOption != null) {
            def = defaultOption.toString();
        }
        TextInputDialog dialog = new TextInputDialog(def);
        dialog.setTitle("Timeblock");
        dialog.setHeaderText(null);
        dialog.setContentText("Time:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                return EventTimeParser.parse(result.get());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private List<CalendarDate> getDateOptions() {
        List<CalendarDate> options = new ArrayList<>();
        for (CalendarDate date : DateManager.getInstance()) {
            options.add(date);
        }
        return options;
    }

    private void saveEvent() {
        if (mEvent != null) {
            mEvent.setTitle(textFieldTitle.getText());
            mEvent.setLocation(textFieldLocation.getText());
            mEvent.setDesc(textAreaDescription.getText());
            mEvent.setRestriction(choiceAge.getValue());
            mEvent.clearTimeblocks();
            for (CalendarEvent block : timeblocks) {
                mEvent.addTimeblock(block);
            }
        }
    }

    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveEvent();
            }
        }, 500);
    }

    private void sync() {
        if (textFieldTitle != null) {
            timeblocks.clear();
            if (mEvent != null) {
                setDisable(false);
                textFieldTitle.setText(mEvent.getTitle());
                int locIdx = getLocIndex(mEvent.getLocation());
                choiceBoxLocation.getSelectionModel().select(getLocIndex(mEvent.getLocation()));
                textFieldLocation.setText(mEvent.getLocation());
                if (locIdx == getNumLocs()) {
                    textFieldLocation.setDisable(false);
                } else {
                    textFieldLocation.setDisable(true);
                }
                textAreaDescription.setText(mEvent.getDesc());
                choiceAge.getSelectionModel().select(getIndex(mEvent.getRestriction()));
                timeblocks.addAll(mEvent.getTimeblocks());
            } else {
                setDisable(true);
                textFieldTitle.setText("");
                choiceBoxLocation.getSelectionModel().select(0);
                textFieldLocation.setText("");
                textAreaDescription.setText("");
                choiceAge.getSelectionModel().select(0);
            }
        }
    }

    private void setDisable(boolean disable) {
        textFieldTitle.setDisable(disable);
        choiceBoxLocation.setDisable(disable);
        textFieldLocation.setDisable(disable);
        textAreaDescription.setDisable(disable);
        choiceAge.setDisable(disable);
        listViewTimeBlocks.setDisable(disable);
    }

    private int getIndex(int age) {
        switch (age) {
            case 13: return 1;
            case 18: return 2;
            default: return 0;
        }
    }

    @Override
    public void itemSelected(ArEvent event) {
        save(); //saves the previous item
        mEvent = event;
        sync();
    }

    @Override
    public void save() {
        saveEvent();
    }

    private int getLocIndex(String purpose) {
        LocationManager manager = LocationManager.getInstance();
        int idx = 0;
        for (ArLocation loc : manager) {
            if (loc.getPurpose().equals(purpose)) {
                return idx;
            }
            idx++;
        }
        return idx;
    }

    private int getNumLocs() {
        return LocationManager.getInstance().size();
    }
}
