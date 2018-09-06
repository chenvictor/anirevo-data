package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DragCell;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class TabGeneralContentController {

    @FXML
    private TextField field_year;

    @FXML
    private ListView<CalendarDate> list_dates;

    private Timer timer;

    public void initialize() {
        timer = new Timer();
        field_year.setText(String.valueOf(DateManager.getInstance().getYear()));
        field_year.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        list_dates.setContextMenu(getBaseCtxMenu());
        list_dates.setCellFactory(lv -> {
            DragCell<CalendarDate> cell = new DragCell<CalendarDate>() {
                @Override
                protected void updateItem(CalendarDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    //deselect
                    list_dates.getSelectionModel().select(-1);
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
        list_dates.setItems(DateManager.getInstance().getDates());
    }

    private ContextMenu getCtxMenu(ListCell cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem rename = new MenuItem("Rename");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, rename, addBefore, addAfter);

        remove.setOnAction(event -> {
            removeDate(cell.getIndex());
        });
        rename.setOnAction(event -> {
            renameDate(cell);
        });
        addBefore.setOnAction(event -> {
            addDate(cell.getIndex());
        });
        addAfter.setOnAction(event -> {
            addDate(cell.getIndex() + 1);
        });
        return menu;
    }

    private ContextMenu getBaseCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addDate(0);
        });
        return menu;
    }

    @FXML
    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int year = Integer.parseInt(field_year.getText());
                    DateManager.getInstance().setYear(year);
                } catch (Exception ignored) {

                }
            }
        }, 500);
    }

    private void addDate(int idx) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Date");
        dialog.setHeaderText("Enter date string");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (dateExists(result.get())) {
                dateExistsDialog();
            } else if (result.get().trim().length() == 0) {
                new Alert(Alert.AlertType.ERROR, "Nothing was entered!").show();
            } else {
                list_dates.getItems().add(idx, new CalendarDate(result.get()));
                list_dates.getSelectionModel().select(idx);
            }
        }
    }

    private void removeDate(int idx) {
        list_dates.getItems().remove(idx);
    }

    private void renameDate(ListCell cell) {
        CalendarDate date = (CalendarDate) cell.getItem();
        TextInputDialog dialog = new TextInputDialog(date.getName());
        dialog.setTitle("Rename date");
        dialog.setHeaderText("Enter new date string");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (dateExists(result.get())) {
                dateExistsDialog();
            } else if (result.get().trim().length() == 0) {
                new Alert(Alert.AlertType.ERROR, "Nothing was entered!").show();
            } else {
                date.setName(result.get());
                list_dates.refresh();
            }
        }
    }

    private void dateExistsDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Date name already exists!");
        alert.show();
    }

    private boolean dateExists(String name) {
        for (CalendarDate date : list_dates.getItems()) {
            if (date.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
