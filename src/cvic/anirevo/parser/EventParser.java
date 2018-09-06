package cvic.anirevo.parser;

import cvic.anirevo.Log;
import cvic.anirevo.model.anirevo.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.model.calendar.EventTime;

public class EventParser {

    private static final String TAG = "anirevo.EventParser";

    public static void parseEvents(JSONArray categories){
        Log.i(TAG, categories.length() + " categories(s) to parse");
        for(int i = 0; i < categories.length(); i++) {
            try {
                parseCategory(categories.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "JSONError hit, skipping category");
            }
        }
    }

    private static void parseCategory(JSONObject cat) throws JSONException{
        String categoryTitle = cat.getString("category");
        ArCategory category = CategoryManager.getInstance().getCategory(categoryTitle);
        
        if (cat.has("events")) {
            JSONArray events = cat.getJSONArray("events");

            for (int i = 0; i < events.length(); i++) {
                try {
                    parseEvent(events.getJSONObject(i), category);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "JSONError hit, skipping event.");
                }
            }
        }
    }

    private static void parseEvent(JSONObject event, ArCategory category) throws JSONException {
        String title = event.getString("title");
        ArEvent arEvent = EventManager.getInstance().getEvent(title);


        //Check age restriction first in case this should be skipped
        if (event.has("age")) {
            //Set age restriction
            arEvent.setRestriction(event.getInt("age"));
        }

        String loc = event.getString("location");
        String desc = event.getString("desc");
        JSONArray timeblocks = event.getJSONArray("time");
        JSONArray tags = event.getJSONArray("tags");

        //Set basic properties
        arEvent.setLocation(loc);
        arEvent.setDesc(desc);

        //Establish mutual reference with category
        arEvent.setCategory(category);
        category.addEvent(arEvent);

        //Set CalendarEvents
        DateManager dManager = DateManager.getInstance();
        for (int i = 0; i < timeblocks.length(); i++) {
            JSONObject time = timeblocks.getJSONObject(i);
            CalendarDate date = dManager.getDate(time.getString("date"));
            EventTime start = EventTimeParser.parse(time.getString("start"));
            EventTime end = EventTimeParser.parse(time.getString("end"));
            CalendarEvent calEvent = new CalendarEvent(arEvent);
            calEvent.setDate(date);
            calEvent.setStart(start);
            calEvent.setEnd(end);
            arEvent.addTimeblock(calEvent);
        }

        //Set Tags
        TagManager tManager = TagManager.getInstance();
        for (int i = 0; i < tags.length(); i++) {
            arEvent.addTag(tManager.getTag(tags.getString(i)));
        }
    }

}
