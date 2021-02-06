import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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



public class systemImplementation extends java.rmi.server.UnicastRemoteObject implements systemInterface
{
    //Temporary hashmap to store usernames and passwords.
    private HashMap<String, String> credentials;
    private HashMap<String, User> registerUsers;
    //The converter object used to encrypt and decrypt messages
    private Converter converter = new Converter();
    //The servers private and public key used in session key negotiation
    private KeyPair serverKeypair;
    //Hashmap of client details for credential negotiation
    private HashMap<Integer, SessionKeyNegotiationValues> sknValues = new HashMap<Integer, SessionKeyNegotiationValues>();
    //The log of all changes made
    private LogsList logs = new LogsList();

        // Implementations must have an explicit constructor
        // in order to declare the RemoteException exception

    public systemImplementation()
        throws java.rmi.RemoteException {
            super();
            credentials = new HashMap<>();
            registerUsers = new HashMap<>();
            try
            {
                //Generate the keys
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
                keyGen.initialize(1024, random);
                serverKeypair = keyGen.generateKeyPair();
            }
            catch(NoSuchAlgorithmException e)
            {
                System.out.println();
                System.out.println("NoSuchAlgorithmException :");
                System.out.println(e);
            }
            catch(NoSuchProviderException e)
            {
                System.out.println();
                System.out.println("NoSuchProviderException :");
                System.out.println(e);
            }
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

    //------------------------------------------Session key functions below

    /**
     * Sets the values for a particular client using the values passed
     * @param clientRandom The random number generated by the client
     * @return the new session id generated forthe client
     * @throws RemoteException
     */
    public int clientHello(long clientRandom) throws RemoteException
    {
        sknValues.put(sknValues.size(), new SessionKeyNegotiationValues());
        sknValues.get(sknValues.size()-1).setClientRandom(clientRandom);
        //add log for person attempting connection, identify person later
        logs.addLog("person " + (sknValues.size()-1), "connect", "requested connection");
        //generate a session id for the client
        return sknValues.size()-1;
    }

    /**
     * Returns the server's public key
     * @return The public key of the server
     * @throws RemoteException
     */
    public PublicKey getPublicKey() throws RemoteException
    {
        return (serverKeypair.getPublic());
    }

    /**
     * Returns a random number generated by the server
     * @param sessionId the id of the current session
     * @return The generated random number
     * @throws RemoteException
     */
    public long getServerRandom(int sessionId) throws RemoteException
    {
        try
        {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            sknValues.get(sessionId).setServerRandom(random.nextLong());
            return (sknValues.get(sessionId).getServerRandom());
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println();
            System.out.println("NoSuchAlgorithmException :");
            System.out.println(e);
        }
        catch(NoSuchProviderException e)
        {
            System.out.println();
            System.out.println("NoSuchProviderException :");
            System.out.println(e);
        }
        return 0;
    }

    /**
     * decrypts the secret random number generated and uses it to create the session key
     * @param secret the secret encrypted using the server public key
     * @param sessionId the id of the current session
     * @throws RemoteException
     */
    public void setSecret(byte[] secret, int sessionId) throws RemoteException
    {
        try
        {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, serverKeypair.getPrivate());
            byte[] secretRandom = cipher.doFinal(secret);
            sknValues.get(sessionId).setSecretRandom(converter.byteArrayToLong(secretRandom,16));
            long totalRand = sknValues.get(sessionId).getTotalRandom();
            sknValues.get(sessionId).setKey(new SecretKeySpec(converter.longToByteArray(totalRand,16), "AES"));
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
    }

    /**
     * decrypts the client finish message
     * @param clientFinished the encrypted message sent by the client
     * @param sessionId the id of the current session
     * @throws RemoteException
     */
    public void clientFinished(byte[] clientFinished, int sessionId) throws RemoteException
    {
        String output = converter.decryptString(clientFinished, sknValues.get(sessionId).getKey());
        if(output.equals("finished"))
        {
            sknValues.get(sessionId).clientIsFinished();
        }
    }

    /**
     * sends the client the server finish message
     * @param sessionId the id of the current session
     * @return the encrypted server finish message
     * @throws RemoteException
     */
    public byte[] serverFinished(int sessionId) throws RemoteException
    {
        if(!(sknValues.get(sessionId).isClientFinished()))
        {
            return null;
        }
        byte[] clientFinished = converter.encryptString("finished", sknValues.get(sessionId).getKey());
        return (clientFinished);
    }

    //---------------------------------------------------------------------

}
