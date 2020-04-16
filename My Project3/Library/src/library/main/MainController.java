/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.main;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.ui.booklist.BookListController;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class MainController implements Initializable {

    @FXML
    private JFXTextField user;
    @FXML
    private JFXPasswordField pass;
    @FXML
    private ListView<?> issueDataList;
    @FXML
    private Button libraryBtn;
    @FXML
    private Button onlineBtn;
    @FXML
    private Button personalBtn;
    @FXML
    private Button publisherBtn;
    Socket connection;
    @FXML
    private TableColumn<BookListController.Book, String> bookNameColumn;
    @FXML
    private TableColumn<BookListController.Book, String> authorNameColumn;
    @FXML
    private TableColumn<BookListController.Book, String> publisherNameColumn;
    @FXML
    private TableColumn<BookListController.Book, String> bookIdColumn;
    @FXML
    private TableColumn<BookListController.Book, Boolean> availableColumn;
    @FXML
    private TableView<BookListController.Book> bookTable;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    ObservableList<BookListController.Book> list = FXCollections.observableArrayList();;
    @FXML
    private Label toplebel;
    @FXML
    private Button log;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         this.connection = MainLoader.getConnection();
         try {
                inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                outToServer = new PrintStream(connection.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        if (!MainLoader.login) {
            libraryBtn.setDisable(true);
            onlineBtn.setDisable(true);
            personalBtn.setDisable(true);
            publisherBtn.setDisable(true);
            log.setDisable(true);
            
        }
        if(MainLoader.login){
            toplebel.setText("Logged In");
        }

        
    }

    private void initCol() {
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorNameColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherNameColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("availabilty"));

    }

    @FXML
    private void loadUsername(ActionEvent event) {
    }


    @FXML
    private void createNewAccount(ActionEvent event) {
        if (!MainLoader.login) {
            loadWindow("/CreateNewAccount/NewMember.fxml", "Create Account");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("You are already Logged In");
            alert.showAndWait();
        }
    }

    @FXML
    private void login(ActionEvent event) throws IOException {
        if (!MainLoader.login) {
            String username = user.getText();
            String password = pass.getText();
 
            BufferedReader dis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintStream dos = new PrintStream(connection.getOutputStream());
            dos.print("login" + "\n");
            dos.flush();
            dos.print(username + "\n");
            dos.flush();
            dos.print(password + "\n");
            dos.flush();
            String str = dis.readLine();
            if (str.equals("okay") && !MainLoader.login) {
                libraryBtn.setDisable(false);
                onlineBtn.setDisable(false);
                personalBtn.setDisable(false);
                publisherBtn.setDisable(false);
                log.setDisable(false);
                System.out.println("Successful!");
                toplebel.setText("Logged In");
                MainLoader.login = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Successfully Logged In.");
                alert.showAndWait();
                
            } else if (str.equals("no")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Wrong Username or Password. Otherwise You Have No Account.");
                alert.showAndWait();
                return;

            }
        }else{
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("You are already logged in.");
                alert.showAndWait();
        }

    }


    @FXML
    private void loadLibrary(ActionEvent event) {
               
        loadWindow("/library/ui/login/Login.fxml", "Library Login");
        
    }

    @FXML
    private void loadOnlineLibrary(ActionEvent event) {
        ((Stage) user.getScene().getWindow()).close();
        loadWindow("/librarymanagement3/FXMLDocument.fxml", "Online Library");
    }

    @FXML
    private void loadHomeLibrary(ActionEvent event) {
        ((Stage) user.getScene().getWindow()).close();
        loadWindow("/PersonalLibrary/personalLibrary.fxml", "Personal Library");
    }

    @FXML
    private void loadPublishers(ActionEvent event) {
        ((Stage) user.getScene().getWindow()).close();
        loadWindow("/library/main/BookShare.fxml", "Personal Book Sharers");

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
    private void vewAllBooks(ActionEvent event) {
        
        list.clear();
        initCol();
        outToServer.print("viewlibrarybooks" + "\n");
        outToServer.flush();

        String bookname = null;
        String author = null;
        String publisher = null;
        String id = null;
        boolean available = false;
        boolean loop = true;
        String msg = null;
        int x = 1;
        while (loop) {
            try {
                msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }
                bookname = inFromServer.readLine();
                author = inFromServer.readLine();
                publisher = inFromServer.readLine();
                id = inFromServer.readLine();
                available = Boolean.parseBoolean(inFromServer.readLine());
                System.out.println(available);
                list.add(new BookListController.Book(bookname, author, publisher, id, available));

            } catch (IOException ex) {
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        bookTable.setItems(list);
    }

    @FXML
    private void loadAbout(ActionEvent event) {
        loadWindow("/library/main/About.fxml", "About Us");
    }

    @FXML
    private void logOut(ActionEvent event) {
        System.exit(0);
    }
    
    

}
