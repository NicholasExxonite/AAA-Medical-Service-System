import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



public class systemImplementation extends java.rmi.server.UnicastRemoteObject implements systemInterface
{
    //Temporary hashmap to store usernames and passwords.
    private HashMap<String, String> credentials;

        // Implementations must have an explicit constructor
        // in order to declare the RemoteException exception

    public systemImplementation()
        throws java.rmi.RemoteException {
            super();
            credentials = new HashMap<>();
        }


    /**
     * Method that tries to register the uesr with the inputted username/password combination
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */
    public synchronized boolean registerAccount(String username, String password) throws RemoteException{
        //always false for now.
        boolean account_exists = false;
        //Code to check database/data array if the username+password combination already exists.

        //If it doesn't exist return true - account is created
        if(!account_exists){
            //Not saved anywhere as of yet.
            UUID user_id = UUID.randomUUID();

            //test on server end.
            System.out.println("User info: " + username + "\n" + password +"\n"+ user_id);

            //populate database
            credentials.put(username, password);

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
        for(Map.Entry e : credentials.entrySet())
        {
            if(username.equals(e.getKey()))
            {
                if(password.equals(e.getValue()))
                {
                    System.out.println("User successfully logged in.");
                    //Return true to the client
                    return true;
                }
                else
                {
                    System.out.println("No such username/password combination found");
                }
            }
        }
        //Return false to the client.
        return false;
    }
}
