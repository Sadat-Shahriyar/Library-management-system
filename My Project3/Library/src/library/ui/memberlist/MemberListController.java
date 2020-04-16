/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.ui.memberlist;

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
import library.ui.addmembers.AddMemberController;
import library.ui.booklist.BookListController;
import library.ui.physicallibrary.PhysicalLibraryController;
import library.util.LibraryUtil;

/**
 * FXML Controller class
 *
 * @author Taufiqun Nur Farid
 */
public class MemberListController implements Initializable {

    ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    @FXML
    private TableView<Member> listTable;
    @FXML
    private TableColumn<?, ?> serial;
    @FXML
    private TableColumn<Member, String> name;
    @FXML
    private TableColumn<Member, String> category;
    @FXML
    private TableColumn<Member, String> address;
    @FXML
    private TableColumn<Member, String> mobile;
    @FXML
    private TableColumn<Member, String> email;
    @FXML
    private TableColumn<Member, String> id;
    @FXML
    private TableColumn<?, ?> contact;
    @FXML
    private StackPane rootPane;
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
        // TODO
        try {
            initCol();

            connection = library.main.MainLoader.getConnection();
            outToServer = new PrintStream(connection.getOutputStream());
            outToServer.print("viewMembers" + "\n");
            outToServer.flush();
            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            loadData();
        } catch (IOException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage getStage() {
        return (Stage) listTable.getScene().getWindow();
    }

    private void initCol() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        mobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
    }

    private void loadData() {
        list.clear();
        String name = null;
        String address = null;
        String catagory = null;
        String mobile = null;
        String email = null;
        String id = null;
        String msg = null;
        //int x =1;
        while (true) {
            try {
                msg = inFromServer.readLine();
                if (msg.equals("false")) {
                    break;
                }
                name = inFromServer.readLine();
                catagory = inFromServer.readLine();
                address = inFromServer.readLine();
                mobile = inFromServer.readLine();
                email = inFromServer.readLine();
                id = inFromServer.readLine();

                list.add(new Member(name, catagory, address, mobile, email, id));

            } catch (IOException ex) {
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listTable.setItems(list);
    }

    @FXML
    private void refreshMember(ActionEvent event) {
        outToServer.print("viewMembers" + "\n");
        outToServer.flush();
        loadData();
    }

    @FXML
    private void editMember(ActionEvent event) {
        
        //Fetch the selected row
        
        Member selectedForEdit = listTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for edit.");
            return;
        }
        
        
               
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/ui/addmembers/AddMember.fxml"));
            Parent parent = loader.load();

            AddMemberController controller = (AddMemberController) loader.getController();
            controller.infalteUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryUtil.setStageIcon(stage);

            stage.setOnCloseRequest((e) -> {
                refreshMember(new ActionEvent());
            });

            
        } catch (IOException ex) {
            Logger.getLogger(PhysicalLibraryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteMember(ActionEvent event) {
        try {
            //Fetch the selected row
            outToServer.print("deleteMember" + "\n");
            outToServer.flush();
            
            MemberListController.Member selectedForDeletion = listTable.getSelectionModel().getSelectedItem();
            if (selectedForDeletion == null) {
                AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion.");
                return;
            }
            
            outToServer.print(selectedForDeletion.getName() + "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getCategory()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getAddress()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getMobile()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getEmail()+ "\n");
            outToServer.flush();
            outToServer.print(selectedForDeletion.getId()+ "\n");
            outToServer.flush();
            
            String msg=inFromServer.readLine();
            if (msg.equals("alreadyHasBook")) {
                AlertMaker.showErrorMessage("Can't be deleted", "This member has some books.");
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting member");
            alert.setContentText("Are you sure want to delete " + selectedForDeletion.getName() + " ?");
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                outToServer.print("delete"+"\n");
                outToServer.flush();
                msg=inFromServer.readLine();
                if (msg.equals("deleted")) {
                    AlertMaker.showSimpleAlert("Member deleted", selectedForDeletion.getName() + " is deleted successfully.");
                    list.remove(selectedForDeletion);
                } else {
                    AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getName() + " could not be deleted");
                }
            } else {
                AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
            }
        } catch (IOException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"   Name    ", "Category", "Address", "   Mobile   ", "    Email   ", "ID"};
        printData.add(Arrays.asList(headers));
        for (Member member : list) {
            List<String> row = new ArrayList<>();
            row.add(member.getName());
            row.add(member.getCategory());
            row.add(member.getAddress());
            row.add(member.getMobile());
            row.add(member.getEmail());
            row.add(member.getId());
            printData.add(row);
        }
        LibraryUtil.initPDFExprot(rootPane, contentPane, getStage(), printData);
    }

    @FXML
    private void closeStage(ActionEvent event) {
        getStage().close();
    }

    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleStringProperty address;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;
        private final SimpleStringProperty id;

        public Member(String name, String category, String address, String mobile, String email, String id) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.address = new SimpleStringProperty(address);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
            this.id = new SimpleStringProperty(id);

        }

        public String getName() {
            return name.get();
        }

        public String getCategory() {
            return category.get();
        }

        public String getAddress() {
            return address.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getId() {
            return id.get();
        }

    }

}
