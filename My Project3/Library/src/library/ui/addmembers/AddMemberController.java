/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.addmembers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.alertMaker.AlertMaker;
import library.ui.memberlist.MemberListController;
import library.ui.memberlist.MemberListController.Member;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class AddMemberController implements Initializable {

    Socket connection = null;
    @FXML
    private TextField name;
    @FXML
    private TextField category;
    @FXML
    private TextField address;
    @FXML
    private TextField mobile;
    @FXML
    private TextField email;
    @FXML
    private TextField id;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane root;
    /**
     * Initializes the controller class.
     */
    private Boolean isInEditMode = Boolean.FALSE;
    BufferedReader dis = null;
    PrintStream dos = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            connection = library.main.MainLoader.getConnection();
            dis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            dos = new PrintStream(connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mCategory = category.getText();
        String mAddress = address.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        String mID = id.getText();
        Boolean flag = mName.isEmpty() || mCategory.isEmpty() || mAddress.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty() || mID.isEmpty();
        
        if (flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the informations carefully");
            alert.showAndWait();
            return;
        }
        if (isInEditMode) {
            handleUpdateMember();
            return;
        }
        try {

            dos.print("addMember" + "\n");
            dos.flush();
            dos.print(mName + "\n");
            dos.flush();
            dos.print(mCategory + "\n");
            dos.flush();
            dos.print(mAddress + "\n");
            dos.flush();
            dos.print(mMobile + "\n");
            dos.flush();
            dos.print(mEmail + "\n");
            dos.flush();
            dos.print(mID + "\n");
            dos.flush();
            String msg = dis.readLine();
            if (msg.equals("exist")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Member with same ID already exist");
                alert.showAndWait();
            } else if (msg.equals("success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Successfully filled the informations of a new Member.");
                alert.showAndWait();
                clearEntries();
            } else if (msg.equals("fail")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Unsuccessfully in filling the informations.");
                alert.showAndWait();

            }
        } catch (IOException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage mstage = (Stage) root.getScene().getWindow();
        mstage.close();
    }

    public void infalteUI(MemberListController.Member member) {
        name.setText(member.getName());
        category.setText(member.getCategory());
        address.setText(member.getAddress());
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        id.setText(member.getId());
        id.setEditable(false);

        isInEditMode = Boolean.TRUE;
    }

    private void clearEntries() {
        name.clear();
        category.clear();
        address.clear();
        mobile.clear();
        email.clear();
        id.clear();

    }

    private void handleUpdateMember() {
        try {
            Member member = new MemberListController.Member(name.getText(), category.getText(), address.getText(), mobile.getText(), email.getText(), id.getText());

            dos.print("editMember" + "\n");
            dos.flush();
            dos.print(member.getName() + "\n");
            dos.flush();
            dos.print(member.getCategory() + "\n");
            dos.flush();
            dos.print(member.getAddress() + "\n");
            dos.flush();
            dos.print(member.getMobile() + "\n");
            dos.flush();
            dos.print(member.getEmail() + "\n");
            dos.flush();
            dos.print(member.getId() + "\n");
            dos.flush();
            String msg = dis.readLine();
            if (msg.equals("hasBook")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("This member has borrowed book.\nSo can't be edited.");
                alert.showAndWait();
            }
            else if (msg.equals("success")) {
                AlertMaker.showSimpleAlert("Success", "Member Updated Successfully");
            } else {
                AlertMaker.showErrorMessage("Failed", "Can't Update Member");
            }
        } catch (IOException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
