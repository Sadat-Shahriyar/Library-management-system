package librarymanagement3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Librarymanagement3 extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        this.stage=stage;
        stage.show();
    }
    public Stage getStage(Stage stage){
        return stage;
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
