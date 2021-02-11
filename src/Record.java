import java.io.Serializable;

public class Record implements Serializable {
    private String username;
    private String information;
    private String record_name;

    public Record(String username, String record_name, String information){
        this.username = username;
        this.information = information;
        this.record_name = record_name;
    }


    public String getUsername() {
        return username;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecord_name() {
        return record_name;
    }
}
