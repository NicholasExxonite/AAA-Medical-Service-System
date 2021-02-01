import java.rmi.RemoteException;
import java.security.PublicKey;

public interface systemInterface extends java.rmi.Remote {
    // add takes two long values, adds them together and returns the resulting
    // long value


    boolean registerAccount(String username, String password) throws RemoteException;
//    public boolean registerAccount(User user) throws RemoteException;
    boolean loginAttempt(String username, String password) throws RemoteException;
    User get_cur_user(String username) throws RemoteException;
    public int clientHello(long clientRandom) throws RemoteException;
    public PublicKey getPublicKey() throws RemoteException;
    public long getServerRandom(int sessionId) throws RemoteException;
    public void setSecret(byte[] secret, int sessionId) throws RemoteException;
    public void clientFinished(byte[] clientFinished, int sessionId) throws RemoteException;
    public byte[] serverFinished(int sessionId) throws RemoteException;
}
