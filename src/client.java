import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.nio.ByteBuffer;

public class client {
    private Boolean is_signedin = false;
    private User current_user;
    //The id number of the current session. used so the server knows which key to use. should be passed with each message
	private int sessionId;
	//The key used for encrypting and decrypting messages
	private SecretKey sessionKey;
	//The converter object used to encrypt and decrypt messages
	private Converter converter = new Converter();

    private client(){
        try{
            Registry registry = LocateRegistry.getRegistry( null);
            systemInterface si = (systemInterface) Naming.lookup("systemInterface");

            if(!sessionKeyNegotiation(si))
            {
                System.out.println("Session Key Negotiaion failed");
                return;
            }
    
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
		    
		    Role role=new Role();
                ThreadLocal<Role> roleThreadLocal=new ThreadLocal<>();
                //patient
                if(input.equals("patient"))
                {
                    role.setRoleName("patient");
                    role.setUserName(current_user.getName());
                    roleThreadLocal.set(role);
                }
                //medical staff
                else if(input.equals("medical staff"))
                {
                    role.setRoleName("medical staff");
                    role.setUserName(current_user.getName());
                    roleThreadLocal.set(role);
                }
                //supervise
                else if(input.equals(" regulator"))
                {
                    role.setRoleName(" regulator");
                    role.setUserName(current_user.getName());
                    roleThreadLocal.set(role);
                }
                else {
                    System.out.println("Unknown command.");
                }
                Map<String,Role> roleMap=new HashMap<>();
                roleMap.put(role.getRoleName(),role);

                havaAuthorisation(roleThreadLocal,roleMap,si);
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

     /**
     * Perform handshake with the server to authenticate the server
     * Generates session keys which should be used to encrypt and decrypt future messages
     * @param si
     * @return True if authentication was successful
     */
    public boolean sessionKeyNegotiation(systemInterface si) throws RemoteException
    {
        try
        {
            //Generate clients keys
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            PublicKey clientPublic = pair.getPublic();
            long clientRandom = random.nextLong();

            //send client hello
            sessionId = si.clientHello(clientRandom);

            //get server hello
            PublicKey serverPublic = si.getPublicKey();
            long serverRandom = si.getServerRandom(sessionId);
            if(serverRandom == 0)
            {
                return false;
            }

            //send secret
            long secretRandom = random.nextLong();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublic);
            cipher.update(converter.longToByteArray(secretRandom,16));
            byte[] secret = cipher.doFinal();
            si.setSecret(secret, sessionId);

            //create sessionkey
            long totalRandoms = clientRandom + serverRandom + secretRandom;
            sessionKey = new SecretKeySpec(converter.longToByteArray(totalRandoms,16), "AES");

            //send client finished
            byte[] clientFinished = converter.encryptString("finished", sessionKey);
            si.clientFinished(clientFinished, sessionId);

            //get server finished
            byte[] serverFinished = si.serverFinished(sessionId);
            if(serverFinished == null)
            {
                return false;
            }
            String output = converter.decryptString(serverFinished, sessionKey);
            if(output.equals("finished"))
            {
                return true;
            }
            return false;
            
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println();
            System.out.println("NoSuchAlgorithmException :");
            System.out.println(e);
        }
        catch(NoSuchPaddingException e)
        {
            System.out.println();
            System.out.println("NoSuchPaddingException :");
            System.out.println(e);
        }
        catch(BadPaddingException e)
        {
            System.out.println();
            System.out.println("BadPaddingException :");
            System.out.println(e);
        }
        catch(InvalidKeyException e)
        {
            System.out.println();
            System.out.println("InvalidKeyException :");
            System.out.println(e);
        }
        catch(IllegalBlockSizeException e)
        {
            System.out.println();
            System.out.println("IllegalBlockSizeException :");
            System.out.println(e);
        }
        catch(NoSuchProviderException e)
        {
            System.out.println();
            System.out.println("NoSuchProviderException :");
            System.out.println(e);
        }

        return false;
    }
}
