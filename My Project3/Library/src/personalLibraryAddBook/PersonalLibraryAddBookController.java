package personalLibraryAddBook;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PersonalLibraryAddBookController implements Initializable {

    @FXML
    private TextField bookNameField;
    @FXML
    private TextField autherNameField;
    @FXML
    private Button addBtn;
    @FXML
    private Label haveBookLabel;
    
    String bookName,autherName,userName;
    Socket connection=null;
    BufferedReader inFromServer= null;
    PrintStream outToServer=null;    
    

    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            connection=library.main.MainLoader.getConnection();
            inFromServer=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer=new PrintStream(connection.getOutputStream());
            haveBookLabel.setText(" ");
        } catch (IOException ex) {
            Logger.getLogger(PersonalLibraryAddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    @FXML
    private void add(ActionEvent event) {
        
        try {
            outToServer.print("addpersonalbook"+"\n");
            outToServer.flush();
            haveBookLabel.setText(" ");
            boolean bookExist = false;
            bookName = bookNameField.getText();
            autherName = autherNameField.getText();
            
            outToServer.print(bookName + "\n");
            outToServer.flush();
            
            outToServer.print(autherName + "\n");
            outToServer.flush();
            
            String msg = inFromServer.readLine();
            
            if(msg.equals("notexist")){
                String msg1 = inFromServer.readLine();
                if(msg1.equals("added")){
                    //alert
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully filled the informations of a new book.");
                    alert.showAndWait();
                }
                else {
                    //alert
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Couldn't fill the informations of a new book.");
                    alert.showAndWait();
                }
            }
            else if(msg.equals("exist")){
                haveBookLabel.setText("You already have this book in library");
            }
        } catch (IOException ex) {
            Logger.getLogger(PersonalLibraryAddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
