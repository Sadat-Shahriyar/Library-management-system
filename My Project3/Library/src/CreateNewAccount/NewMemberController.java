/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CreateNewAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class NewMemberController implements Initializable {

    Socket connection;
    @FXML
    private TextField name;
    @FXML
    private TextField password;
    @FXML
    private TextField address;
    @FXML
    private TextField mobile;
    @FXML
    private TextField email;
    @FXML
    private TextField id;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane root;
    /**
     * Initializes the controller class.
     */
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.connection = library.main.MainLoader.getConnection();
    }

    @FXML
    private void saveMember(ActionEvent event) {
        String mName = name.getText();
        String mPassword = password.getText();
        String mAddress = address.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        String mID = id.getText();

        Boolean flag = mName.isEmpty() || mPassword.isEmpty() || mAddress.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty() || mID.isEmpty();
        
        if (flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the informations carefully");
            alert.showAndWait();
            return;
        }
        
        try {

            BufferedReader dis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintStream dos = new PrintStream(connection.getOutputStream());
            dos.print("register" + "\n");
            dos.flush();
            dos.print(mName + "\n");
            dos.flush();
            dos.print(mPassword + "\n");
            dos.flush();
            dos.print(mAddress + "\n");
            dos.flush();
            dos.print(mMobile + "\n");
            dos.flush();
            dos.print(mEmail + "\n");
            dos.flush();
            dos.print(mID + "\n");
            dos.flush();

            String msg = dis.readLine();
            System.out.println(msg);
            if(msg.equals("repeatation")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Member with same Username already exists.\nPlease use new username.");
                alert.showAndWait();
            }else if (msg.equals("success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Successfully filled the informations of a new member.");
                alert.showAndWait();
                clearEntries();
            } else if (msg.equals("fail")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Unsuccessfully in filling the informations.");
                alert.showAndWait();
                
            }

        } catch (IOException ex) {
            Logger.getLogger(NewMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage mstage = (Stage) root.getScene().getWindow();
        mstage.close();
    }

 private void clearEntries() {
        name.clear();
        password.clear();
        address.clear();
        mobile.clear();
        email.clear();
        id.clear();

 }
}
