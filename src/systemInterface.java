import java.rmi.RemoteException;

public interface systemInterface extends java.rmi.Remote {
    // add takes two long values, adds them together and returns the resulting
    // long value


    boolean registerAccount(String username, String password) throws RemoteException;
//    public boolean registerAccount(User user) throws RemoteException;
     boolean loginAttempt(String username, String password) throws RemoteException;
     User get_cur_user(String username) throws RemoteException;
}
