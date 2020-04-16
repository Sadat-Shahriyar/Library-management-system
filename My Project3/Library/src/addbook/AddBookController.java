package addbook;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.stage.FileChooser;

public class AddBookController implements Initializable {

    @FXML
    private TextField bookNameField;
    @FXML
    private TextField autherNameField;
    @FXML
    private TextField publisherNameField;
    @FXML
    private Button chooseFileBtn;
    @FXML
    private Button uploadBookBtn;
    @FXML
    private Button cancelBtn;

    @FXML
    private Label bookExistLabel;

    File file;
    public static int x;
    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File", "*.pdf"));
        file = fc.showOpenDialog(null);
    }

    @FXML
    private void uploadBook(ActionEvent event) {
        try {
            boolean bookExists = false;
            String bookname, authername, publishername;
            bookname = bookNameField.getText();
            authername = autherNameField.getText();
            publishername = publisherNameField.getText();
            if (bookname.isEmpty() || authername.isEmpty() || publishername.isEmpty() || !(file.exists())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Please fill all the informations carefully");
                alert.showAndWait();
                return;
            }
            outToServer.print("onlineAdd" + "\n");
            outToServer.flush();
            outToServer.print(bookname + "\n");
            outToServer.flush();
            outToServer.print(authername + "\n");
            outToServer.flush();
            outToServer.print(publishername + "\n");
            outToServer.flush();

            String msg = inFromServer.readLine();
            if (msg.equals("bookNot")) {
                outToServer.print("query" + "\n");
                outToServer.flush();

                msg = inFromServer.readLine();
                if (msg.equals("queryDone")) {
                    Thread.sleep(500);
                    FileInputStream fis = null;
                    byte contents[];
                    fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    OutputStream os = connection.getOutputStream();

                    long fileLength = file.length();
                    outToServer.print(String.valueOf(fileLength) + '\n');
                    outToServer.flush();
                    long current = 0;

                    while (current != fileLength) {
                        int Size = 10000;
                        if (fileLength - current >= Size) {
                            current += Size;
                        } else {
                            Size = (int) (fileLength - current);
                            current = fileLength;
                        }
                        contents = new byte[Size];
                        bis.read(contents, 0, Size);
                        os.write(contents);
                    }
                    os.flush();
                    fis.close();
                    System.out.println("file sent");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Book Uploaded.");
                    alert.showAndWait();
                }

            } else if (msg.equals("bookYes")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("This book already exists");
                alert.showAndWait();
            }
            librarymanagement3.FXMLDocumentController.stage.close();
            librarymanagement3.FXMLDocumentController.stage = null;
        } catch (IOException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        librarymanagement3.FXMLDocumentController.stage.close();
        librarymanagement3.FXMLDocumentController.stage = null;
    }

    public boolean checkBookExists(String bookName) {
        boolean bookExists = false;
        Statement statement = null;
        String name;
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby:database");
            statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM ONLINELIBRARY2");
            while (res.next()) {
                name = res.getString("BOOK_TITLE");
                if (name.equalsIgnoreCase(bookName)) {
                    bookExists = true;
                    break;

                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("connection exception");
        }
        return bookExists;
    }

}
