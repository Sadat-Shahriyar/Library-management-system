/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.addbooks;

import CreateNewAccount.NewMemberController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.alertMaker.AlertMaker;
import library.ui.booklist.BookListController;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class AddBookController implements Initializable {

    @FXML
    private TextField title;
    @FXML
    private TextField author;
    @FXML
    private TextField publisher;
    @FXML
    private TextField id;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane rootPane;
    /**
     * Initializes the controller class.
     */
    Socket connection = null;
    private Boolean isInEditMode = Boolean.FALSE;
    BufferedReader dis=null;
    PrintStream dos=null;        
    
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.connection = library.main.MainLoader.getConnection();
            dis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            dos = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addBook(ActionEvent event) {
        String bookName = title.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();
        String bookID = id.getText();

        if (bookName.isEmpty() || bookAuthor.isEmpty() || bookPublisher.isEmpty() || bookID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the informations carefully");
            alert.showAndWait();
            return;
            

        }
        if (isInEditMode) {
            handleEditOperation();
            return;
        }
        try {
            
            dos.print("add" + "\n");
            dos.flush();
            dos.print(bookName + "\n");
            dos.flush();
            dos.print(bookAuthor + "\n");
            dos.flush();
            dos.print(bookPublisher + "\n");
            dos.flush();
            dos.print(bookID + "\n");
            dos.flush();

            String msg = dis.readLine();
            if (msg.equals("exist")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Book with same ID already exist");
                alert.showAndWait();  
            } else if (msg.equals("success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Successfully filled the informations of a new book.");
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

    private void clearEntries() {
        title.clear();
        author.clear();
        publisher.clear();
        id.clear();

    }
    public void inflateUI(BookListController.Book book) {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setText(book.getId());
        
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }
    
    private void handleEditOperation() {
        try {
            BookListController.Book book1 = new BookListController.Book(title.getText(),author.getText(), publisher.getText(), id.getText(), true);
            
            dos.print("editBook" + "\n");
            dos.flush();
            dos.print(book1.getTitle()+"\n");
            dos.flush();
            dos.print(book1.getAuthor()+"\n");
            dos.flush();
            dos.print(book1.getPublisher()+"\n");
            dos.flush();
            dos.print(book1.getId()+"\n");
            dos.flush();
            dos.print(String.valueOf(book1.getAvailabilty())+"\n");
            dos.flush();
            
            String msg = dis.readLine();
            if(msg.equals("notavailable")){
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Failed");
                    alert2.setHeaderText(null);
                    alert2.setContentText("This book is already issued.\nSo, You Can't edit now.");
                    alert2.showAndWait();
            }
            else if (msg.equals("success")) {
                AlertMaker.showSimpleAlert("Success", "Book Updated");
            } else {
                AlertMaker.showErrorMessage("Failed", "Can't Update Book");
            }
        } catch (IOException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  

}