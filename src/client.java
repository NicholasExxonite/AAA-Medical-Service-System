import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class client {
    private Boolean is_signedup = false;

    private client(){
        try{
            Registry registry = LocateRegistry.getRegistry( null);
            systemInterface si = (systemInterface) Naming.lookup("systemInterface");




            //Start the running loop.
            System.out.println("Client initialized. Running.");

            while(true) {

                Scanner s = new Scanner(System.in);
                String input = s.nextLine();


                //Maybe ues switch cases
                //Exit client.
                if(input.equals("e"))
                {
                    break;
                }
                //Register User
                else if(input.equals("r"))
                {
                    tryRegistering(si);

                }
                //Log in user
                else if(input.equals("l"))
                {
                    tryLoggingIn(si);

                }
                else {
                    System.out.println("Unknown command.");
                }
            }
        }
        // Catch the exceptions that may occur - rubbish URL, Remote exception
        // Not bound exception or the arithmetic exception that may occur in
        // one of the methods creates an arithmetic error (e.g. divide by zero)
        catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
        catch (java.lang.ArithmeticException ae) {
            System.out.println();
            System.out.println("java.lang.ArithmeticException");
            System.out.println(ae);
        }
    }
    public static void main(String[] args)
    {
        client client = new client();

    }

    /**
     * Method that tries to register the user.
     * Passes the user inputted username and password
     * to the server through java RMI for checking.
     *
     * @param si
     * @throws RemoteException
     */
    public void tryRegistering(systemInterface si) throws RemoteException{

        System.out.println("Please enter username: ");
        Scanner sc = new Scanner(System.in);
        String username = sc.nextLine();

        System.out.println("Please enter password: ");
        String userpass = sc.nextLine();

        if(si.registerAccount(username,  userpass))
        {
            System.out.println("Account registered successfully!");
        }
        else System.out.println("Account with this username/password combination already exists.");
    }

    /**
     * Method that tries to log in the user.
     * Passes the user inputted username and password
     * to the server to check if such combination exists.
     * @param ai
     * @return True if the user is successfully logged in or false if not.
     * @throws RemoteException
     */
    public boolean tryLoggingIn(systemInterface ai) throws RemoteException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter username: ");
        String username = sc.nextLine();

        System.out.println("Please enter password: ");
        String password = sc.nextLine();



        //Try logging in after authentication
        if(ai.loginAttempt(username, password))
        {
            System.out.println("You have logged in successfully!");
            //Set user signed up to true
            this.is_signedup = true;
            return true;
        }
        else {
            System.out.println("Logging in unsuccessful.");
            return false;
        }
    }
}
