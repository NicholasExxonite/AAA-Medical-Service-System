import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
    //Temporary hashmap to store usernames and rolenames
    private HashMap<String,Role> roleHashMap;
    //The converter object used to encrypt and decrypt messages
    private Converter converter = new Converter();
    //The servers private and public key used in session key negotiation
    private KeyPair serverKeypair;
    //Hashmap of client details for credential negotiation
    private HashMap<Integer, SessionKeyNegotiationValues> sknValues = new HashMap<Integer, SessionKeyNegotiationValues>();
    //The log of all changes made
    private LogsList logs = new LogsList();
    //Contains a list of users and their assigned roles.
    private HashMap<String, Role> access_list = new HashMap<>();

    //Contains a list of all records. The key is their id.
    private HashMap<Integer, Record> records_list = new HashMap<>();

    private static int rec_counter = 1;

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

            //Dummy users for test-----------------------------
            User user1 = new User("patient1", "patient1!");
            Role role1 = new Role();
            role1.setRoleName("patient");
            User user2 = new User("patient2", "patient2!");
            Role role2 = new Role();
            role2.setRoleName("patient");
            User user3 = new User("medical", "medical1!");
            Role role3 = new Role();
            role3.setRoleName("medical staff");

            registerUsers.put(user1.getName(), user1);
            registerUsers.put(user2.getName(), user2);
            registerUsers.put(user3.getName(), user3);

            access_list.put(user1.getName(), role1);
            access_list.put(user2.getName(), role2);
            access_list.put(user3.getName(), role3);
            //----------------------------------------------------
        }

    /**
     * A method that generates a random one-time password.
     * @param len : the length of the one-time password.
     * @return
     * @throws RemoteException
     */
    public char[] generate_password(int len) throws RemoteException
    {
        //possible characters for the password.
        String possible_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "!@#$%^&*_=+-/.?<>)";


        Random rnd = new Random();
        //New char array that will contain 8symbol password.
        char[] password = new char[len];

        //take random(len amount) chars from possible_characters and fill up the OTP.
        for(int i=0; i<len; i++)
        {
            password[i] = possible_chars.charAt(rnd.nextInt(possible_chars.length()));
        }
		
		logs.addLog("CLIENT " + (sknValues.size()-1), "create", "Created one time password");
        return password;
    }

    /**
     * A method that checks if such an account exists. If not it creates a new one and saves it.
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */
    public synchronized boolean registerAccount(String username, String password, Role role) throws RemoteException{
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

            //Add the user and his assigned role to the access list.

            access_list.put(username, role);

            //Tests..
            System.out.println(registerUsers.get(user.getName()).getName());
            System.out.println(registerUsers.get(user.getName()).getPassword());
            System.out.println(registerUsers.get(user.getName()).getId());
            System.out.println(access_list.get(username).getRoleName());
            logs.addLog(username, "create", "created user");
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
                logs.addLog(username, "login", "Successfull Login");
                return true;
            }
            else {
				System.out.println("Username exists, password doesn't match");
				logs.addLog(username, "login", "Wrong Password");
			}
        }
        else System.out.println("Username doesn't exist");

        //Return false to the client.
        return false;
    }

    /**
     * Method which authorizes if the user is allowed to do an operation.
     * Returns True if the user is authorized and False if the user is not authorized.
     * @param username
     * @param operation
     * @return
     * @throws RemoteException
     */
    public boolean roleAuthorization(String username, String operation) throws RemoteException{
        System.out.println("roleAuthorization method");

        if(access_list.get(username).getRoleName().equals("patient"))
        {
            if(operation.equals("r")){
                logs.addLog(username, "authorization", "Authorized Patient");
                return true;
            }else 
            {	logs.addLog(username, "authorization", "Unauthorized");
				 return false;
			}
        }
        else if(access_list.get(username).getRoleName().equals("medical staff"))
        {
			logs.addLog(username, "authorization", "Authorized Medical Staff");
            return true;
        }
        else if(access_list.get(username).getRoleName().equals("regulator"))
        {
			logs.addLog(username, "authorization", "Authorized Regulator");
            return true;
        }
        else {
			logs.addLog(username, "authorization", "Unauthorized");
			 return false;
		 }
    }

    /**
     * Method that displays all records.
     * @return String containing the information of every record.
     * @throws RemoteException
     */
    public String display_records()throws RemoteException
    {
        StringBuilder toreturn = new StringBuilder();
        for(Map.Entry<Integer, Record> entry : records_list.entrySet())
        {
            Integer id = entry.getKey();
            Record rec = entry.getValue();


            String s = id + " : " + rec.getRecord_name() + " : " + rec.getUsername();

            toreturn.append("\n").append(s);
        }
        return toreturn.toString();
    }

    /**
     * Method to display only 1 record
     * @param id The id of the record that should be displayed.
     * @return A string containing all information in the record.
     * @throws RemoteException
     */
    public String display_one_record(Integer id) throws RemoteException
    {
           String toretrun = ("Record name: " + records_list.get(id).getRecord_name()
            + "\n" + "Record information: " + records_list.get(id).getInformation()
            + "\n" + "This record is for user: " + records_list.get(id).getUsername());
//        try {
//            toretrun = ("Record name: " + records_list.get(id).getRecord_name()
//                    + "\n" + "Record information: " + records_list.get(id).getInformation()
//                    + "\n" + "This record is for user: " + records_list.get(id).getUsername());
//        }catch (NullPointerException | NumberFormatException ex)
//        {
//            toretrun = "No record with this ID";
//        }
//
//        return toretrun;
        return toretrun;
    }

    /**
     * A method that creates a record with the given parameters. Records are saved in records_list
     * @param username
     * @param record_name
     * @param record_info
     * @return True if the record is created successfully.
     * @throws RemoteException
     */
    public boolean create_record(String username, String record_name, String record_info) throws RemoteException
    {
        boolean does_user_exist = false;
        //Check if the user for whom the record is, exists.
        for(String name : registerUsers.keySet())
        {
            if(name.equals(username))
            {
                does_user_exist=true;
            }
        }

        if(does_user_exist)
        {
            Record rec = new Record(username, record_name, record_info);
            records_list.put(rec_counter, rec);
            System.out.println("New record created: " + records_list.get(1).getRecord_name());


            rec_counter++;
            return true;
        }
        else return false;
    }


    //Returns the record_list
    public HashMap<Integer, Record> getRecords_list() throws RemoteException
    {
        return records_list;
    }

    /**
     * A method to get a record with a username as parameter.
     * @param username
     * @return The record corresponding to the given username.
     * @throws RemoteException
     */
    public Record getRecord(String username) throws RemoteException
    {

        for(Record value : records_list.values())
        {
            if(value.getUsername().equals(username))
            {
                return value;
            }
        }
    return null;
    }

    /**
     * A method to update a record, given the id and new information.
     * @param id
     * @param new_info
     * @throws RemoteException
     */
    public String updateRecord(Integer id,String new_info) throws RemoteException
    {
        try {
            records_list.get(id).setInformation(new_info);
            return "Update successful.";
        }catch (NullPointerException | NumberFormatException ex)
        {
            return "No record with such ID";
        }
    }

    /**
     * A method to delete a record, given the id.
     * @param id
     * @throws RemoteException
     */
    public void deleteRecord(Integer id) throws RemoteException
    {
        records_list.remove(id);
    }

    //Get the role of a user
    public String get_cur_user_role(String username) throws RemoteException{
        return access_list.get(username).getRoleName();
    }

    //Get the user object based on the key(username for now)
    public User get_cur_user(String username) throws RemoteException{
        return registerUsers.get(username);
    }

//Get the anonymity user
    public User get_anonymity_user(String username) throws RemoteException{
        User user=registerUsers.get(username);
        user.setName("anonymity");
       return user;

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
        logs.addLog("CLIENT " + (sknValues.size()-1), "connect", "requested connection");
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
