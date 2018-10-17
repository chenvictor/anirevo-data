package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import cvic.anirevo.model.anirevo.ArEvent;
import cvic.anirevo.model.anirevo.ArGuest;
import cvic.anirevo.model.anirevo.EventManager;
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
                if (cell.isEmpty()) {
                    cell.setContextMenu(getEmptyEventsMenu());
                } else {
                    cell.setContextMenu(getEventsMenu(cell));
                }
            });
            return cell;
        });
        listViewEvents.setContextMenu(getEmptyEventsMenu());
        SortedList<ArEvent> sortedEvents = new SortedList<>(events);
        sortedEvents.setComparator(Comparator.comparing(ArEvent::getTitle));
        listViewEvents.setItems(sortedEvents);
    }

    private ContextMenu getEmptyEventsMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);

        add.setOnAction(event -> {
            addEvent();
        });
        return menu;
    }

    private ContextMenu getEventsMenu(ListCell<ArEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        MenuItem remove = new MenuItem("Remove");
        menu.getItems().addAll(add, remove);

        add.setOnAction(event -> {
            addEvent();
        });
        remove.setOnAction(event -> {
            removeEvent(cell.getItem());
        });

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
        } else {
            setDisable(true);
            textFieldName.setText("");
            textFieldJapanese.setText("");
            textFieldTitle.setText("");
            imagePortrait.setImage(null);
        }
    }

    private void setDisable(boolean disable) {
        textFieldName.setDisable(disable);
        textFieldJapanese.setDisable(disable);
        textFieldTitle.setDisable(disable);
        imagePortrait.setDisable(disable);
        buttonChangeImage.setDisable(disable);
        listViewEvents.setDisable(disable);
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
