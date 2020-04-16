/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import library.alertMaker.AlertMaker;
import library.data.Info.MailServerInfo;
import library.ui.mail.TestMailController;
import library.util.LibraryUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class SettingController implements Initializable {

 
 
    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField serverName;
    @FXML
    private JFXTextField smtpPort;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXPasswordField emailPassword;
    @FXML
    private JFXCheckBox sslCheckbox;
    @FXML
    private JFXSpinner progressSpinner;
         
     
     Socket connection = null;
     BufferedReader inFromserver = null;
     PrintStream outToServer = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            
            connection = library.main.MainLoader.getConnection();
            outToServer = new PrintStream(connection.getOutputStream());
            inFromserver = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            initDefaultValues();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SettingController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
    }    

    private void handleCancel(ActionEvent event) {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }

    
    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDays()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        String passHash = String.valueOf(preferences.getPassword());
        password.setText(passHash.substring(0, Math.min(passHash.length(), 10)));
        loadMailServerConfigurations();
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        int ndays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String uname = username.getText();
        String pass = password.getText();

        Preferences preferences = Preferences.getPreferences();
        preferences.setnDays(ndays);
        preferences.setFinePerDay(fine);
        preferences.setUsername(uname);
        preferences.setPassword(pass);

        Preferences.writePreferences(preferences);
        
    }

    @FXML
    private void handleTestMailAction(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailSererInfo();
        if (mailServerInfo != null) {
            TestMailController controller = (TestMailController) LibraryUtil.loadWindow(getClass().getResource("/library/assistant/ui/mail/test_mail.fxml"), "Test Email", null);
            controller.setMailServerInfo(mailServerInfo);
        }

    }

    @FXML
    private void saveMailServerConfuration(ActionEvent event) {
        
        try {
            outToServer.print("saveEmail" + "\n");
            outToServer.flush();
            
            MailServerInfo mailServerInfo = readMailSererInfo();
            
            outToServer.print(mailServerInfo.getMailServer() + "\n");
            outToServer.flush();
            outToServer.print(String.valueOf(mailServerInfo.getPort())+ "\n");
            outToServer.flush();
            outToServer.print(mailServerInfo.getEmailID()+ "\n");
            outToServer.flush();
            outToServer.print(mailServerInfo.getPassword()+ "\n");
            outToServer.flush();
            outToServer.print(String.valueOf(mailServerInfo.getSslEnabled())+ "\n");
            outToServer.flush();
            String msg = inFromserver.readLine();
            if (mailServerInfo != null) {
                if (msg.equals("success")) {                    
                    AlertMaker.showSimpleAlert("Success", "Saved successfully!");
                } else {
                    AlertMaker.showErrorMessage("Failed", "Something went wrong!");
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SettingController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @FXML
        
    private Stage getStage() {
        return ((Stage) nDaysWithoutFine.getScene().getWindow());
    }

     private MailServerInfo readMailSererInfo() {
        try {
            MailServerInfo mailServerInfo
                    = new MailServerInfo(serverName.getText(), Integer.parseInt(smtpPort.getText()), emailAddress.getText(), emailPassword.getText(), sslCheckbox.isSelected());
            if (!mailServerInfo.validate() || !LibraryUtil.validateEmailAddress(emailAddress.getText())) {
                throw new InvalidParameterException();
            }
            return mailServerInfo;
        } catch (Exception exp) {
            AlertMaker.showErrorMessage("Invalid Entries Found", "Correct input and try again");
        }
        return null;
    }
     
      private void loadMailServerConfigurations() {
        
        try {
            outToServer.print("settings" + "\n");
            outToServer.flush();
            String msg1 = inFromserver.readLine();
            if(msg1.equals("ok")){
            String mailServer = inFromserver.readLine();
            int port = Integer.parseInt(inFromserver.readLine());
            String email = inFromserver.readLine();
            String password = inFromserver.readLine();
            boolean ssl = Boolean.parseBoolean(inFromserver.readLine());
            MailServerInfo mailServerInfo = new MailServerInfo(mailServer, port, email, password, ssl);
            
            if (mailServerInfo != null) {
                serverName.setText(mailServerInfo.getMailServer());
                smtpPort.setText(String.valueOf(mailServerInfo.getPort()));
                emailAddress.setText(mailServerInfo.getEmailID());
                emailPassword.setText(mailServerInfo.getPassword());
                sslCheckbox.setSelected(mailServerInfo.getSslEnabled());
            }
            }
            else{
                
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SettingController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
}
