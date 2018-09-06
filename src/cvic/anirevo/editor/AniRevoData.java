package cvic.anirevo.editor;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AniRevoData extends Application {

    private Controller mController;

    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {

    };

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        mController = loader.getController();
        primaryStage.setTitle("AniRevo");

        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
