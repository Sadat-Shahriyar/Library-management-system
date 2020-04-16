package PersonalLibrary;

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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

public class PersonalLibraryController implements Initializable {

    @FXML
    private Button homeBtn;
    @FXML
    private Button libraryBtn;
    @FXML
    private Button publisherPageBtn;
    @FXML
    private Button onlineLibraryBtn;
    @FXML
    private Label nameViewLabel;
    @FXML
    private Label addressViewLabel;
    @FXML
    private Label emailViewLabel;
    @FXML
    private Button addBookBtn;
    @FXML
    private Button viewBookBtn;
    @FXML
    private Button deleteBookBtn;

    String userName;
    String name, address, email;
    public static Stage stage;
    @FXML
    private Button messageBtn;
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
            
                       
            outToServer.print("personalInfo" + "\n");
            outToServer.flush();
            
            name = inFromServer.readLine();
            address = inFromServer.readLine();
            email = inFromServer.readLine();
            nameViewLabel.setText(name);
            addressViewLabel.setText(address);
            emailViewLabel.setText(email);
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(PersonalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

    }

    @FXML
    private void home(ActionEvent event) throws IOException {
        
        ((Stage)nameViewLabel.getScene().getWindow()).close();
        loadWindow("/library/main/Main.fxml", "Home Page");
    }

    @FXML
    private void library(ActionEvent event) throws IOException {
        
        loadWindow("/library/ui/login/Login.fxml", "Library Login");
    }

    @FXML
    private void publisherPage(ActionEvent event) throws IOException {
        
        ((Stage)nameViewLabel.getScene().getWindow()).close();
        loadWindow("/library/main/BookShare.fxml", "Library Login");
    }

    @FXML
    private void onlineLibrary(ActionEvent event) throws IOException {
        
        ((Stage)nameViewLabel.getScene().getWindow()).close();
        loadWindow("/librarymanagement3/FXMLDocument.fxml", "Online Library");
    }

    @FXML
    private void addBook(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/personalLibraryAddBook/personalLibraryAddBook.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        this.stage = stage;
        stage.show();
    }

    @FXML
    private void viewBook(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/personalLibraryViewBooks/ViewBook.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);

        this.stage = stage;

        stage.show();
    }

    @FXML
    private void deleteBook(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/PersonalLibraryDeleteBook/deleteBook.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);

        this.stage = stage;

        stage.show();
    }

    @FXML
    private void message(ActionEvent event) {
    }

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            library.main.MainLoader.stage = library.main.MainLoader.stage1;
            library.main.MainLoader.stage1 = stage;
            stage.show();
            LibraryUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
