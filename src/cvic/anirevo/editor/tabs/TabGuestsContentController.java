package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.model.calendar.LocatedCalendarEvent;
import cvic.anirevo.parser.EventTimeParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import sun.util.resources.CalendarData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TabGuestsContentController extends ContentController {

    @FXML
    private TextField textFieldName, textFieldTitle, textFieldJapanese;

    @FXML
    private ImageView imagePortrait;

    @FXML
    private Button buttonChangeImage;

    @FXML
    private ListView<ArEvent> listViewEvents;

    @FXML
    private ListView<LocatedCalendarEvent> listViewAutographs;

    @FXML
    private ListView<LocatedCalendarEvent> listViewPhotobooth;

    private ObservableList<ArEvent> events = FXCollections.observableArrayList();

    private ArGuest mGuest;

    private Timer timer;

    private FileChooser fileChooser;

    public void initialize() {
        timer = new Timer();
        sync();
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldJapanese.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Guest Portrait");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        fileChooser.setInitialDirectory(new File(DataPaths.IMAGES));
        initListViews();
    }

    private void initListViews() {
        listViewEvents.setCellFactory(lv -> {
            ListCell<ArEvent> cell = new ListCell<ArEvent>(){
                @Override
                protected void updateItem(ArEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    listViewEvents.getSelectionModel().select(-1);
                }
            });
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                cell.setContextMenu(cell.isEmpty() ? getEmptyEventsMenu() : getEventsMenu(cell));
            });
            return cell;
        });
        listViewEvents.setContextMenu(getEmptyEventsMenu());
        SortedList<ArEvent> sortedEvents = new SortedList<>(events);
        sortedEvents.setComparator(Comparator.comparing(ArEvent::getTitle));
        listViewEvents.setItems(sortedEvents);

        listViewAutographs.setCellFactory(lv -> {
            ListCell<LocatedCalendarEvent> cell = new ListCell<LocatedCalendarEvent>(){
                @Override
                protected void updateItem(LocatedCalendarEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    setText (empty ? null : item.getName());
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    listViewAutographs.getSelectionModel().select(-1);
                }
            });
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                cell.setContextMenu(cell.isEmpty() ? getEmptyAutographMenu() : getAutographMenu(cell));
            });
            return cell;
        });
        listViewAutographs.setContextMenu(getEmptyAutographMenu());

        listViewPhotobooth.setCellFactory(lv -> {
            ListCell<LocatedCalendarEvent> cell = new ListCell<LocatedCalendarEvent>(){
                @Override
                protected void updateItem(LocatedCalendarEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    setText (empty ? null : item.getName());
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    listViewAutographs.getSelectionModel().select(-1);
                }
            });
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                cell.setContextMenu(cell.isEmpty() ? getEmptyPhotoboothMenu() : getPhotoboothMenu(cell));
            });
            return cell;
        });
        listViewPhotobooth.setContextMenu(getEmptyPhotoboothMenu());
    }

    private ContextMenu getEmptyEventsMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);

        add.setOnAction(event -> addEvent());
        return menu;
    }

    private ContextMenu getEventsMenu(ListCell<ArEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        MenuItem remove = new MenuItem("Remove");
        menu.getItems().addAll(add, remove);

        add.setOnAction(event -> addEvent());
        remove.setOnAction(event -> removeEvent(cell.getItem()));

        return menu;
    }

    private ContextMenu getEmptyAutographMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);
        add.setOnAction(event -> addAutograph());
        return menu;
    }

    private ContextMenu getAutographMenu (ListCell<LocatedCalendarEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        MenuItem remove = new MenuItem("Remove");
        menu.getItems().addAll(add, remove);

        add.setOnAction(event -> addAutograph());
        remove.setOnAction(event -> removeAutograph(cell.getItem()));

        return menu;
    }

    private ContextMenu getEmptyPhotoboothMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);

        add.setOnAction(event -> addPhotobooth());
        return menu;
    }

    private ContextMenu getPhotoboothMenu(ListCell<LocatedCalendarEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        MenuItem remove = new MenuItem("Remove");
        menu.getItems().addAll(add, remove);

        add.setOnAction(event -> addPhotobooth());
        remove.setOnAction(event -> removePhotobooth(cell.getItem()));

        return menu;
    }

    @FXML
    private void changeImage() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            File baseDirectory = new File(DataPaths.IMAGES);
            if (!file.getParentFile().getAbsolutePath().equals(baseDirectory.getAbsolutePath())) {
                System.out.println("Copy file over");
                //file must be copied over
                try {
                    Files.copy(file.toPath(), baseDirectory.toPath().resolve(file.getName()), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mGuest.setPortraitPath(file.getName());
            sync();
        }
    }

    private void addEvent() {
        List<ArEvent> choices = EventManager.getInstance().getEvents();
        ChoiceDialog<ArEvent> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Add event");
        dialog.setHeaderText(null);
        Optional<ArEvent> result = dialog.showAndWait();
        if (result.isPresent()) {
            ArEvent event = result.get();
            if (!events.contains(event)) {
                events.add(event);
                changed();
            }
        }
    }

    private void removeEvent(ArEvent event) {
        events.remove(event);
        event.getGuests().remove(mGuest);
        changed();
    }

    private void addAutograph() {
        LocatedCalendarEvent event = getLocatedCalendarEvent();
        if (event != null) {
            mGuest.getAutograph().add(event);
        }
    }


    private void removeAutograph (CalendarEvent event) {
        mGuest.getAutograph().remove(event);
    }

    private void addPhotobooth() {
        LocatedCalendarEvent event = getLocatedCalendarEvent();
        if (event != null) {
            mGuest.getPhotobooth().add(event);
        }
    }

    private void removePhotobooth (CalendarEvent event) {
        mGuest.getPhotobooth().remove(event);
    }

    private LocatedCalendarEvent getLocatedCalendarEvent() {
        ArLocation arLoc;
        CalendarDate calDate;
        String startTime;
        String endTime;

        List<ArLocation> locChoices = LocationManager.getInstance().getLocations();
        ChoiceDialog<ArLocation> selectLocation = new ChoiceDialog<>(locChoices.get(0), locChoices);
        selectLocation.setTitle("Add Event");
        selectLocation.setHeaderText("Set Location");
        Optional<ArLocation> location = selectLocation.showAndWait();
        if (location.isPresent()) {
            arLoc = location.get();
        } else {
            return null;
        }
        List<CalendarDate> dateChoices = DateManager.getInstance().getDates();
        ChoiceDialog<CalendarDate> selectDate = new ChoiceDialog<>(dateChoices.get(0), dateChoices);
        selectDate.setTitle("Add Event");
        selectDate.setHeaderText("Set Date");
        Optional<CalendarDate> date = selectDate.showAndWait();
        if (date.isPresent()) {
            calDate = date.get();
        } else {
            return null;
        }
        TextInputDialog timeDialog = new TextInputDialog();
        timeDialog.setTitle("Add Event");
        timeDialog.setHeaderText("Set start time");
        Optional<String> start = timeDialog.showAndWait();
        if (start.isPresent()) {
            startTime = start.get();
        } else {
            return null;
        }
        timeDialog.setHeaderText("Set end time");
        Optional<String> end = timeDialog.showAndWait();
        if (end.isPresent()) {
            endTime = end.get();
        } else {
            return null;
        }

        LocatedCalendarEvent event = new LocatedCalendarEvent();
        event.setDate(calDate);
        event.setLocation(arLoc);
        event.setStart(EventTimeParser.parse(startTime));
        event.setEnd(EventTimeParser.parse(endTime));
        return event;
    }

    private void save() {
        if (mGuest != null) {
            mGuest.setName(textFieldName.getText());
            mGuest.setTitle(textFieldTitle.getText());
            mGuest.setJapanese(textFieldJapanese.getText());
            mGuest.getEvents().clear();
            for (ArEvent event : events) {
                mGuest.addEvent(event);
                event.getGuests().add(mGuest);
            }
        }
    }

    private void sync() {
        events.clear();
        if (mGuest != null) {
            setDisable(false);
            textFieldName.setText(mGuest.getName());
            textFieldTitle.setText(mGuest.getTitle());
            textFieldJapanese.setText(mGuest.getJapanese());
            if (mGuest.getPortraitPath() != null && mGuest.getPortraitPath().length() != 0) {
                try {
                    File file = new File(DataPaths.IMAGES + mGuest.getPortraitPath());
                    Image image = new Image(file.toURI().toString());
                    imagePortrait.setImage(image);
                } catch (Exception e) {
                    //something wrong with portrait path, remove it
                    mGuest.setPortraitPath(null);
                }
            } else {
                imagePortrait.setImage(null);
            }
            events.addAll(mGuest.getEvents());
            SortedList<LocatedCalendarEvent> sortedAutograph = new SortedList<>(mGuest.getAutograph());
            sortedAutograph.setComparator(Comparator.comparing(LocatedCalendarEvent::getName));
            listViewAutographs.setItems(sortedAutograph);
            SortedList<LocatedCalendarEvent> sortedPhotobooth = new SortedList<>(mGuest.getPhotobooth());
            sortedAutograph.setComparator(Comparator.comparing(LocatedCalendarEvent::getName));
            listViewPhotobooth.setItems(sortedPhotobooth);
        } else {
            setDisable(true);
            textFieldName.setText("");
            textFieldJapanese.setText("");
            textFieldTitle.setText("");
            imagePortrait.setImage(null);
            listViewAutographs.setItems(null);
            listViewPhotobooth.setItems(null);
        }
    }

    private void setDisable(boolean disable) {
        textFieldName.setDisable(disable);
        textFieldJapanese.setDisable(disable);
        textFieldTitle.setDisable(disable);
        imagePortrait.setDisable(disable);
        buttonChangeImage.setDisable(disable);
        listViewEvents.setDisable(disable);
        listViewAutographs.setDisable(disable);
        listViewPhotobooth.setDisable(disable);
    }

    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 500);
    }

    @Override
    public void itemSelected(Object guest) {
        if (guest == null || guest instanceof ArGuest) {
            mGuest = (ArGuest) guest;
            sync();
        }
    }
}
