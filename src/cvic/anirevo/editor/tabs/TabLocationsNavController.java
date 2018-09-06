package cvic.anirevo.editor.tabs;

import cvic.anirevo.Log;
import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.editor.DragCell;
import cvic.anirevo.model.anirevo.ArLocation;
import cvic.anirevo.model.anirevo.LocationManager;
import cvic.anirevo.parser.LocationParser;
import cvic.anirevo.utils.JSONUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class TabLocationsNavController implements SubController {

    private LocationsNavListener mListener;

    public void setListener(LocationsNavListener listener) {
        mListener = listener;
        if (!listViewLocationsNav.getItems().isEmpty()) {
            listViewLocationsNav.getSelectionModel().select(0);
            mListener.itemSelected(listViewLocationsNav.getItems().get(0));
        }
    }

    @FXML
    private ListView<ArLocation> listViewLocationsNav;

    public void initialize() {
        listViewLocationsNav.setContextMenu(getBaseCtxMenu());
        listViewLocationsNav.setCellFactory(lv -> {
            DragCell cell = new DragCell<ArLocation>() {
                @Override
                protected void updateItem(ArLocation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getPurpose());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    //select
                    mListener.itemSelected((ArLocation) cell.getItem());
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
        listViewLocationsNav.getItems().clear();
        for (ArLocation loc : LocationManager.getInstance()) {
            listViewLocationsNav.getItems().add(loc);
        }
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
        if (mListener != null) {
            mListener.itemSelected(newLoc);
        }
    }

    @Override
    public void onSaveBtn() {
        mListener.save(); //Save the content first
        JSONArray output = new JSONArray();
        for (ArLocation loc : listViewLocationsNav.getItems()) {
            JSONObject locObj = new JSONObject();
            locObj.put("purpose", loc.getPurpose());
            locObj.put("location", loc.getLocation());
            if (loc.isSchedule()) {
                locObj.put("schedule", loc.isSchedule());
            }
            output.put(locObj);
        }
        JSONUtils.writeJSON(DataPaths.JSON_LOCATIONS, output);
    }

    @Override
    public void onLoadBtn() {
        LocationManager.getInstance().clear();
        try {
            LocationParser.parseLocs(JSONUtils.getArray(DataPaths.JSON_LOCATIONS));
            Log.notify("General", "Loaded data from file: " + DataPaths.JSON_LOCATIONS);
        } catch (FileNotFoundException e) {
            Log.notify("General", "File not found: " + DataPaths.JSON_LOCATIONS);
            e.printStackTrace();
        }
        initialize();
    }

    public interface LocationsNavListener {

        void itemSelected(ArLocation location);

        void save();

    }

}
