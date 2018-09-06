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
            JSONObject catObject = objectify(cat);
            output.put(catObject);
        }
        JSONUtils.writeJSON(DataPaths.JSON_EVENTS, output);
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

}
