/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.physicallibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.ui.callback.BookReturnCallback;
import library.util.LibraryUtil;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class PhysicalLibraryController implements Initializable, BookReturnCallback {

    private static final String BOOK_NOT_AVAILABLE = "Not Available";
    private static final String NO_SUCH_BOOK_AVAILABLE = "No Such Book Available";
    private static final String NO_SUCH_MEMBER_AVAILABLE = "No Such Member Available";
    private static final String BOOK_AVAILABLE = "Available";

    private Boolean isReadyToSubmit = false;
    
    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField bookID;
    @FXML
    private Text bookName;
    @FXML
    private Text authorName;
    @FXML
    private Text publisherName;
    @FXML
    private Text availability;
    @FXML
    private TextField memberID;
    @FXML
    private Text memberName;
    @FXML
    private Text address;
    @FXML
    private Text email;

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField bookID1;
    @FXML
    private ListView<String> issueDataList;
    @FXML
    private StackPane rootPane;

    Socket connection = null;
    BufferedReader inFromServer = null;
    PrintStream outToServer = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        connection = library.main.MainLoader.getConnection();
        try {
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            outToServer = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/library/ui/addmembers/AddMember.fxml", "Add New Member");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadWindow("/library/ui/addbooks/AddBook.fxml", "Add New Book");
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/library/ui/memberlist/MemberList.fxml", "All Member Information");
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        loadWindow("/library/ui/booklist/BookList.fxml", "All Book Information");
    }

    @FXML
    private void loadSettings(ActionEvent event) {
        loadWindow("/library/ui/settings/setting.fxml", "Settings");
    }

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void clearBookCache() {
        bookName.setText("");
        authorName.setText("");
        publisherName.setText("");
        availability.setText("");
    }

    void clearMemberCache() {
        memberName.setText("");
        address.setText("");
        email.setText("");
    }

    @FXML
    private void loadBookInformation(ActionEvent event) {
        clearBookCache();

        outToServer.print("searchBook" + "\n");
        outToServer.flush();

        String bid = bookID.getText();
        outToServer.print(bid + "\n");
        outToServer.flush();
        Boolean flag = false;
        try {
            while (true) {
                String msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }

                String bName = inFromServer.readLine();
                String bauthor = inFromServer.readLine();
                String bpublisher = inFromServer.readLine();
                boolean bavail = Boolean.parseBoolean(inFromServer.readLine());

                bookName.setText(bName);
                authorName.setText(bauthor);
                publisherName.setText(bpublisher);
                String status = (bavail) ? "Available" : "Not Availalble";
                availability.setText(status);
                flag = true;

            }
            if (!flag) {
                bookName.setText("No such book available");

            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadMemberInformation(ActionEvent event) {
        clearMemberCache();

        outToServer.print("searchMember" + "\n");
        outToServer.flush();

        String mid = memberID.getText();

        outToServer.print(mid + "\n");
        outToServer.flush();

        Boolean flag = false;
        try {
            while (true) {
                String msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }
                String mName = inFromServer.readLine();
                String mAddress = inFromServer.readLine();
                String mEmail = inFromServer.readLine();

                memberName.setText(mName);
                address.setText(mAddress);
                email.setText(mEmail);

                flag = true;

            }
            if (!flag) {
                memberName.setText("No such member available");

            } else {
            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadIssueBook(ActionEvent event) {
        String bid = bookID.getText();
        String mid = memberID.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Book Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to issue the book " + bookName.getText() + "\nTo " + memberName.getText() + "?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {

            try {
                outToServer.print("issueBook" + "\n");
                outToServer.flush();
                outToServer.print(bid + "\n");
                outToServer.flush();
                outToServer.print(mid + "\n");
                outToServer.flush();

                String msg = inFromServer.readLine();

                if (msg.equals("success")) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book Issue Complete");
                    alert1.showAndWait();
                } else {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Failed");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Issue Operation Failed");
                    alert2.showAndWait();
                }
            } catch (IOException ex) {
                Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Cancelled");
            alert3.setHeaderText(null);
            alert3.setContentText("Issue Operation Cancelled");
            alert3.showAndWait();

        }
    }
    
    @FXML
    private void loadBook2(ActionEvent event) {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyToSubmit = false;
        String id = bookID1.getText();

        outToServer.print("renew" + "\n");
        outToServer.flush();
        outToServer.print(id + "\n");
        outToServer.flush();

        try {

            String msg = inFromServer.readLine();
            if (msg.equals("success")) {

                String mBookID = inFromServer.readLine();
                String mMemberID = inFromServer.readLine();
                String issueTime = inFromServer.readLine();

                Timestamp mIssueTime = Timestamp.valueOf(issueTime);

                int mrenewCount = Integer.parseInt(inFromServer.readLine());

                issueData.add("Issue Date and Time: " + mIssueTime.toGMTString());
                issueData.add("Renew Count: " + mrenewCount);

                issueData.add("Book Information: ");

                issueData.add("\tBook Name: " + inFromServer.readLine());
                issueData.add("\tBook Author: " + inFromServer.readLine());
                issueData.add("\tBook Publisher: " + inFromServer.readLine());
                issueData.add("\tBook ID: " + inFromServer.readLine());

                issueData.add("Member Information: ");

                issueData.add("\tMember Name: " + inFromServer.readLine());
                issueData.add("\tMember Address: " + inFromServer.readLine());
                issueData.add("\tMember Email: " + inFromServer.readLine());

                isReadyToSubmit = true;
                
                String x = inFromServer.readLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    private void loadSubmit(ActionEvent event) {
        if (!isReadyToSubmit) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("Failed");
            alert2.setHeaderText(null);
            alert2.setContentText("Please Select a Valid Book ID to Submit");
            alert2.showAndWait();
            return;
        }

        outToServer.print("submit" + "\n");
        outToServer.flush();

        String id = bookID1.getText();

        outToServer.print(id + "\n");
        outToServer.flush();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submit Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to return the book?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {

            try {
                outToServer.print("executeQuery" + "\n");
                outToServer.flush();

                String returnMsg = inFromServer.readLine();
                System.out.println(returnMsg);
                if (returnMsg.equals("success")) {
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Success");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Book has been submitted successfully");
                    alert2.showAndWait();
                } else {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Failed");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Submission has been failed");
                    alert2.showAndWait();
                }
            } catch (IOException ex) {
                Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Cancelled");
            alert3.setHeaderText(null);
            alert3.setContentText("Submit Operation Cancelled");
            alert3.showAndWait();
        }
    }

    
    @FXML
    private void loadRenew(ActionEvent event) {
        
        String id = bookID1.getText();
        System.out.println(id);
        
        if (!isReadyToSubmit) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("Failed");
            alert2.setHeaderText(null);
            alert2.setContentText("Please Select a Valid Book ID to Renew.");
            alert2.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to renew the book?");
        
        
        
        outToServer.print("renewcount" + "\n");
        outToServer.flush();
                
        
        
        
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            
            try {
                outToServer.print("confirm" + "\n");
                outToServer.flush();
                outToServer.print(id + "\n");
                outToServer.flush();
                
                String msg = inFromServer.readLine();
                
                if (msg.equals("success")) {
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Success");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Book has been renewed successfully");
                    alert2.showAndWait();
                } else {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Failed");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Renew has been failed");
                    alert2.showAndWait();
                }
            } catch (IOException ex) {
                Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Cancelled");
            alert3.setHeaderText(null);
            alert3.setContentText("Renew Operation Cancelled");
            alert3.showAndWait();
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void addNewMember(ActionEvent event) {
        loadWindow("/library/ui/addmembers/AddMember.fxml", "Add New Member");
    }

    @FXML
    private void addNewBook(ActionEvent event) {
        loadWindow("/library/ui/addbooks/AddBook.fxml", "Add New Book");
    }

    @FXML
    private void loadViewMembers(ActionEvent event) {
        loadWindow("/library/ui/memberlist/MemberList.fxml", "All Member Information");
    }

    @FXML
    private void loadViewBooks(ActionEvent event) {
        loadWindow("/library/ui/booklist/BookList.fxml", "All Book Information");
    }

    @FXML
    private void loadFullScreen(ActionEvent event) {
        Stage stage = ((Stage) rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());

    }

    @FXML
    private void goSettings(ActionEvent event) {
        loadWindow("/library/ui/settings/setting.fxml", "Settings");
    }

    @FXML
    private void loadIssuedBook(ActionEvent event) {
        loadWindow("/library/ui/issuedbooks/IssuedBooks.fxml", "All issued Book Information");
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    private void cancelMembership(ActionEvent event) {
        loadWindow("/library/ui/physicallibrary/cancelMember.fxml", "Cancel Membership");
    }


    @FXML
    private void logOut(ActionEvent event) {
        ((Stage)bookID.getScene().getWindow()).close();
        loadWindow("/library/main/Main.fxml", "Home Page");
    }

    @Override
    public void loadBookReturn(String bookID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @FXML
    private void loadAbout(ActionEvent event) {
        loadWindow("/library/main/About.fxml", "About Us");
    }

}
