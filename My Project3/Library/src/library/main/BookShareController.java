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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class BookShareController implements Initializable {

    @FXML
    private TableView<bookSharers> shareTable;
    @FXML
    private TableColumn<bookSharers, String> nameCol;
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    ObservableList<bookSharers> list;
    @FXML
    private Button homeBtn;
    @FXML
    private Button libraryBtn;
    @FXML
    private Button onlineLibraryBtn;
    @FXML
    private Button myhomelibrarybtn;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());

            outToServer.print("showSharer" + "\n");
            outToServer.flush();
            loadtable();
        } catch (IOException ex) {
            Logger.getLogger(BookShareController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadtable() {
        list = FXCollections.observableArrayList();
        String name, username;
        while (true) {
            try {
                String msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }
                name = inFromServer.readLine();
                username = inFromServer.readLine();
                list.add(new bookSharers(name, username));
            } catch (IOException ex) {
                Logger.getLogger(BookShareController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        shareTable.setItems(list);

    }

    @FXML
    private void viewBooks(ActionEvent event) {
        bookSharers user = shareTable.getSelectionModel().getSelectedItem();
        outToServer.print("showSharedBooks" + "\n");
        outToServer.flush();

        outToServer.print(user.getUsername() + "\n");
        outToServer.flush();
        loadWindow("/library/main/Books.fxml", "Books");

    }

    @FXML
    private void contact(ActionEvent event) {
        bookSharers user = shareTable.getSelectionModel().getSelectedItem();
        outToServer.print("showContact" + "\n");
        outToServer.flush();

        outToServer.print(user.getUsername() + "\n");
        outToServer.flush();
        loadWindow("/library/main/contact.fxml", "Contact Information");
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

    @FXML
    private void homePage(ActionEvent event) {
        ((Stage)shareTable.getScene().getWindow()).close();
        loadWindow("/library/main/Main.fxml", "Home Page");
    }

    @FXML
    private void library(ActionEvent event) {
        loadWindow("/library/ui/login/Login.fxml", "Library Login");
    }

    @FXML
    private void onlineLibrary(ActionEvent event) {
        ((Stage)shareTable.getScene().getWindow()).close();
        loadWindow("/librarymanagement3/FXMLDocument.fxml", "Online Library");
    }

    @FXML
    private void myLibraryPage(ActionEvent event) {
        ((Stage)shareTable.getScene().getWindow()).close();
        loadWindow("/PersonalLibrary/personalLibrary.fxml", "My Library");
    }
}
