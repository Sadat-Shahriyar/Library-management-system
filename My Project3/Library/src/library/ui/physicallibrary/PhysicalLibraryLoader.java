/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.physicallibrary;

import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.util.LibraryUtil;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class PhysicalLibraryLoader extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/library/ui/login/Login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Library Login");
        
             
        LibraryUtil.setStageIcon(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

