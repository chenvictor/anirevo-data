package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryManager implements Iterable<ArCategory>{

    private static CategoryManager instance;

    private List<ArCategory> categories;

    public static CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    private CategoryManager() {
        categories = new ArrayList<>();
    }

    public ArCategory getCategory(String title) {
        for (ArCategory cat : categories) {
            if (cat.getTitle().equals(title)){
                return cat;
            }
        }
        ArCategory newCat = new ArCategory(title);
        categories.add(newCat);
        return newCat;
    }

    public void setCategories(List<ArCategory> categories) {
        this.categories = categories;
    }

    public void addCategory(int idx, ArCategory cat) {
        categories.add(idx, cat);
    }

    public void clear() {
        categories.clear();
    }

    @Override
    public Iterator<ArCategory> iterator() {
        return categories.iterator();
    }

}
