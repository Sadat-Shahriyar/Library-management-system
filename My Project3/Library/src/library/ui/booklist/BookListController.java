/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.booklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.alertMaker.AlertMaker;
import library.ui.addbooks.AddBookController;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class BookListController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();
    @FXML
    private StackPane rootPane;
    @FXML
    private TableView<Book> listTable;
    @FXML
    private TableColumn<Book, String> bookCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;
    @FXML
    private TableColumn<?, ?> viewCol;
    @FXML
    private TableColumn<?, ?> serial;
    @FXML
    private AnchorPane contentPane;

    Socket connection = null;
    PrintStream outToServer = null;
    BufferedReader inFromServer = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initCol();

            connection = library.main.MainLoader.getConnection();
            outToServer = new PrintStream(connection.getOutputStream());
            outToServer.print("viewlibrarybooks" + "\n");
            outToServer.flush();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            loadData();
        } catch (IOException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Stage getStage() {
        return (Stage) listTable.getScene().getWindow();
    }

    private void initCol() {
        //serial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        bookCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availabilty"));
    }

    private void loadData() {
        list.clear();
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

                list.add(new Book(bookname, author, publisher, id, available));

            } catch (IOException ex) {
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listTable.setItems(list);

    }

    @FXML
    private void deleteBook(ActionEvent event) {
        try {
            outToServer.print("deleteLibraryBook" + "\n");
            outToServer.flush();
            
            //Fetch the selected row
            Book selectedForDeletion = listTable.getSelectionModel().getSelectedItem();
            
            if (selectedForDeletion == null) {
                AlertMaker.showErrorMessage("No book selected", "Please select a book for delete.");
                return;
            }
            
            outToServer.print(selectedForDeletion.getTitle()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getAuthor()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getPublisher()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getId()+ "\n");
            outToServer.flush();
            outToServer.print(String.valueOf(selectedForDeletion.getAvailabilty())+ "\n");
            outToServer.flush();
            
            
            
            String msg = inFromServer.readLine();
            if (msg.equals("alreadyIssued")) {
                AlertMaker.showErrorMessage("Can't be deleted", "This book is already issued and can't be deleted.");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting Book");
            alert.setContentText("Are you sure to delete the book " + selectedForDeletion.getTitle() + " ?");
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                outToServer.print("delete" + "\n");
                outToServer.flush();
                
                msg = inFromServer.readLine();
                
                if (msg.equals("deleted")) {
                    AlertMaker.showSimpleAlert("Book deleted", selectedForDeletion.getTitle() + " is deleted successfully.");
                    list.remove(selectedForDeletion);
                } else {
                    AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getTitle() + " could not be deleted");
                }
            } else {
                AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
            }
        } catch (IOException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void editBook(ActionEvent event) {
        //Fetch the selected row
        

        Book selectedForEdit = listTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/ui/addbooks/AddBook.fxml"));
            Parent parent = loader.load();

            AddBookController controller = (AddBookController) loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            
            stage.setOnCloseRequest((e) -> {
                refreshTable(new ActionEvent());

            });

        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void refreshTable(ActionEvent event) {
        outToServer.print("viewlibrarybooks" + "\n");
        outToServer.flush();
        loadData();
    }

    @FXML
    private void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"   Title   ", "  Author  ", "  Publisher ", "ID", "Avail"};
        printData.add(Arrays.asList(headers));
        for (Book book : list) {
            List<String> row = new ArrayList<>();
            row.add(book.getTitle());
            row.add(book.getAuthor());
            row.add(book.getPublisher());
            row.add(book.getId());
            row.add(book.getAvailabilty());
            printData.add(row);
        }
        LibraryUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    private void closeStage(ActionEvent event) {
        getStage().close();
    }

    public static class Book {

        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty id;
        private final SimpleStringProperty availabilty;

        public Book(String title, String author, String pub, String id, Boolean avail) {
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
            this.id = new SimpleStringProperty(id);
            if (avail) {
                this.availabilty = new SimpleStringProperty("Available");
            } else {
                this.availabilty = new SimpleStringProperty("Not Available");
            }
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public String getAvailabilty() {
            return availabilty.get();
        }

    }

}
