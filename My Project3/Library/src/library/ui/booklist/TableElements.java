package library.ui.booklist;

/**
 *
 * @author USER
 */
public class TableElements {
    String bookname;
    String authorname, publishername , id;
    boolean available;
    int serial;

    public TableElements(String bookname, String authorname, String publishername, String id, boolean available, int serial) {
        this.bookname = bookname;
        this.authorname = authorname;
        this.publishername = publishername;
        this.id = id;
        this.available = available;
        this.serial = serial;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getPublishername() {
        return publishername;
    }

    public void setPublishername(String publishername) {
        this.publishername = publishername;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }
    
}
