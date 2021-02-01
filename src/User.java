import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private String name;
    private String password;
    private UUID id;

    public User(String name, String password){
        this.name = name;
        this.password = password;
        //Create random UUID upon initialization
        this.id = UUID.randomUUID();

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