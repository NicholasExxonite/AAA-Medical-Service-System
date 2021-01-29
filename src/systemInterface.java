import java.rmi.RemoteException;

public interface systemInterface extends java.rmi.Remote {
    // add takes two long values, adds them together and returns the resulting
    // long value


    public boolean registerAccount(String username, String password) throws RemoteException;
    public boolean loginAttempt(String username, String password) throws RemoteException;
}
