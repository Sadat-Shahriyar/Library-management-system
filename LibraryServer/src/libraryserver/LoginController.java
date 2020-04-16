/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class LoginController {

    private final String username;
    private final String password;
    private String userId;

    public LoginController(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean query() {
        try {
            String userName;
            String passWord;
                  
            Connection connection = DriverManager.getConnection("jdbc:derby:ServerDataBase");
            if(connection!=null) System.out.println("Connection success");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT *FROM UserTable1");
            while(result.next()){
                userName = result.getString("username");
                passWord = result.getString("password");
                if(userName.equals(username) && passWord.equals(password)){
                    connection.close();
                    return true;
                }
            }
            connection.close();
                    } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
