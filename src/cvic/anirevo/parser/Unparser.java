package cvic.anirevo.parser;

import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.utils.JSONUtils;
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
        return guestObject;
    }

}
