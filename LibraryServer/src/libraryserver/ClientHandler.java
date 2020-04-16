/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author taufiqun Nur Farid
 */
public class ClientHandler extends Thread {

    private BufferedReader fromClient;
    private PrintStream toClient;
    private Socket socket;
    String searchBookName;
    public ClientHandler(BufferedReader fromClient, PrintStream toClient, Socket socket) {
        this.fromClient = fromClient;
        this.toClient = toClient;
        this.socket = socket;
    }

    @Override
    public void run() {

        String uName = null;
        DataBASE dbase = new DataBASE();
        boolean logIn = false;
        String command = null;
        System.out.println("thread started");
        while (true) {
            try {
                try{
                command = fromClient.readLine();
                }catch(Exception e){
                    break;
                }
                System.out.println(command);
                if (command.equals("login") && !logIn) {
                    String username = fromClient.readLine();
                    String password = fromClient.readLine();
                    uName = username;
                    System.out.println(username + " " + password);
                    LoginController login = new LoginController(username, password);
                    if (login.query()) {
                        toClient.print("okay" + "\n");
                        toClient.flush();
                        logIn = true;
                        //toClient.writeUTF(login.getUserId());
                    } else {
                        toClient.print("no" + "\n");
                        toClient.flush();
                    }
                } else if (command.equals("register") && !logIn) {
                    String username = fromClient.readLine();
                    String password = fromClient.readLine();
                    String address = fromClient.readLine();
                    String mobile = fromClient.readLine();
                    String email = fromClient.readLine();
                    String id = fromClient.readLine();
                    System.out.println(username + " " + password);

                    boolean msg = DataHelper.isUserExists(id);

                    if (msg) {
                        toClient.print("repeatation" + "\n");
                        toClient.flush();

                    } else {
                        String st = "INSERT INTO UserTable1 VALUES ("
                                + "'" + username + "',"
                                + "'" + password + "',"
                                + "'" + address + "',"
                                + "'" + mobile + "',"
                                + "'" + email + "',"
                                + "'" + id + "'"
                                + ")";

                        System.out.println(st);

                        if (dbase.execAction(st)) {
                            toClient.print("success" + "\n");
                            toClient.flush();

                        } else {
                            toClient.print("fail" + "\n");
                            toClient.flush();
                        }

                    }
                } else if (command.equals("add")) {
                    Boolean isInEditMode = Boolean.FALSE;
                    String bookname = fromClient.readLine();
                    String bookauthor = fromClient.readLine();
                    String publisher = fromClient.readLine();
                    String id = fromClient.readLine();

                    System.out.println(bookname + " " + bookauthor);

                    boolean result1 = DataHelper.isBookExists(id);
                    if (result1) {
                        toClient.print("exist" + "\n");
                        toClient.flush();
                    } else {

                        Book book = new Book(bookname, bookauthor, publisher, id, Boolean.TRUE);
                        boolean result = DataHelper.insertNewBook(book);
                        if (result) {
                            //AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New book added", bookName + " has been added");
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            //AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
                            toClient.print("fail" + "\n");
                            toClient.flush();
                        }
                    }
                } else if (command.equals("viewlibrarybooks")) {
                    String bookname, author, publisher, id;
                    boolean available;
                    ResultSet result = dbase.exQuery("SELECT * FROM book1");
                    while (result.next()) {

                        bookname = result.getString("title");
                        author = result.getString("author");
                        publisher = result.getString("publisher");
                        id = result.getString("id");
                        available = result.getBoolean("isAvail");
                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(bookname + "\n");
                        toClient.flush();
                        toClient.print(author + "\n");
                        toClient.flush();
                        toClient.print(publisher + "\n");
                        toClient.flush();
                        toClient.print(id + "\n");
                        toClient.flush();
                        toClient.print(String.valueOf(available) + "\n");
                        toClient.flush();

                    }
                    toClient.print("false" + "\n");
                    toClient.flush();

                } else if (command.equals("addMember")) {
                    System.out.println("ulala");
                    Boolean isInEditMode = Boolean.FALSE;
                    String name = fromClient.readLine();
                    String catagory = fromClient.readLine();
                    String address = fromClient.readLine();
                    String mobile = fromClient.readLine();
                    String email = fromClient.readLine();
                    String id = fromClient.readLine();
                    //System.out.println(name + " " + catagory);

                    boolean result1 = DataHelper.isMemberExists(id);
                    if (result1) {
                        toClient.print("exist" + "\n");
                        toClient.flush();
                    } else {
                        Member member = new Member(name, catagory, address, mobile, email, id);
                        boolean result = DataHelper.insertNewMember(member);
                        if (result) {
                            //AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New book added", bookName + " has been added");
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            //AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new book", "Check all the entries and try again");
                            toClient.print("fail" + "\n");
                            toClient.flush();
                        }
                    }

                } else if (command.equals("viewMembers")) {
                    String name, catagory, address, id, mobile, email;
                    ResultSet result = dbase.exQuery("SELECT * FROM member1");
                    while (result.next()) {

                        name = result.getString("name");
                        catagory = result.getString("category");
                        address = result.getString("address");
                        mobile = result.getString("mobile");
                        email = result.getString("email");
                        id = result.getString("id");
                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(name + "\n");
                        toClient.flush();
                        toClient.print(catagory + "\n");
                        toClient.flush();
                        toClient.print(address + "\n");
                        toClient.flush();
                        toClient.print(mobile + "\n");
                        toClient.flush();
                        toClient.print(email + "\n");
                        toClient.flush();
                        toClient.print(id + "\n");
                        toClient.flush();

                    }
                    toClient.print("false" + "\n");
                    toClient.flush();

                } else if (command.equals("searchBook")) {
                    String bid = fromClient.readLine();
                    String bqu = "SELECT * FROM BOOK1 WHERE id = '" + bid + "'";
                    ResultSet brs = dbase.execQuery(bqu);
                    try {
                        while (brs.next()) {
                            String bName = brs.getString("title");
                            String bauthor = brs.getString("author");
                            String bpublisher = brs.getString("publisher");
                            Boolean bavail = brs.getBoolean("isAvail");

                            toClient.print("true" + "\n");
                            toClient.flush();

                            toClient.print(bName + "\n");
                            toClient.flush();
                            toClient.print(bauthor + "\n");
                            toClient.flush();
                            toClient.print(bpublisher + "\n");
                            toClient.flush();
                            toClient.print(bavail + "\n");
                            toClient.flush();

                        }
                        toClient.print("false" + "\n");
                        toClient.flush();

                    } catch (SQLException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (command.equals("searchMember")) {
                    String mid = fromClient.readLine();
                    String mqu = "SELECT * FROM MEMBER1 WHERE id = '" + mid + "'";
                    ResultSet brs = dbase.execQuery(mqu);
                    try {
                        while (brs.next()) {
                            String mName = brs.getString("name");
                            String mAddress = brs.getString("address");
                            String mEmail = brs.getString("email");

                            toClient.print("true" + "\n");
                            toClient.flush();

                            toClient.print(mName + "\n");
                            toClient.flush();
                            toClient.print(mAddress + "\n");
                            toClient.flush();
                            toClient.print(mEmail + "\n");
                            toClient.flush();

                        }
                        toClient.print("false" + "\n");
                        toClient.flush();

                    } catch (SQLException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (command.equals("issueBook")) {
                    String bid = fromClient.readLine();
                    String mid = fromClient.readLine();

                    String str = "INSERT INTO ISSUEBOOK(mid,bid) VALUES ("
                            + "'" + mid + "',"
                            + "'" + bid + "')";
                    String str1 = "UPDATE BOOK1 SET isAvail = false WHERE id = '" + bid + "'";
                    System.out.println(str + " and " + str1);

                    if (dbase.execAction(str) && dbase.execAction(str1)) {
                        toClient.print("success" + "\n");
                        toClient.flush();
                    }

                } else if (command.equals("renew")) {
                    String bookid = fromClient.readLine();
                    System.out.println(bookid);
//                    String qur = "SELECT * FROM BOOK1 WHERE id = '" + bookid + "'";
//
//                    ResultSet result = dbase.execQuery(qur);
//                    Boolean avail = result.getBoolean("isAvail");
//                    System.out.println(avail);
                    String qu = "SELECT * FROM ISSUEBOOK WHERE bid = '" + bookid + "'";
                    ResultSet rs = dbase.execQuery(qu);

                    while (rs.next()) {
                        toClient.print("success" + "\n");
                        toClient.flush();

                        String mBookID = bookid;
                        String mMemberID = rs.getString("mid");
                        Timestamp IssueTime = rs.getTimestamp("issueTime");
                        String mIssueTime = IssueTime.toString();
                        System.out.println(mIssueTime);
                        int mrenewCount = rs.getInt("renew_count");

                        toClient.print(mBookID + "\n");
                        toClient.flush();
                        toClient.print(mMemberID + "\n");
                        toClient.flush();
                        toClient.print(mIssueTime + "\n");
                        toClient.flush();
                        toClient.print(String.valueOf(mrenewCount) + "\n");
                        toClient.flush();
                        System.out.println("xyz1");
                        qu = "SELECT * FROM BOOK1 WHERE id = '" + mBookID + "'";
                        ResultSet r1 = dbase.execQuery(qu);

                        while (r1.next()) {
                            String name = r1.getString("title");
                            String author = r1.getString("author");
                            String publisher = r1.getString("publisher");
                            String bid = r1.getString("id");

                            toClient.print(name + "\n");
                            toClient.flush();
                            toClient.print(author + "\n");
                            toClient.flush();
                            toClient.print(publisher + "\n");
                            toClient.flush();
                            toClient.print(bid + "\n");
                            toClient.flush();
                            System.out.println("xyz2");
                        }

                        qu = "SELECT * FROM MEMBER1 WHERE id = '" + mMemberID + "'";
                        r1 = dbase.execQuery(qu);

                        while (r1.next()) {
                            String name = r1.getString("name");
                            String address = r1.getString("address");
                            String email = r1.getString("email");

                            toClient.print(name + "\n");
                            toClient.flush();
                            toClient.print(address + "\n");
                            toClient.flush();
                            toClient.print(email + "\n");
                            toClient.flush();

                        }

                    }

                    toClient.print("end" + " \n");
                    toClient.flush();

                } else if (command.equals("submit")) {
                    String id = fromClient.readLine();

                    String message = fromClient.readLine();
                    if (message.equals("executeQuery")) {

                        String ac1 = "DELETE FROM ISSUEBOOK WHERE bid = '" + id + "'";
                        String ac2 = "UPDATE BOOK1 SET isAvail = TRUE WHERE id = '" + id + "'";

                        if (dbase.execAction(ac1) && dbase.execAction(ac2)) {
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            toClient.print("fail" + "\n");
                            toClient.flush();
                        }

                    }
                } else if (command.equals("showissue")) {

                    String qu = "SELECT ISSUEBOOK.bid, ISSUEBOOK.mid, ISSUEBOOK.issueTime, MEMBER1.name, BOOK1.title FROM ISSUEBOOK\n"
                            + "LEFT OUTER JOIN MEMBER1\n"
                            + "ON MEMBER1.id = ISSUEBOOK.mid\n"
                            + "LEFT OUTER JOIN BOOK1\n"
                            + "ON BOOK1.id = ISSUEBOOK.bid";
                    ResultSet rs = dbase.execQuery(qu);

                    while (rs.next()) {

                        String memberName = rs.getString("name");
                        String bookID = rs.getString("bid");
                        String bookTitle = rs.getString("title");
                        Timestamp issueTime = rs.getTimestamp("issueTime");

                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(memberName + "\n");
                        toClient.flush();
                        toClient.print(bookID + "\n");
                        toClient.flush();
                        toClient.print(bookTitle + "\n");
                        toClient.flush();
                        toClient.print(issueTime.toString() + "\n");
                        toClient.flush();

                    }
                    toClient.print("false" + "\n");
                    toClient.flush();
                } else if (command.equals("renewcount")) {
                    String msg = fromClient.readLine();
                    if (msg.equals("confirm")) {
                        String id = fromClient.readLine();
                        System.out.println(id);
                        String ac = "UPDATE ISSUEBOOK SET issueTime = CURRENT_TIMESTAMP, renew_count=renew_count+1 WHERE bid = '" + id + "'";
                        if (dbase.execAction(ac)) {
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            toClient.print("fail" + "\n");
                            toClient.flush();
                        }
                    }
                } else if (command.equals("onlineAdd")) {
                    String bookName = fromClient.readLine();
                    String authorName = fromClient.readLine();
                    String publisherName = fromClient.readLine();

                    ResultSet res = dbase.execQuery("SELECT * FROM ONLINELIBRARY");

                    int x = 0;
                    while (res.next()) {
                        x = res.getInt("ID");
                    }
                    System.out.println(x);
                    if (!(checkBookExists(bookName))) {

                        toClient.print("bookNot" + "\n");
                        toClient.flush();

                        String msg = fromClient.readLine();
                        if (msg.equals("query")) {
                            x++;

                            String query = "INSERT INTO ONLINELIBRARY VALUES ("
                                    + "" + x + ","
                                    + "'" + bookName + "',"
                                    + "'" + authorName + "',"
                                    + "'" + publisherName + "'"
                                    + ")";

                            System.out.println(query);
                            if (dbase.execAction(query)) {
                                //JOptionPane.showMessageDialog(null, "success!");
                                System.out.println("x");
                                toClient.print("queryDone" + "\n");
                                toClient.flush();
//                                
//                                
//                               recieve file from client
//                                
//                                recieve file from cilent
//                                

                                String bookNameString = bookName + ".pdf";
                                String fileSize = fromClient.readLine();
                                int size = Integer.parseInt(fileSize);
                                byte[] contents = new byte[10000];
                                FileOutputStream fos = new FileOutputStream(bookNameString);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                InputStream is = socket.getInputStream();

                                int bytesRead = 0;
                                int total = 0;
                                while (total != size) {
                                    bytesRead = is.read(contents);
                                    total += bytesRead;
                                    bos.write(contents, 0, bytesRead);
                                }

                                bos.flush();
                                fos.close();

                                File f = new File(bookNameString);
                                CopyFile.copyFile(f, bookName);

                            } else {
                                // JOptionPane.showMessageDialog(null, "Failed");
                                System.out.println("y");
                            }

                        }

                    } else {
                        toClient.print("bookYes" + "\n");
                        toClient.flush();
                    }

                } else if (command.equals("onlineTable")) {
                    ResultSet result = dbase.execQuery("SELECT *FROM ONLINELIBRARY");
                    while (result.next()) {
                        int id = result.getInt("ID");
                        String bookname = result.getString("BOOK_TITLE");
                        String authername = result.getString("AUTHER_NAME");
                        String publishername = result.getString("PUBLISHER_NAME");

                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(String.valueOf(id) + "\n");
                        toClient.flush();
                        toClient.print(bookname + "\n");
                        toClient.flush();
                        toClient.print(authername + "\n");
                        toClient.flush();
                        toClient.print(publishername + "\n");
                        toClient.flush();

                    }

                    toClient.print("false" + "\n");
                    toClient.flush();

                } else if (command.equals("searchOnline")) {
                    String book = fromClient.readLine();

                    ResultSet result = dbase.execQuery("SELECT *FROM ONLINELIBRARY");
                    while (result.next()) {

                        int id = result.getInt("ID");
                        String bookname = result.getString("BOOK_TITLE");
                        String authername = result.getString("AUTHER_NAME");
                        String publishername = result.getString("PUBLISHER_NAME");
                        if (book.equalsIgnoreCase(bookname)) {
                            searchBookName = bookname;
                            toClient.print("true" + "\n");
                            toClient.flush();
                            toClient.print(String.valueOf(id) + "\n");
                            toClient.flush();
                            toClient.print(bookname + "\n");
                            toClient.flush();
                            toClient.print(authername + "\n");
                            toClient.flush();
                            toClient.print(publishername + "\n");
                            toClient.flush();
                            break;
                        }

                    }

                    toClient.print("false" + "\n");
                    toClient.flush();
                } else if (command.equals("personalInfo")) {

                    ResultSet result = dbase.exQuery("SELECT * FROM UserTable1 WHERE username = '" + uName + "'");
                    if (result.next()) {
                        String name = result.getString("name");
                        String address = result.getString("address");
                        String email = result.getString("email");

                        toClient.print(name + "\n");
                        toClient.flush();

                        toClient.print(address + "\n");
                        toClient.flush();

                        toClient.print(email + "\n");
                        toClient.flush();

                    }

                } else if (command.equals("addpersonalbook")) {
                    String bookName = fromClient.readLine();
                    String author = fromClient.readLine();
                    boolean bookExist = false;
                    ResultSet result = dbase.exQuery("SELECT * FROM PERSONAL_LIBRARY");
                    try {
                        while (result.next()) {
                            String bookname = result.getString("BOOKNAME");
                            String username = result.getString("USERNAME");
                            if (bookname.equalsIgnoreCase(bookName) && username.equals(uName)) {
                                bookExist = true;

                                break;
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (!bookExist) {
                        toClient.print("notexist" + "\n");
                        toClient.flush();
                        String query = "INSERT INTO PERSONAL_LIBRARY VALUES ("
                                + "'" + uName + "',"
                                + "'" + bookName + "',"
                                + "'" + author + "'"
                                + ")";

                        if (dbase.exAction(query)) {
                            System.out.println("book added");
                            toClient.print("added" + "\n");
                            toClient.flush();
                        } else {
                            System.out.println("book not added");
                            toClient.print("notadded" + "\n");
                            toClient.flush();
                        }

                    } else {
                        toClient.print("exist" + "\n");
                        toClient.flush();

                    }

                } else if (command.equals("viewPersonalLibrary")) {
                    ResultSet result = dbase.exQuery("SELECT * FROM PERSONAL_LIBRARY");

                    try {
                        while (result.next()) {

                            String username = result.getString("USERNAME");
                            if (username.equals(uName)) {
                                toClient.print("true" + "\n");
                                toClient.flush();
                                String bookName = result.getString("BOOKNAME");
                                String autherName = result.getString("AUTHERNAME");

                                toClient.print(bookName + "\n");
                                toClient.flush();
                                toClient.print(autherName + "\n");
                                toClient.flush();

                            }
                        }

                        toClient.print("false" + "\n");
                        toClient.flush();
                    } catch (SQLException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (command.endsWith("deletePersonalLibrary")) {

                    String bookName = fromClient.readLine();
                    String autherName = fromClient.readLine();
                    String query1 = "SELECT * FROM PERSONAL_LIBRARY";

                    System.out.println(query1);
                    ResultSet result = dbase.execQuery(query1);

                    while (result.next()) {
                        String username = result.getString("USERNAME");
                        String bookname = result.getString("BOOKNAME");
                        String authername = result.getString("AUTHERNAME");
                        if (bookname.equalsIgnoreCase(bookName) && authername.equalsIgnoreCase(autherName) && username.equals(uName)) {
                            toClient.print("exist" + "\n");
                            toClient.flush();

                            String msg = fromClient.readLine();
                            if (msg.equals("delete")) {
                                String query = "DELETE FROM PERSONAL_LIBRARY WHERE USERNAME = '" + uName + "' AND BOOKNAME = '" + bookname + "' AND AUTHERNAME = '" + authername + "'";
                                if (dbase.execAction(query)) {
                                    toClient.print("success" + "\n");
                                    toClient.flush();
                                } else {
                                    toClient.print("fail" + "\n");
                                    toClient.flush();
                                }
                            }

                            break;
                        }

                    }
                    toClient.print("notExist" + "\n");
                    toClient.flush();

                } else if (command.equals("editBook")) {
                    String title = fromClient.readLine();
                    String author = fromClient.readLine();
                    String publisher = fromClient.readLine();
                    String id = fromClient.readLine();
                    boolean available = Boolean.parseBoolean(fromClient.readLine());

                    boolean avail = dbase.isBookAlreadyIssued(new Book(title, author, publisher, id, available));
                    if (avail) {
                        toClient.print("notavailable" + "\n");
                        toClient.flush();
                    } else {
                        if (dbase.updateBook(new Book(title, author, publisher, id, available))) {
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            toClient.print("false" + "\n");
                            toClient.flush();
                        }
                    }
                } else if (command.equals("editMember")) {
                    String name = fromClient.readLine();
                    String category = fromClient.readLine();
                    String address = fromClient.readLine();
                    String mobile = fromClient.readLine();
                    String email = fromClient.readLine();
                    String id = fromClient.readLine();
                    if (dbase.isMemberHasAnyBooks(new MemberListController.Member(name, category, address, mobile, email, id))) {
                        toClient.print("hasBook" + "\n");
                        toClient.flush();
                    } else {
                        if (dbase.updateMember(new MemberListController.Member(name, category, address, mobile, email, id))) {
                            toClient.print("success" + "\n");
                            toClient.flush();
                        } else {
                            toClient.print("false" + "\n");
                            toClient.flush();
                        }
                    }

                } else if (command.equals("deleteLibraryBook")) {
                    String bookName = fromClient.readLine();
                    String author = fromClient.readLine();
                    String publisher = fromClient.readLine();
                    String id = fromClient.readLine();
                    boolean available = Boolean.parseBoolean(fromClient.readLine());

                    Book selectedForDeletion = new Book(bookName, author, publisher, id, available);

                    if (dbase.isBookAlreadyIssued(selectedForDeletion)) {
                        toClient.print("alreadyIssued" + "\n");
                        toClient.flush();
                        continue;
                    } else {
                        toClient.print("no" + "\n");
                        toClient.flush();

                    }

                    String msg1 = fromClient.readLine();
                    if (msg1.equals("delete")) {
                        Boolean result = dbase.deleteBook(selectedForDeletion);
                        if (result) {
                            toClient.print("deleted" + "\n");
                            toClient.flush();

                        } else {
                            toClient.print("no" + "\n");
                            toClient.flush();
                        }
                    }

                } else if (command.equals("deleteMember")) {
                    String name = fromClient.readLine();
                    String category = fromClient.readLine();
                    String address = fromClient.readLine();
                    String mobile = fromClient.readLine();
                    String email = fromClient.readLine();
                    String id = fromClient.readLine();
                    MemberListController.Member selectedForDeletion = new MemberListController.Member(name, category, address, mobile, email, id);
                    if (dbase.isMemberHasAnyBooks(selectedForDeletion)) {
                        toClient.print("alreadyHasBook" + "\n");
                        toClient.flush();
                        continue;
                    } else {
                        toClient.print("no" + "\n");
                        toClient.flush();

                    }

                    String msg1 = fromClient.readLine();
                    if (msg1.equals("delete")) {
                        Boolean result = dbase.deleteMember(selectedForDeletion);
                        if (result) {
                            toClient.print("deleted" + "\n");
                            toClient.flush();

                        } else {
                            toClient.print("no" + "\n");
                            toClient.flush();
                        }
                    }

                } else if (command.equals("settings")) {
                    MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();
                    if (mailServerInfo != null) {
                        toClient.print("ok" + "\n");
                        toClient.flush();
                        toClient.print(mailServerInfo.getMailServer() + "\n");
                        toClient.flush();
                        toClient.print(String.valueOf(mailServerInfo.getPort()) + "\n");
                        toClient.flush();
                        toClient.print(mailServerInfo.getEmailID() + "\n");
                        toClient.flush();
                        toClient.print(mailServerInfo.getPassword() + "\n");
                        toClient.flush();
                        toClient.print(String.valueOf(mailServerInfo.getSslEnabled()) + "\n");
                        toClient.flush();

                    } else {
                        toClient.print("no" + "\n");
                        toClient.flush();

                    }

                } else if (command.equals("saveEmail")) {
                    String mailServer = fromClient.readLine();
                    int port = Integer.parseInt(fromClient.readLine());
                    String email = fromClient.readLine();
                    String password = fromClient.readLine();
                    boolean ssl = Boolean.parseBoolean(fromClient.readLine());

                    MailServerInfo mailServerInfo = new MailServerInfo(mailServer, port, email, password, ssl);

                    if (DataHelper.updateMailServerInfo(mailServerInfo)) {
                        toClient.print("success" + "\n");
                        toClient.flush();
                    } else {
                        toClient.print("failed" + "\n");
                        toClient.flush();
                    }

                } else if (command.equals("cancelMember")) {
                    String id = fromClient.readLine();
                    boolean result = dbase.execAction("DELETE FROM MEMBER1 WHERE id = '" + id + "'");
                    if (result) {
                        toClient.print("ok" + "\n");
                        toClient.flush();
                    } else {
                        toClient.print("no" + "\n");
                        toClient.flush();
                    }
                } else if (command.equals("showSharer")) {
                    ResultSet result = dbase.execQuery("SELECT * FROM UserTable1");
                    while (result.next()) {
                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(result.getString("name") + "\n");
                        toClient.flush();
                        toClient.print(result.getString("username") + "\n");
                        toClient.flush();
                    }
                    toClient.print("false" + "\n");
                    toClient.flush();
                } else if (command.equals("showSharedBooks")) {
                    String username = fromClient.readLine();
                    ResultSet result = dbase.execQuery("SELECT * FROM PERSONAL_LIBRARY");
                    while (result.next()) {
                        String userName = result.getString("USERNAME");
                        if (username.equals(userName)) {
                            toClient.print("true" + "\n");
                            toClient.flush();
                            String bookName = result.getString("BOOKNAME");
                            String author = result.getString("AUTHERNAME");
                            toClient.print(bookName + "\n");
                            toClient.flush();
                            toClient.print(author + "\n");
                            toClient.flush();
                        }
                    }
                    toClient.print("false" + "\n");
                    toClient.flush();

                } else if (command.equals("showContact")) {
                    String username = fromClient.readLine();
                    ResultSet result = dbase.execQuery("SELECT * FROM USERTABLE1 WHERE USERNAME = '" + username + "'");
                    if (result.next()) {
                        toClient.print("true" + "\n");
                        toClient.flush();
                        toClient.print(result.getString("name") + "\n");
                        toClient.flush();
                        toClient.print(result.getString("address") + "\n");
                        toClient.flush();
                        toClient.print(result.getString("mobile") + "\n");
                        toClient.flush();
                        toClient.print(result.getString("email") + "\n");
                        toClient.flush();
                    } else {
                        toClient.print("false" + "\n");
                        toClient.flush();
                    }
                } else if (command.equals("download")) {
                    
                    String fileNameString = "F:\\LibraryServer\\Books\\" + searchBookName + ".pdf";

                    File file = new File(fileNameString);
                    FileInputStream fis = null;
                    byte contents[];
                    fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    OutputStream os = socket.getOutputStream();

                    long fileLength = file.length();
                    toClient.print(String.valueOf(fileLength) + '\n');
                    toClient.flush();
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
                    System.out.println(file.getName() + " sent");
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean checkBookExists(String bookName) {
        boolean bookExists = false;
        Statement statement = null;
        String name;
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby:ServerDataBase");
            statement = connection.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM ONLINELIBRARY");
            while (res.next()) {
                name = res.getString("BOOK_TITLE");
                if (name.equalsIgnoreCase(bookName)) {
                    //JOptionPane.showMessageDialog(null, "This book already exosts");
                    //System.out.println("this book already exists");
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
