import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class systemImplementation extends java.rmi.server.UnicastRemoteObject implements systemInterface
{
    //Temporary hashmap to store usernames and passwords.
    private HashMap<String, String> credentials;
    private HashMap<String, User> registerUsers;

        // Implementations must have an explicit constructor
        // in order to declare the RemoteException exception

    public systemImplementation()
        throws java.rmi.RemoteException {
            super();
            credentials = new HashMap<>();
            registerUsers = new HashMap<>();
        }


    /**
     * A method that checks if such an account exists. If not it creates a new one and saves it.
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */
    public synchronized boolean registerAccount(String username, String password) throws RemoteException{
        //always false for now.
        boolean account_exists = false;


        //Code to check database/data array if the username+password combination already exists.
        //...
        //...


        //If it doesn't exist return true, account is created
        if(!account_exists){

            //Create new user
            User user = new User(username, password);
            //Save new users with usernames as key. Maybe use the random UUID ??
            registerUsers.put(user.getName(), user);

            //Tests..
            System.out.println(registerUsers.get(user.getName()).getName());
            System.out.println(registerUsers.get(user.getName()).getPassword());
            System.out.println(registerUsers.get(user.getName()).getId());
            return true;
        }
        else return false;

    }

    /**
     * Method that tries to log in the user with the inputted username/password combination.
     * Using the printline statements just ot tests things on server end.
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */

    public boolean loginAttempt(String username, String password) throws RemoteException {
        //Check if this username exists.
        if(registerUsers.containsKey(username))
        {
            System.out.println("Username exists.");
            //Check if the inputted password matches this user object's passowrd.
            if(password.equals(registerUsers.get(username).getPassword())){
                System.out.println("Passowrd exists! Logged in");
                return true;
            }
            else System.out.println("Username exists, password doesn't match");
        }
        else System.out.println("Username doesn't exist");

        //Return false to the client.
        return false;
    }

    //Get the user object based on the key(username for now)
    public User get_cur_user(String username) throws RemoteException{
        return registerUsers.get(username);
    }
}
