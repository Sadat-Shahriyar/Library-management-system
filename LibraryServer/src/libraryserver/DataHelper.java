/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Taufiqun Nur Farid
 */
public class DataHelper {
    private final static Logger LOGGER = LogManager.getLogger(DataBASE.class.getName());

   
    public static boolean insertNewBook(Book book) {
        try {
            PreparedStatement statement = DataBASE.getInstance().getConnection().prepareStatement(
                    "INSERT INTO BOOK1(title,author,publisher,id,isAvail) VALUES(?,?,?,?,?)");
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setString(4, book.getId());
            statement.setBoolean(5, book.getAvailability());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
            //LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean insertNewMember(Member member) {
        try {
            PreparedStatement statement = DataBASE.getInstance().getConnection().prepareStatement(
                    "INSERT INTO MEMBER1(name,category,address,mobile,email,id) VALUES(?,?,?,?,?,?)");
            statement.setString(1, member.getName());
            statement.setString(2, member.getCategory());
            statement.setString(3, member.getAddress());
            statement.setString(4, member.getMobile());
            statement.setString(5, member.getEmail());
            statement.setString(6, member.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean isBookExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM BOOK1 WHERE id=?";
            PreparedStatement stmt = DataBASE.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean isMemberExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM MEMBER1 WHERE id=?";
            PreparedStatement stmt = DataBASE.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static boolean isUserExists(String id) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM UserTable1 WHERE username=?";
            PreparedStatement stmt = DataBASE.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }
    public static ResultSet getBookInfoWithIssueData(String id) {
        try {
            String query = "SELECT BOOK1.title, BOOK1.author, BOOK1.isAvail, ISSUEBOOK.issueTime FROM BOOK1 LEFT JOIN ISSUEBOOK on BOOK1.id = ISSUEBOOK.bid where BOOK1.id = ?";
            PreparedStatement stmt = DataBASE.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }

    public static void wipeTable(String tableName) {
        try {
            Statement statement = DataBASE.getInstance().getConnection().createStatement();
            statement.execute("DELETE FROM " + tableName + " WHERE TRUE");
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
    }

    public static boolean updateMailServerInfo(MailServerInfo mailServerInfo) {
        try {
            wipeTable("MAIL_SERVER_INFO");
            PreparedStatement statement = DataBASE.getInstance().getConnection().prepareStatement(
                    "INSERT INTO MAIL_SERVER_INFO(server_name,server_port,user_email,user_password,ssl_enabled) VALUES(?,?,?,?,?)");
            statement.setString(1, mailServerInfo.getMailServer());
            statement.setInt(2, mailServerInfo.getPort());
            statement.setString(3, mailServerInfo.getEmailID());
            statement.setString(4, mailServerInfo.getPassword());
            statement.setBoolean(5, mailServerInfo.getSslEnabled());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public static MailServerInfo loadMailServerInfo() {
        try {
            String checkstmt = "SELECT * FROM MAIL_SERVER_INFO";
            PreparedStatement stmt = DataBASE.getInstance().getConnection().prepareStatement(checkstmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mailServer = rs.getString("server_name");
                Integer port = rs.getInt("server_port");
                String emailID = rs.getString("user_email");
                String userPassword = rs.getString("user_password");
                Boolean sslEnabled = rs.getBoolean("ssl_enabled");
                return new MailServerInfo(mailServer, port, emailID, userPassword, sslEnabled);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return null;
    }
    
}
