package viewbook;

public class tableElement {
    public int id=0;
    public String bookName="";
    public String autherName="";
    public String publisherName="";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public tableElement(int id, String bookName, String autherName, String publisherName) {
        this.id = id;
        this.bookName = bookName;
        this.autherName = autherName;
        this.publisherName = publisherName;
    }
}

