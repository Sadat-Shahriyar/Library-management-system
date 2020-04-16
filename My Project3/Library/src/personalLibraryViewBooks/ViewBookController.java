package personalLibraryViewBooks;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ViewBookController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private TableColumn<tableElements, String> bookTitleColumn;
    @FXML
    private TableColumn<tableElements, String> autherNameColumn;
    @FXML
    private TableView<tableElements> table;

    String userName;
    
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
            autherNameColumn.setCellValueFactory(new PropertyValueFactory<>("autherName"));
            
            connection = library.main.MainLoader.getConnection();
            outToServer = new PrintStream(connection.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            outToServer.print("viewPersonalLibrary" + "\n");
            outToServer.flush();
            
            setTable();
        } catch (IOException ex) {
            Logger.getLogger(ViewBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setTable() {
        String bookName, autherName,username;
        
        ObservableList<tableElements> list = FXCollections.observableArrayList();
        while(true){
            try {
                String msg = inFromServer.readLine();
                if(msg.equals("false")) break;
                
                bookName = inFromServer.readLine();
                autherName = inFromServer.readLine();
                
                list.add(getTableElement(bookName,autherName));
            } catch (IOException ex) {
                Logger.getLogger(ViewBookController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        table.setItems(list);

    }
    
    private tableElements getTableElement(String bookName, String autherName){
        tableElements obj = new tableElements(bookName,autherName);
        return obj;
    }

    @FXML
    private void back(ActionEvent event) {
        PersonalLibrary.PersonalLibraryController.stage.close();
        PersonalLibrary.PersonalLibraryController.stage = null;
    }

}
