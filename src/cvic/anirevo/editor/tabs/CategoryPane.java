package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.DragPane;
import cvic.anirevo.model.anirevo.ArCategory;
import cvic.anirevo.model.anirevo.ArEvent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;

public class CategoryPane extends DragPane {

    private final ArCategory category;
    private ListView<ArEvent> listView;

    public CategoryPane(Accordion parent, ArCategory category) {
        super(parent);
        this.category = category;
    }

    public void setListView(ListView<ArEvent> listView) {
        this.listView = listView;
        setContent(listView);
    }

    public ArCategory getCategory() {
        return category;
    }

    public ListView<ArEvent> getListView() {
        return this.listView;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        category.setTitle(title);
    }
}
