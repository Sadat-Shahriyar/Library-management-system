/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.main;

import java.net.Socket;
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
public class MainLoader extends Application{
    static Socket connection;
    static  boolean login = false;
    public static Stage stage;
    public static Stage stage1;
    
    @Override
    public void start(Stage stage) throws Exception {
        connection =new Socket("192.168.0.219",4557);
        
        Parent root = FXMLLoader.load(getClass().getResource("/library/main/Main.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        this.stage = stage;
        stage1 = stage;
        stage.show();
        stage.setTitle("Home Page");
        
             
        LibraryUtil.setStageIcon(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static Socket getConnection(){
        return connection;
    }
    
}
