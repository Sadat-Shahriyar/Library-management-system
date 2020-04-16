/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.login;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.ui.settings.Preferences;
import library.util.LibraryUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class LoginController implements Initializable {
     private final static Logger LOGGER = LogManager.getLogger(LoginController.class.getName());


    @FXML
    private Label label;
    @FXML
    private javafx.scene.control.TextField username;
    @FXML
    private PasswordField password;

    Preferences preference1;
    public static volatile boolean loggedIn = false;
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        preference1=Preferences.getPreferences();
    }    

    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage)label.getScene().getWindow()).close();
    }
    

    @FXML
    private void handleLogin(ActionEvent event) {
        label.setText("Library Login");
        label.setStyle("-fx-background-color:black;-fx-text-fill:white");
        String name = username.getText();
        String pass = DigestUtils.shaHex(password.getText());
        System.out.println(pass);
        if(name.equals(preference1.getUsername()) && pass.equals(preference1.getPassword())){
            loggedIn = true;
            closeStage();
            loadMain();
             LOGGER.log(Level.INFO, "User successfully logged in {}", name);
        }else{
            label.setText("Invalid Username or Password");
            label.setStyle("-fx-background-color:#d32f2f;-fx-text-fill:white");
        }
    }

    private void closeStage() {
        ((Stage)username.getScene().getWindow()).close();
        
    }
     void loadMain( ) {
        try {
            
            library.main.MainLoader.stage.close();
            Parent parent = FXMLLoader.load(getClass().getResource("/library/ui/physicallibrary/PhysicalLibrary.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);

        }

    }
    
}
