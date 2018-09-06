package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.List;

public class TagManager {

    /**
     * Singleton Manager to keep track of ArTags
     */

    private static TagManager instance;
    private List<ArTag> tags;

    public static TagManager getInstance() {
        if (instance == null) {
            instance = new TagManager();
        }
        return instance;
    }

    private TagManager() {
        tags = new ArrayList<>();
    }

    public ArTag getTag(String name) {
        return null; //stub
    }

    public void clear() {
        tags.clear();
    }
}
