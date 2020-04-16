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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class BooksController implements Initializable {

    @FXML
    private TableView<Books> bookTable;
    @FXML
    private TableColumn<Books, String> bookCol;
    @FXML
    private TableColumn<Books, String> authorCol;

    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    ObservableList<Books> list;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            bookCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
            authorCol.setCellValueFactory(new PropertyValueFactory<>("authorName"));
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer=new PrintStream(connection.getOutputStream());            
            loadtable();
        } catch (IOException ex) {
            Logger.getLogger(BookShareController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    private void loadtable(){
        
        list = FXCollections.observableArrayList();
        String bookName;
        String authorName;
        while(true) {
            try {
                String msg = inFromServer.readLine();
                if(msg.equals("false")) break;
                bookName = inFromServer.readLine();
                authorName = inFromServer.readLine();
                list.add(new Books(bookName,authorName));
            } catch (IOException ex) {
                Logger.getLogger(BookShareController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        bookTable.setItems(list);
        
    }
    
}
