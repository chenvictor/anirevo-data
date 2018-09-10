package cvic.anirevo.editor;

public class TabInteractionHandler {

    private ContentController contentController;
    private NavigationController navController;

    void setContentController(Object listener) {
        if (listener instanceof ContentController) {
            contentController = (ContentController) listener;
            contentController.setInteractionHandler(this);
        }
    }

    void setNavController(Object listener) {
        if (listener instanceof NavigationController) {
            navController = (NavigationController) listener;
            navController.setInteractionHandler(this);
        }
    }

    private void updateNav() {
        if (navController != null) {
            navController.update();
        }
    }

    private void updateContent(Object object) {
        if (contentController != null) {
            contentController.itemSelected(object);
        }
    }

    public static abstract class NavigationController {

        TabInteractionHandler handler;

        void setInteractionHandler(TabInteractionHandler handler) {
            this.handler = handler;
        }

        protected void updateContent(Object object) {
            handler.updateContent(object);
        }

        public abstract void update();

    }

    public static abstract class ContentController {

        TabInteractionHandler handler;

        void setInteractionHandler(TabInteractionHandler handler) {
            this.handler = handler;
        }

        public abstract void itemSelected(Object object);

    }

}
