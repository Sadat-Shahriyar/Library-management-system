package library.main;

public class bookSharers {
    String name ;
    String username;

    public bookSharers(String name, String u) {
        this.name = name;
        username = u;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
