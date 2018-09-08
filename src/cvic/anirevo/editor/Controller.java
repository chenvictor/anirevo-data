package cvic.anirevo.editor;

import cvic.anirevo.Log;
import cvic.anirevo.editor.tabs.*;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.parser.*;
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
            loadTab("tabs/tab_nav_guests.fxml", "tabs/tab_content_guests.fxml");
        }
    }

    public void onSave() {
        Unparser.info();
        Unparser.locations();
        Unparser.events();
        Unparser.guests();
        Log.notify("Anirevo-data", "Data saved");
    }

    public void onLoad() {
        loadAll();
        Log.notify("Anirevo-data", "Data loaded");
    }

    private void loadTab(String navFXML, String contentFXML) {
        Node node;
        Object navController = null;
        Object contentController;
        if (navFXML != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(navFXML));
                node = loader.load();
                navController = loader.getController();
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
                if (navController != null) {
                    if (navController instanceof TabLocationsNavController) {
                        ((TabLocationsNavController) navController).setListener((TabLocationsContentController) contentController);
                    } else if (navController instanceof TabEventsNavController) {
                        ((TabEventsNavController) navController).setListener((TabEventsContentController) contentController);
                    } else if (navController instanceof TabGuestsNavController) {
                        ((TabGuestsNavController) navController).setListener((TabGuestsContentController) contentController);
                    }
                }
                contentPane.getChildren().setAll(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            contentPane.getChildren().clear();
        }
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

    @FXML
    private void printStatus() {
        //printDateManager();
        //printLocationManager();
        //printCategoryManager();
    }

    private void printDateManager() {
        System.out.println("Date Manager");
        System.out.println("Year: " + DateManager.getInstance().getYear());
        System.out.println("Dates:");
        for (CalendarDate date : DateManager.getInstance().getDates()) {
            System.out.println("  -" + date.getName());
        }
    }

    private void printLocationManager() {
        System.out.println("Location Manager");
        System.out.println("Locations:");
        for (ArLocation loc : LocationManager.getInstance()) {
            System.out.println(loc.getPurpose() + " - " + loc.getLocation() + (loc.isSchedule() ? " sched" : ""));
        }
    }

    private void printCategoryManager() {
        System.out.println("Category Manager");
        System.out.println("Categories:");
        for (ArCategory cat : CategoryManager.getInstance()) {
            System.out.println("  " + cat.getTitle() + ":");
            for (ArEvent event : cat) {
                System.out.println("  -" + event.getTitle());
            }
        }
    }

}
