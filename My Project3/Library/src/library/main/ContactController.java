/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class ContactController implements Initializable {

    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label mobile;
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    @FXML
    private Label name;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());

            LoadInfo();

        } catch (IOException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void LoadInfo() {
        try {
            String msg = inFromServer.readLine();
            if (msg.equals("true")) {
                String name1 = inFromServer.readLine();
                String address1 = inFromServer.readLine();
                String mobile1 = inFromServer.readLine();
                String email1 = inFromServer.readLine();
                address.setText(address1);
                mobile.setText(mobile1);
                email.setText(email1);
                name.setText(name1);
            }

        } catch (IOException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
