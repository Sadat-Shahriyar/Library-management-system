/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.physicallibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class CancelMemberController implements Initializable {

    @FXML
    private HBox member_info;
    @FXML
    private TextField memberID;
    @FXML
    private Text memberName;
    @FXML
    private Text address;
    @FXML
    private Text email;

    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = library.main.MainLoader.getConnection();
        try {
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearMemberCache() {
        memberName.setText("");
        address.setText("");
        email.setText("");
    }


    @FXML
    private void loadMemberInformation(ActionEvent event) {
        clearMemberCache();
        outToServer.print("searchMember" + "\n");
        outToServer.flush();
        String mid = memberID.getText();
        //System.out.println(mid);

        outToServer.print(mid + "\n");
        outToServer.flush();

        Boolean flag = false;
        try {
            while (true) {
                String msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }
                String mName = inFromServer.readLine();
                String mAddress = inFromServer.readLine();
                String mEmail = inFromServer.readLine();

                memberName.setText(mName);
                address.setText(mAddress);
                email.setText(mEmail);

                flag = true;

            }
            if (!flag) {
                memberName.setText("No such member available");

            } else {
                //btnIssue.requestFocus();
            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancelMembership(ActionEvent event) {

        try {
            outToServer.print("cancelMember" + "\n");
            outToServer.flush();
            String mid = memberID.getText();
            outToServer.print(mid + "\n");
            outToServer.flush();
            
            String msg = inFromServer.readLine();
            if (msg.equals("ok")) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Membership has been cancelled");
                alert1.showAndWait();
                clearMemberCache();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Membership was not cancelled");
                alert1.showAndWait();
            }
        } catch (IOException ex) {
            Logger.getLogger(CancelMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
