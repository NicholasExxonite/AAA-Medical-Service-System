import java.util.HashMap;
import java.util.Arrays;
import java.io.*;

public class LogsList 
{
    private HashMap<Integer, Log> logs;

    /**
     * Create a new loglist, or load one if it already exists. also ensure the list has not been tampered with.
     */
    public LogsList()
    {
        try 
        {
            File logFile = new File("SavedLogs.ser");
            if(logFile.exists())
            {
                FileInputStream fis = new FileInputStream(logFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                logs = (HashMap) ois.readObject();
                ois.close();
                fis.close();
            }
            else
            {
                //create a new log if not found
                logs = new HashMap<Integer, Log>();
                logs.put(1, new Log("-Server-", "Start", "New log list created", "FirstEntry".getBytes()));
                //save it into a file
                FileOutputStream fos = new FileOutputStream(logFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(logs);
                oos.close();
                fos.close();
            }
        }
        catch (IOException e)
        {
            System.out.println();
            System.out.println("IOException :");
            System.out.println(e);
        }
        catch (ClassNotFoundException e)
        {
            System.out.println();
            System.out.println("ClassNotFoundException :");
            System.out.println(e);
        }
        //Print logs
        System.out.println(this);
        //verify all logs against previous hash
        for(int i = 2; i <= logs.size(); i++)
        {
            if(!(Arrays.equals(logs.get(i).getPrevHash(), logs.get(i-1).hash())))
            {
                //tampering has occured!
                System.out.println("\n\nTampering may have occured with the following log \n" + i + "  -  " + logs.get(i));
            }
        }
    }

    /**
     * Adds a new log (example - 06/02/2021 18:23:11 : John : Edit : Added patient "Dave")
     * @param userName the username of the person who triggered a log event
     * @param messageType the type of log event
     * @param message details of the log event
     */
    public void addLog(String userName, String messageType, String message)
    {
        //create the log message
        logs.put(logs.size() + 1, new Log(userName, messageType, message, logs.get(logs.size()).hash()));
        //update the saved file
        try 
        {
            FileOutputStream fos = new FileOutputStream(new File("SavedLogs.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(logs);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            System.out.println();
            System.out.println("IOException :");
            System.out.println(e);
        }
        //print log
        System.out.println("\n " + logs.size() + "  -  " + logs.get(logs.size()));
    }

    /**
     * prints the entire log list as a string
     * @return the formatted logs
     */
    public String toString()
    {
        String output = "\n";
        for(int i = 1; i <= logs.size(); i++)
        {
            output = output + "\n " + i + "  -  " + logs.get(i);
        }
        return output;
    }

}