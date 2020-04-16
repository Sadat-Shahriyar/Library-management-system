/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class MemberListController {
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
            this.mobile= new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
            this.id= new SimpleStringProperty(id);
            
            
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
