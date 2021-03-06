package cvic.anirevo.parser;

import cvic.anirevo.model.MapVenue;
import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.model.ArShow;
import cvic.anirevo.model.VenueManager;
import cvic.anirevo.model.ViewingRoomManager;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.model.calendar.LocatedCalendarEvent;
import cvic.anirevo.utils.JSONUtils;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Unparser {

    public static void info() {
        DateManager manager = DateManager.getInstance();
        JSONObject output = new JSONObject();
        output.put("year", manager.getYear());
        output.put("dates", arrayifyDates(manager));
        JSONUtils.writeJSON(DataPaths.JSON_INFO, output);
    }

    public static void locations() {
        LocationManager manager = LocationManager.getInstance();
        JSONArray output = new JSONArray();
        for (ArLocation loc : manager) {
            output.put(objectify(loc));
        }
        JSONUtils.writeJSON(DataPaths.JSON_LOCATIONS, output);
    }

    public static void events() {
        CategoryManager manager = CategoryManager.getInstance();
        JSONArray output = new JSONArray();
        for (ArCategory cat : manager) {
            output.put(objectify(cat));
        }
        JSONUtils.writeJSON(DataPaths.JSON_EVENTS, output);
    }

    public static void guests() {
        GuestManager manager = GuestManager.getInstance();
        JSONArray output = new JSONArray();
        for (ArGuest guest : manager) {
            output.put(objectify(guest));
        }
        JSONUtils.writeJSON(DataPaths.JSON_GUESTS, output);
    }

    public static void viewingRooms() {
        ViewingRoomManager manager = ViewingRoomManager.getInstance();
        JSONArray output = new JSONArray();
        for (CalendarDate date : manager.getKeys()) {
            JSONObject dateObject = new JSONObject();
            dateObject.put("day", date.getName());
            if (date.getShowStartHour() == null) {
                dateObject.put("start", "");
            } else {
                dateObject.put("start", date.getShowStartHour());
            }
            JSONArray roomsArray = new JSONArray();
            for (ViewingRoomManager.ViewingRoom room : manager.getViewingRooms(date)) {
                JSONObject roomObject = new JSONObject();
                roomObject.put("purpose", room.getPurpose());
                JSONArray shows = new JSONArray();
                for (ArShow show : room.getShows()) {
                    JSONObject showObject = new JSONObject();
                    showObject.put("title", show.getTitle());
                    if (show.getAge() != 0) {
                        showObject.put("age", show.getAge());
                    }
                    if (show.getDescription() != null && show.getDescription().length() != 0) {
                        showObject.put("desc", show.getDescription());
                    }
                    shows.put(showObject);
                }
                roomObject.put("shows", shows);
                roomsArray.put(roomObject);
            }
            dateObject.put("rooms", roomsArray);
            output.put(dateObject);
        }
        JSONUtils.writeJSON(DataPaths.JSON_VIEWING_ROOMS, output);
    }

    public static void map() {
        VenueManager manager = VenueManager.getInstance();
        JSONArray output = new JSONArray();
        for (MapVenue venue : manager.getVenues()) {
            JSONObject venueObject = new JSONObject();
            venueObject.put("name", venue.getName());
            venueObject.put("image", venue.getImagePath());
            output.put(venueObject);
        }
        JSONUtils.writeJSON(DataPaths.JSON_MAP, output);
    }

    //Arrayifiers

    private static JSONArray arrayifyDates(DateManager manager) {
        JSONArray dates = new JSONArray();
        for (CalendarDate date : manager) {
            dates.put(date.getName());
        }
        return dates;
    }

    private static JSONArray arrayifyTime(ArEvent event) {
        JSONArray timeArray = new JSONArray();
        for (CalendarEvent time : event.getTimeblocks()) {
            timeArray.put(objectify(time));
        }
        return timeArray;
    }

    private static JSONArray arrayifyGuests(ArEvent event) {
        JSONArray guestArray = new JSONArray();
        for (ArGuest guest : event.getGuests()) {
            guestArray.put(guest.getName());
        }
        return guestArray;
    }

    //Objectifiers

    private static JSONObject objectify(ArCategory cat) {
        JSONObject catObject = new JSONObject();
        catObject.put("category", cat.getTitle());
        JSONArray eventsArray = new JSONArray();
        for (ArEvent event : cat) {
            eventsArray.put(objectify(event));
        }
        catObject.put("events", eventsArray);
        return catObject;
    }

    private static JSONObject objectify(ArEvent event) {
        JSONObject eventObject = new JSONObject();
        eventObject.put("title", event.getTitle());
        eventObject.put("time", arrayifyTime(event));
        eventObject.put("location", event.getLocation());
        eventObject.put("desc", event.getDesc());
        if (event.getRestriction() != 0) {
            eventObject.put("age", event.getRestriction());
        }
        if (event.getGuests().size() != 0) {
            eventObject.put("guests", arrayifyGuests(event));
        }
        eventObject.put("tags", new JSONArray());   //TODO: temp empty tag list
        return eventObject;
    }

    private static JSONObject objectify(CalendarEvent time) {
        JSONObject timeObject = new JSONObject();
        timeObject.put("date", time.getDate().getName());
        timeObject.put("start", time.getStartTime().toString());
        timeObject.put("end", time.getEndTime().toString());
        return timeObject;
    }

    private static JSONObject objectify(ArLocation loc) {
        JSONObject locObject = new JSONObject();
        locObject.put("purpose", loc.getPurpose());
        locObject.put("location", loc.getLocation());
        if (loc.isSchedule()) {
            locObject.put("schedule", true);
        }
        return locObject;
    }

    private static JSONObject objectify(ArGuest guest) {
        JSONObject guestObject = new JSONObject();
        guestObject.put("name", guest.getName());
        guestObject.put("title", guest.getTitle());
        if (guest.getJapanese() != null && guest.getJapanese().length() != 0) {
            guestObject.put("japanese", guest.getJapanese());
        }
        if (guest.getPortraitPath() != null && guest.getPortraitPath().length() != 0) {
            guestObject.put("image", guest.getPortraitPath());
        }
        if (guest.getAutograph().size() != 0) {
            guestObject.put("autographs", arrayifyLocCalEvents(guest.getAutograph()));
        }
        if (guest.getPhotobooth().size() != 0) {
            guestObject.put("photobooth", arrayifyLocCalEvents(guest.getPhotobooth()));
        }
        return guestObject;
    }

    private static JSONArray arrayifyLocCalEvents(ObservableList<LocatedCalendarEvent> events) {
        JSONArray output = new JSONArray();
        for (LocatedCalendarEvent event : events) {
            JSONObject eventObject = new JSONObject();
            eventObject.put("date", event.getDate().getName());
            eventObject.put("location", event.getLocation().getPurpose());
            eventObject.put("start", event.getStartTime().shortString());
            eventObject.put("end", event.getEndTime().shortString());
            output.put(eventObject);
        }
        return output;
    }

}
