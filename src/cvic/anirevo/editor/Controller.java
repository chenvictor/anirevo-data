package cvic.anirevo.editor;

import cvic.anirevo.editor.tabs.*;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.parser.EventParser;
import cvic.anirevo.parser.GuestParser;
import cvic.anirevo.parser.InfoParser;
import cvic.anirevo.parser.LocationParser;
import cvic.anirevo.utils.JSONUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {

    @FXML
    private AnchorPane navPane, contentPane;

    @FXML
    private Tab tabGeneral, tabLocations, tabEvents, tabGuests;

    private SubController subController;

    public void initialize() {
        loadAll();
        tabChanged();
    }

    @FXML
    public void tabChanged() {
        if (contentPane == null || navPane == null) {
            return;
        }
        if (tabGeneral.isSelected()) {
            loadTab(null, "tabs/tab_content_general.fxml");
        } else if (tabLocations.isSelected()) {
            loadTab("tabs/tab_nav_locations.fxml", "tabs/tab_content_locations.fxml");
        } else if (tabEvents.isSelected()) {
            loadTab("tabs/tab_nav_events.fxml", "tabs/tab_content_events.fxml");
        } else if (tabGuests.isSelected()) {
            tabGueSelect();
        }
    }

    public void onSave() {
        if (subController != null) {
            subController.onSaveBtn();
        }
    }

    public void onLoad() {
        if (subController != null) {
            subController.onLoadBtn();
        }
    }

    private void loadTab(String navFXML, String contentFXML) {
//        if (ChangeDetector.isChanged()) {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Changes may not have been saved! Save before switching tabs?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
//
//            alert.showAndWait();
//            if (alert.getResult() == ButtonType.CANCEL) {
//                //Cancel closing the window
//                return;
//            } else if (alert.getResult() == ButtonType.YES) {
//                onSave();
//            }
//            ChangeDetector.setSaved();
//        }
        subController = null;
        Node node;
        Object navController = null;
        Object contentController;
        if (navFXML != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(navFXML));
                node = loader.load();
                navController = loader.getController();
                if (subController == null && navController instanceof SubController) {
                    subController = (SubController) navController;
                }
                navPane.getChildren().setAll(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            navPane.getChildren().clear();
        }
        if (contentFXML != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(contentFXML));
                node = loader.load();
                contentController = loader.getController();
                if (subController == null && contentController instanceof SubController) {
                    subController = (SubController) contentController;
                }
                if (navController != null) {
                    if (navController instanceof TabLocationsNavController) {
                        ((TabLocationsNavController) navController).setListener((TabLocationsContentController) contentController);
                    } else if (navController instanceof TabEventsNavController) {
                        ((TabEventsNavController) navController).setListener((TabEventsContentController) contentController);
                    }
                }
                contentPane.getChildren().setAll(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            contentPane.getChildren().clear();
            subController = null;
        }
    }

    private void tabGueSelect() {
        System.out.println("Guests Tab");
    }

    private void loadAll() {
        //clear managers
        LocationManager.getInstance().clear();
        CategoryManager.getInstance().clear();
        EventManager.getInstance().clear();
        DateManager.getInstance().clear();
        GuestManager.getInstance().clear();
        TagManager.getInstance().clear();

        try {
            InfoParser.parseInfo(JSONUtils.getObject(DataPaths.JSON_INFO));
        } catch (FileNotFoundException e) {
            System.out.println("info.json not found");
            e.printStackTrace();
        }
        try {
            LocationParser.parseLocs(JSONUtils.getArray(DataPaths.JSON_LOCATIONS));
        } catch (FileNotFoundException e) {
            System.out.println("locations.json not found");
            e.printStackTrace();
        }
        try {
            EventParser.parseEvents(JSONUtils.getArray(DataPaths.JSON_EVENTS));
        } catch (FileNotFoundException e) {
            System.out.println("events.json not found");
            e.printStackTrace();
        }
        try {
            GuestParser.parseGuests(JSONUtils.getArray(DataPaths.JSON_GUESTS));
        } catch (FileNotFoundException e) {
            System.out.println("guests.json not found");
            e.printStackTrace();
        }

        //reset tab
        tabChanged();
    }

}
