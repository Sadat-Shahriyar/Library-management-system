package librarymanagement3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

public class FXMLDocumentController implements Initializable {

    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    private Label label;
    @FXML
    private Button homePageBtn;
    @FXML
    private Button libraryBtn;
    @FXML
    private Button publisherPageBtn;
    @FXML
    private Button personalLibraryBtn;
    @FXML
    private TextField searchBookTextfield;
    @FXML
    private Label bookNameLabel;
    @FXML
    private Label autherNameLabel;
    @FXML
    private Button downloadBtn;
    @FXML
    private Label publisherNameLabel;
    @FXML
    private Button addBookBtn;
    @FXML
    private Button viewBookBtn;
    @FXML
    private Button searchBtn;

    public static Stage stage;
    boolean searchField = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            connection = library.main.MainLoader.getConnection();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void homePage(ActionEvent event) throws IOException {

        ((Stage) bookNameLabel.getScene().getWindow()).close();
        loadWindow("/library/main/Main.fxml", "Home Page");
    }

    @FXML
    private void library(ActionEvent event) throws IOException {

        loadWindow("/library/ui/login/Login.fxml", "Library Login");

    }

    @FXML
    private void publisherPage(ActionEvent event) throws IOException {

        ((Stage) bookNameLabel.getScene().getWindow()).close();
        loadWindow("/library/main/BookShare.fxml", "Personal Library");

    }

    @FXML
    private void personalLibrary(ActionEvent event) throws IOException {

        ((Stage) bookNameLabel.getScene().getWindow()).close();
        loadWindow("/PersonalLibrary/personalLibrary.fxml", "My Library");

    }

    @FXML
    private void download(ActionEvent event) {

        if (searchField) {
            try {
                String bookname = searchBookTextfield.getText();
                outToServer.print("download" + "\n");
                outToServer.flush();

                String x = inFromServer.readLine();

                System.out.println(x);

                String bookNameString = bookname + ".pdf";
                String fileSize = inFromServer.readLine();
                System.out.println(fileSize);
                int size = Integer.parseInt(fileSize);
                byte[] contents = new byte[10000];
                FileOutputStream fos = new FileOutputStream(bookNameString);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                InputStream is = connection.getInputStream();

                int bytesRead = 0;
                int total = 0;
                while (total != size) {
                    bytesRead = is.read(contents);
                    total += bytesRead;
                    bos.write(contents, 0, bytesRead);
                }

                bos.flush();
                fos.close();
                System.out.println("file recieved");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Book Downloaded.");
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter Book Name");
            alert.showAndWait();
        }

    }

    @FXML
    private void addBook(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/addbook/AddBook.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        this.stage = stage;
        stage.show();

    }

    @FXML
    private void viewBook(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/viewbook/ViewBook.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        this.stage = stage;
        stage.show();

    }

    @FXML
    private void search(ActionEvent event) {
        String search = searchBookTextfield.getText();

        outToServer.print("searchOnline" + "\n");
        outToServer.flush();

        outToServer.print(search + "\n");
        outToServer.flush();

        int id;
        String bookname;
        String authername;
        String publishername;
        try {

            String msg = inFromServer.readLine();
            if (msg.equals("true")) {
                id = Integer.parseInt(inFromServer.readLine());
                bookname = inFromServer.readLine();
                authername = inFromServer.readLine();
                publishername = inFromServer.readLine();

                bookNameLabel.setText(bookname);
                autherNameLabel.setText(authername);
                publisherNameLabel.setText(publishername);
                searchField = true;

            } else {
                bookNameLabel.setText("Book Not Found");
                autherNameLabel.setText("");
                publisherNameLabel.setText("");
                searchField = false;
            }

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
