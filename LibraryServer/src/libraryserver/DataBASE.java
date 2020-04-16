/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Taufiqun Nur Farid
 */
public final class DataBASE {

    private final static Logger LOGGER = LogManager.getLogger(DataBASE.class.getName());
    public static DataBASE handler=null;
    public static final String db_url = "jdbc:derby:ServerDataBase;create=true";
    public static Statement statement = null;
    public static Connection connection = null;
    private static HTML.Tag FROM;

    public DataBASE() {
        setConnection();
        setupUserTable();
        inflateDB();
        createTable1();
        createTable2();

    }

    private static void setConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(db_url);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cant load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public ResultSet exQuery(String query) {
        ResultSet result = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (Exception e) {

        }
        return result;

    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result;
    }
    public boolean exAction(String action) {
        try {
            System.out.println(action);
            statement = connection.createStatement();
            System.out.println("connection established");
            statement.executeUpdate(action);
            System.out.println("query executed");
            return true;
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            System.out.println("inside exaction exception");
            return false;
        }
    }

    public boolean execAction(String qu) {
        try {
            statement = connection.createStatement();
            statement.execute(qu);
            System.out.println("query executed");
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }

    private static void inflateDB() {
        List<String> tableData = new ArrayList<>();
        try {
            Set<String> loadedTables = getDBTables();
            System.out.println("Already loaded tables " + loadedTables);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DataBASE.class.getClass().getResourceAsStream("/libraryserver/data/tables.xml"));
            NodeList nList = doc.getElementsByTagName("table-entry");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                Element entry = (Element) nNode;
                String tableName = entry.getAttribute("name");
                String query = entry.getAttribute("col-data");
                if (!loadedTables.contains(tableName.toLowerCase())) {
                    tableData.add(String.format("CREATE TABLE %s (%s)", tableName, query));
                }
            }
            if (tableData.isEmpty()) {
                System.out.println("Tables are already loaded");
            } else {
                System.out.println("Inflating new tables.");
                createTables(tableData);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
            ex.printStackTrace();

        }
    }

    private static Set<String> getDBTables() throws SQLException {
        Set<String> set = new HashSet<>();
        DatabaseMetaData dbmeta = connection.getMetaData();
        readDBTable(set, dbmeta, "TABLE", null);
        return set;
    }

    private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
        ResultSet rs = dbmeta.getTables(null, schema, null, new String[]{searchCriteria});
        while (rs.next()) {
            set.add(rs.getString("TABLE_NAME").toLowerCase());
        }
    }

    private static void createTables(List<String> tableData) throws SQLException {
        Statement statement = connection.createStatement();
        statement.closeOnCompletion();
        for (String command : tableData) {
            System.out.println(command);
            statement.addBatch(command);
        }
        statement.executeBatch();
    }

    static void setupUserTable() {
        String TABLE_NAME = "UserTable1";
        try {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exist. Ready to go!");
            } else {
                statement.execute("Create Table " + TABLE_NAME + "("
                        + "     name varchar(100),\n"
                        + "     password varchar(50),\n"
                        + "     address varchar(200),\n"
                        + "     mobile varchar(20),\n"
                        + "     email varchar(50),\n"
                        + "     username varchar(50) primary key"
                        + ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + ".......setupDatabase");
        } finally {
        }

    }
    
        private void createTable2() {
         String TABLE_NAME = "PERSONAL_LIBRARY";
        try {
            statement = connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exist. Ready to go!");
            } else {
                statement.execute("Create Table " + TABLE_NAME + "("
                        + "     USERNAME varchar(200),\n"
                        + "     BOOKNAME varchar(200),\n"
                        + "     AUTHERNAME varchar(200)"
                        + ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + ".......setupDatabase");
        } finally {
        }
}
     private void createTable1() {
        String TABLE_NAME = "ONLINELIBRARY";
        try {
            statement= connection.createStatement();
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exist. Ready to go!");
            } else {
                statement.execute("Create Table " + TABLE_NAME + "("
                        + "     ID integer primary key,\n"
                        + "     BOOK_TITLE varchar(200),\n"
                        + "     AUTHER_NAME varchar(200),\n"
                        + "     PUBLISHER_NAME varchar(200)"
                        + ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + ".......setupDatabase");
        } finally {
        }

    }

    public Connection getConnection() {
        return connection;
    }

    public static DataBASE getInstance() {
        if (handler == null) {
            handler = new DataBASE();
        }
        return handler;
    }
    
    public boolean deleteBook(Book book) {
        try {
            String deleteStatement = "DELETE FROM BOOK1 WHERE ID = ?";
            PreparedStatement stmt = connection.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }
    
    public boolean isBookAlreadyIssued(Book book) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUEBOOK WHERE bid=?";
            PreparedStatement stmt = connection.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
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

    public boolean deleteMember(MemberListController.Member member) {
        try {
            String deleteStatement = "DELETE FROM MEMBER1 WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(deleteStatement);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean isMemberHasAnyBooks(MemberListController.Member member) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUEBOOK WHERE mid=?";
            PreparedStatement stmt = connection.prepareStatement(checkstmt);
            stmt.setString(1, member.getId());
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
    public boolean updateBook(Book book) {
        try {
            String update = "UPDATE BOOK1 SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
            PreparedStatement stmt = connection.prepareStatement(update);

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

    public boolean updateMember(MemberListController.Member member) {
        try {
            String update = "UPDATE MEMBER1 SET NAME=?, CATEGORY=?, ADDRESS=?, MOBILE=?, EMAIL=? WHERE ID=?";
            PreparedStatement stmt = connection.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getCategory());
            stmt.setString(3, member.getAddress());
            stmt.setString(4, member.getMobile());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
        return false;
    }

}