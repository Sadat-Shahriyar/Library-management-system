package viewbook;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewBookController implements Initializable {

    @FXML
    private TableColumn<tableElement, Integer> IDColumn;
    @FXML
    private TableColumn<tableElement, String> bookTitleColumn;
    @FXML
    private TableColumn<tableElement, String> autherNameColumn;
    @FXML
    private TableColumn<tableElement, String> publisherNameColumn;
    @FXML
    private Button backBtn;
    
    
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;
    
    @FXML
    private TableView<tableElement> table;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
            
            outToServer.print("onlineTable" + "\n");
            outToServer.flush();
            
            
            IDColumn.setCellValueFactory(new PropertyValueFactory("id"));
            bookTitleColumn.setCellValueFactory(new PropertyValueFactory("bookName"));
            autherNameColumn.setCellValueFactory(new PropertyValueFactory("autherName"));
            publisherNameColumn.setCellValueFactory(new PropertyValueFactory("publisherName"));
            
            setTable();
        } catch (IOException ex) {
            Logger.getLogger(ViewBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void back(ActionEvent event) {
        librarymanagement3.FXMLDocumentController.stage.close();
        librarymanagement3.FXMLDocumentController.stage = null;
        
    }
    
    
    
    private void setTable()
    {
        int id;
        String bookname;
        String authername;
        String publishername;
      
        ObservableList<tableElement> list = FXCollections.observableArrayList();
        try {
            
            
            while(true)
            {
                String msg = inFromServer.readLine();
                if(msg.equals("false")) break;
                id = Integer.parseInt(inFromServer.readLine());
                bookname = inFromServer.readLine();
                authername= inFromServer.readLine();
                publishername =inFromServer.readLine();
                System.out.println(id);
                list.add(getTableElement(id,bookname,authername,publishername));
            }
            table.setItems(list);
            
                
        } catch (IOException ex) {
            Logger.getLogger(ViewBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    private tableElement getTableElement(int id, String bookName, String autherName, String publisherName){
        tableElement obj = new tableElement(id,bookName,autherName,publisherName);
        return obj;
    }
    
}
