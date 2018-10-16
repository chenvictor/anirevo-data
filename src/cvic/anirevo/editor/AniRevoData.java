package cvic.anirevo.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AniRevoData extends Application {

    private Controller mController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        mController = loader.getController();
        primaryStage.setTitle("AniRevo");

        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
