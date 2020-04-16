package PersonalLibraryDeleteBook;

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

public class DeleteBookController implements Initializable {

    @FXML
    private TextField bookNameField;
    @FXML
    private Button deleteBtn;

    String userName;
    @FXML
    private Label bookExistLabel;
    @FXML
    private TextField autherNameField;

    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            
            connection = library.main.MainLoader.getConnection();
            outToServer = new PrintStream(connection.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            outToServer.print("deletePersonalLibrary" + "\n");
            outToServer.flush();

        } catch (IOException ex) {
            Logger.getLogger(DeleteBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void delete(ActionEvent event) {
        try {
            String username = null;
            String bookName = bookNameField.getText();
            String autherName = autherNameField.getText();

            outToServer.print(bookName + "\n");
            outToServer.flush();
            outToServer.print(autherName + "\n");
            outToServer.flush();


            String msg = inFromServer.readLine();

            if (msg.equals("exist")) {

                outToServer.print("delete" + "\n");
                outToServer.flush();

                String msg1 = inFromServer.readLine();
                if (msg1.equals("success")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully deleted the book.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Couldn't delete the book.");
                    alert.showAndWait();
                }
                
                String x= inFromServer.readLine();
                PersonalLibrary.PersonalLibraryController.stage.close();
                PersonalLibrary.PersonalLibraryController.stage = null;
            } else {
                bookExistLabel.setText("This book doesn't exist");
            }
        } catch (IOException ex) {
            Logger.getLogger(DeleteBookController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
