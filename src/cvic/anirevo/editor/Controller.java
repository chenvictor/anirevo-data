package cvic.anirevo.editor;

import cvic.anirevo.Log;
import cvic.anirevo.model.VenueManager;
import cvic.anirevo.model.ViewingRoomManager;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.parser.*;
import cvic.anirevo.utils.JSONUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {

    @FXML
    private AnchorPane navPane, contentPane;

    @FXML
    private Tab tabGeneral, tabLocations, tabEvents, tabGuests, tabViewingRooms, tabMap;

    private TabInteractionHandler handler;

    public void initialize() {
        handler = new TabInteractionHandler();
        loadAll();
        // tabChanged();    loadAll already calls tabChanged();
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
        } else if (tabViewingRooms.isSelected()) {
            loadTab("tabs/tab_nav_viewing_rooms.fxml", "tabs/tab_content_viewing_rooms.fxml");
        } else if (tabMap.isSelected()) {
            loadTab("tabs/tab_nav_map.fxml", "tabs/tab_content_map.fxml");
        }
    }

    public void onSave() {
        Unparser.info();
        Unparser.locations();
        Unparser.events();
        Unparser.guests();
        Unparser.viewingRooms();
        Unparser.map();
        Log.notify("Anirevo-data", "Data saved");
    }

    public void onLoad() {
        loadAll();
        Log.notify("Anirevo-data", "Data loaded");
    }

    private void loadTab(String navFXML, String contentFXML) {
        Node node;
        Object navController = null;
        Object contentController = null;
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
                contentPane.getChildren().setAll(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            contentPane.getChildren().clear();
        }
        handler.setContentController(contentController);
        handler.setNavController(navController);
    }

    private void loadAll() {
        //clear managers
        LocationManager.getInstance().clear();
        CategoryManager.getInstance().clear();
        EventManager.getInstance().clear();
        DateManager.getInstance().clear();
        ViewingRoomManager.getInstance().clear();
        GuestManager.getInstance().clear();
        VenueManager.getInstance().clear();
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
        try {
            ViewingRoomParser.parseViewingRoom(JSONUtils.getArray(DataPaths.JSON_VIEWING_ROOMS));
        } catch (FileNotFoundException e) {
            System.out.println("viewing_rooms.json not found");
            e.printStackTrace();
        }
        try {
            MapParser.parse(JSONUtils.getArray(DataPaths.JSON_MAP));
        } catch (FileNotFoundException e) {
            System.out.println("map.json not found");
            e.printStackTrace();
        }

        //reset tab
        tabChanged();
    }

    @FXML
    private void printStatus() {
        printViewingRoomManager();
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

    private void printViewingRoomManager() {
        System.out.println("ViewingRoom Manager");
        System.out.println("Dates:");
        for (CalendarDate date : DateManager.getInstance()) {
            System.out.println("  " + date.getName() + ":");
            for (ViewingRoomManager.ViewingRoom room : ViewingRoomManager.getInstance().getViewingRooms(date)) {
                System.out.println("  -" + room.getPurpose());
            }
        }
    }

}
