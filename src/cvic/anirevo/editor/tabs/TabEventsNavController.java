package cvic.anirevo.editor.tabs;

import cvic.anirevo.Log;
import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.editor.DragCell;
import cvic.anirevo.editor.DragPane;
import cvic.anirevo.model.anirevo.*;
import cvic.anirevo.model.calendar.CalendarEvent;
import cvic.anirevo.parser.EventParser;
import cvic.anirevo.utils.JSONUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.Optional;

public class TabEventsNavController implements SubController, ArEvent.TitleChangeListener {

    private EventsNavListener mListener;

    @FXML
    private Accordion navAccordion;

    public void setListener(EventsNavListener listener) {
        mListener = listener;
        navAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            CategoryPane newPane = (CategoryPane) navAccordion.getExpandedPane();
            if (newPane == null) {
                return;
            }
            ArEvent event = newPane.getListView().getSelectionModel().getSelectedItem();
            mListener.itemSelected(event);
            Platform.runLater(() -> newPane.getContent().requestFocus());
        });
        for (TitledPane pane : navAccordion.getPanes()) {
            CategoryPane catPane = (CategoryPane) pane;
            for (ArEvent event : catPane.getCategory()) {
                mListener.itemSelected(event);
                return;
            }
        }
    }

    public void initialize() {
        for (ArCategory cat : CategoryManager.getInstance()) {
            DragPane newPane = makeNewPane(cat);
            navAccordion.getPanes().add(newPane);
        }
        if (navAccordion.getPanes().size() != 0) {
            TitledPane pane = navAccordion.getPanes().get(0);
            navAccordion.setExpandedPane(pane);
            Platform.runLater(() -> pane.getContent().requestFocus());
        }
        navAccordion.setContextMenu(getEmptyCategoryCtxMenu());
        navAccordion.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            if (event.getTarget().equals(navAccordion) && navAccordion.getPanes().size() != 0) {
                event.consume();
            }
        });
    }

    private ContextMenu getEmptyCategoryCtxMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Category");
        menu.getItems().add(add);

        add.setOnAction(event -> {
            addCategory(0);
        });
        return menu;
    }

    private ContextMenu getCategoryCtxMenu(DragPane pane) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem rename = new MenuItem("Rename");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, rename, addBefore, addAfter);

        remove.setOnAction(event -> {
            int idx = pane.getIndex();
            if (pane.equals(navAccordion.getExpandedPane()) || navAccordion.getPanes().size() == 1) {
                //deselect item
                mListener.itemSelected(null);
            }
            navAccordion.getPanes().remove(idx);
        });
        rename.setOnAction(event -> {
            int idx = pane.getIndex();
            String newTitle = categoryTitleInput(pane.getTitle(), idx);
            if (newTitle != null) {
                pane.setTitle(newTitle);
            }
        });
        addBefore.setOnAction(event -> {
            int idx = pane.getIndex();
            addCategory(idx);
        });
        addAfter.setOnAction(event -> {
            int idx = pane.getIndex();
            addCategory(idx + 1);
        });
        return menu;
    }

    private ContextMenu getEmptyEventCtxMenu(ListView<ArEvent> listView) {
        ContextMenu menu = new ContextMenu();
        MenuItem add = new MenuItem("Add Event");
        menu.getItems().add(add);
        add.setOnAction(event -> {
            addEvent(listView, 0);
        });

        return menu;
    }

    private ContextMenu getEventCtxMenu(DragCell<ArEvent> cell) {
        ContextMenu menu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        MenuItem changeCategory = new MenuItem("Change Category");
        MenuItem addBefore = new MenuItem("Add Before");
        MenuItem addAfter = new MenuItem("Add After");
        menu.getItems().addAll(remove, changeCategory, addBefore, addAfter);
        remove.setOnAction(event -> {
            ListView<ArEvent> view = cell.getListView();
            view.getItems().remove(cell.getIndex());
            mListener.itemSelected(view.getSelectionModel().getSelectedItem());
        });
        changeCategory.setOnAction(event -> {
            //TODO
        });
        addBefore.setOnAction(event -> {
            addEvent(cell.getListView(), cell.getIndex());
        });
        addAfter.setOnAction(event -> {
            addEvent(cell.getListView(), cell.getIndex() + 1);
        });

        return menu;
    }

    private void addCategory(int idx) {
        String title = categoryTitleInput("untitled", idx);
        if (title == null) {
            return;
        }
        ArCategory cat = new ArCategory(title);
        CategoryPane newPane = makeNewPane(cat);
        navAccordion.getPanes().add(idx, newPane);
    }

    private void addEvent(ListView<ArEvent> listView, int idx) {
        ArEvent event = new ArEvent("untitled");
        listView.getItems().add(idx, event);
        listView.getSelectionModel().select(idx);
        mListener.itemSelected(event);
        event.setListener(this);
    }

    private String categoryTitleInput(String defaultValue, int idx) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Category");
        dialog.setHeaderText("Enter title string");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (validCategoryName(result.get())) {
                return result.get();
            } else {
                new Alert(Alert.AlertType.ERROR, "Category Title is already taken!").show();
            }
        }
        return null;
    }

    private boolean validCategoryName(String title) {
        for (TitledPane pane : navAccordion.getPanes()) {
            CategoryPane catPane = (CategoryPane) pane;
            ArCategory cat = catPane.getCategory();
            if (cat.getTitle().equals(title)) {
                return false;
            }
        }
        return true;
    }

    private CategoryPane makeNewPane(ArCategory cat) {
        ListView<ArEvent> catList = makeNewList(cat);

        CategoryPane newPane = new CategoryPane(navAccordion, cat);
        newPane.setListView(catList);
        newPane.setTitle(cat.getTitle());
        newPane.setCollapsible(true);
        newPane.prefWidthProperty().bind(navAccordion.prefWidthProperty());
        newPane.setMaxWidth(Control.USE_PREF_SIZE);
        newPane.setContextMenu(getCategoryCtxMenu(newPane));
        return newPane;
    }

    private ListView<ArEvent> makeNewList(ArCategory cat) {
        ListView<ArEvent> catList = new ListView<>();
        catList.setContextMenu(getEmptyEventCtxMenu(catList));
        catList.prefWidthProperty().bind(navAccordion.prefWidthProperty());
        catList.setCellFactory(lv -> {
            DragCell<ArEvent> cell = new DragCell<ArEvent>() {
                @Override
                protected void updateItem(ArEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    //select
                    mListener.itemSelected(cell.getItem());
                    TitledPane pane = (TitledPane) cell.getListView().getParent().getParent();
                    System.out.println(pane.getText());
                }
            });
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (cell.isEmpty()) {
                    event.consume();
                }
            });
            cell.prefWidthProperty().bind(navAccordion.prefWidthProperty().subtract(2));
            cell.setMaxWidth(Control.USE_PREF_SIZE);
            cell.setContextMenu(getEventCtxMenu(cell));
            return cell;
        });
        for (ArEvent event : cat) {
            catList.getItems().add(event);
            event.setListener(this);
        }
        if (catList.getItems().size() != 0) {
            catList.getSelectionModel().select(0);
        }
        return catList;
    }

    @Override
    public void titleChanged(String newTitle) {
        TitledPane pane = navAccordion.getExpandedPane();
        if (pane != null) {
            ((ListView) pane.getContent()).refresh();
        }
    }

    @Override
    public void onSaveBtn() {
        mListener.save();
        JSONArray output = new JSONArray();
        for (TitledPane pane : navAccordion.getPanes()) {
            JSONObject catObject = new JSONObject();
            CategoryPane catPane = (CategoryPane) pane;
            catObject.put("category", catPane.getTitle());
            JSONArray eventsArray = new JSONArray();
            for (ArEvent event : catPane.getListView().getItems()) {
                JSONObject eventObject = new JSONObject();
                eventObject.put("title", event.getTitle());
                JSONArray timeblocks = new JSONArray();
                for (CalendarEvent block : event.getTimeblocks()) {
                    JSONObject time = new JSONObject();
                    time.put("date", block.getDate().getName());
                    time.put("start", block.getStartTime().shortString());
                    time.put("end", block.getEndTime().shortString());
                    timeblocks.put(time);
                }
                eventObject.put("time", timeblocks);
                eventObject.put("location", event.getLocation());
                eventObject.put("desc", event.getDesc());
                eventObject.put("tags", new JSONArray());
                if (event.getRestriction() != 0) {
                    eventObject.put("restriction", event.getRestriction());
                }
                eventsArray.put(eventObject);
            }
            catObject.put("events", eventsArray);
            output.put(catObject);
        }
        JSONUtils.writeJSON(DataPaths.JSON_EVENTS, output);
    }

    @Override
    public void onLoadBtn() {
        CategoryManager.getInstance().clear();
        EventManager.getInstance().clear();
        try {
            EventParser.parseEvents(JSONUtils.getArray(DataPaths.JSON_EVENTS));
            Log.notify("General", "Loaded data from file: " + DataPaths.JSON_EVENTS);
        } catch (FileNotFoundException e) {
            Log.notify("General", "File not found: " + DataPaths.JSON_EVENTS);
            e.printStackTrace();
        }
        initialize();
    }

    public interface EventsNavListener {

        void itemSelected(ArEvent event);

        void save();

    }

}
