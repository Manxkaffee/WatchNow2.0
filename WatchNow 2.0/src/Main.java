import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Manxkaffee on 15.11.2017.
 */
public class Main extends Application {
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        primaryStage.setTitle("WatchNow 2.0");
        primaryStage.setScene(new Scene(root, 600,400));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[]args){
        launch(args);
    }
}
