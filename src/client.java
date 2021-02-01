import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class client {
    private Boolean is_signedin = false;
    private User current_user;

    private client(){
        try{
            Registry registry = LocateRegistry.getRegistry( null);
            systemInterface si = (systemInterface) Naming.lookup("systemInterface");


            //...
            System.out.println("Client initialized. Running.");

            //Guest user UI..
            while(!is_signedin) {


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

            //Logged in user UI
            while(is_signedin){
                System.out.println("Welcome to the user clinet " + current_user.getName() + " !");

                Scanner s = new Scanner(System.in);
                String input = s.nextLine();
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

    /**
     * Main method. Client initialization.
     * @param args
     */
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

        //Instead of saving username and password in a map, create a new user object
        //and save that in the map. (userobj is value, key: user name or id?)
//        User testusr= new User(username, userpass);


        //test print statement
//        System.out.println(testusr.getName() + " "+ testusr.getPassword() + " "+ testusr.getId());

        //try registering.
        if(calculatePassword(userpass)){
            if(si.registerAccount(username, userpass)){
                System.out.println("Account succesfully registered!");
            }
            else System.out.println("Account with this username/password combination already exists.");
        }else System.out.println("register failed");


    }

    /**
     * Method that tries to log in the user.
     * Passes the user inputted username and password
     * to the server to check if such combination exists.
     * @param si
     * @return True if the user is successfully logged in or false if not.
     * @throws RemoteException
     */
    public boolean tryLoggingIn(systemInterface si) throws RemoteException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter username: ");
        String username = sc.nextLine();

        System.out.println("Please enter password: ");
        String password = sc.nextLine();



        //Try logging
        if(si.loginAttempt(username, password))
        {
            System.out.println("You have logged in successfully!");
            //Set user signed up to true
            this.is_signedin = true;

            //If this user exists save his user data in current_user.
            this.current_user = si.get_cur_user(username);
            //System.out.println(current_user.getName() + " " + current_user.getPassword());
            return true;
        }
        else {
            System.out.println("Logging in unsuccessful.");
            return false;
        }
    }

    /**
     * Method that checks a password and calculates a score for it. (0-10)
     * The password must have at least 8 symbols AND at least a score of 6 to be accepted.
     * @param password
     * @return
     */
    private boolean calculatePassword(String password) {
        int pass_score = 0;
        boolean is_passwordGood = false;

        //password length must be 8 or more.
        if (password.length() < 8){
            System.out.println("Password length must be at least 8 characters.");
            return false;
        }

        else if (password.length() >= 10)
            pass_score += 2;
        else
            pass_score += 1;

        //if it contains one digit, add 2 to total score
        if (password.matches("(?=.*[0-9]).*"))
            pass_score += 2;

        //if it contains one lower case letter, add 2 to total score
        if (password.matches("(?=.*[a-z]).*"))
            pass_score += 2;

        //if it contains one upper case letter, add 2 to total score
        if (password.matches("(?=.*[A-Z]).*"))
            pass_score += 2;

        //if it contains one special character, add 2 to total score
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
            pass_score += 2;

        if(pass_score < 6){
            System.out.println("Passowrd too weak" + " " + pass_score);
            return false;
        }
        else return true;
    }
}
