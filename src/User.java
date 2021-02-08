import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    public void setName(String name) {
        this.name = name;
    }
    private String name;
    private String password;
    private UUID id;

    public User(String name, String password){
        this.name = name;
        this.password = password;
        //Create random UUID upon initialization
        this.id = UUID.randomUUID();

    }
public User(){

    }
    
    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
