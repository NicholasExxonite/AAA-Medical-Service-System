import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.HashMap;

public interface systemInterface extends java.rmi.Remote {
    // add takes two long values, adds them together and returns the resulting
    // long value


    boolean registerAccount(String username, String password, Role role) throws RemoteException;
    public boolean create_record(String username, String record_name, String record_info) throws RemoteException;
    public Record getRecord(String username) throws RemoteException;
    public String display_records()throws RemoteException;
    public String display_one_record(Integer id) throws RemoteException;
    public void updateRecord(Integer id,String new_info) throws RemoteException;
    public void deleteRecord(Integer id) throws RemoteException;
    public HashMap<Integer, Record> getRecords_list() throws RemoteException;
    char[] generate_password(int len) throws RemoteException;
    public String get_cur_user_role(String username) throws RemoteException;
    public boolean roleAuthorization(String username, String operation) throws RemoteException;
//    public boolean registerAccount(User user) throws RemoteException;
    boolean loginAttempt(String username, String password) throws RemoteException;
    User get_cur_user(String username) throws RemoteException;
    User get_anonymity_user(String username) throws RemoteException;
    public int clientHello(long clientRandom) throws RemoteException;
    public PublicKey getPublicKey() throws RemoteException;
    public long getServerRandom(int sessionId) throws RemoteException;
    public void setSecret(byte[] secret, int sessionId) throws RemoteException;
    public void clientFinished(byte[] clientFinished, int sessionId) throws RemoteException;
    public byte[] serverFinished(int sessionId) throws RemoteException;
}
