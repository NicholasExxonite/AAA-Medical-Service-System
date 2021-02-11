import java.util.HashMap;
import java.util.Arrays;
import java.io.*;

public class LogsList 
{
    private HashMap<Integer, Log> logs;
    private static final String MAINLOGS = "logfiles" + File.separator + "mainLogs.ser";
    private static final String ABANDONEDLOG = "logfiles" + File.separator + "_lastAbandonedLogs.ser";
    private static final String BACKUPLOGS = "logfiles" + File.separator + "_backupLog";


    /**
     * Create a new loglist, or load one if it already exists. also ensure the list has not been tampered with.
     */
    public LogsList()
    {
        try 
        {
            File logFile = new File(MAINLOGS);
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
                //make a folder for logs
                File file = new File("logfiles");
                file.mkdir();
                //create a new log if not found
                logs = new HashMap<Integer, Log>();
                logs.put(1, new Log("-Server-", "Start", "New log list created", "FirstEntry".getBytes()));
                //save it into a file
                saveLog(MAINLOGS, logs);
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
        //if not successful try to load a backup
        if(logs == null)
        {
            System.out.println("Logs corrupted, attempting to load latest backup");
            //load the corrupted file and save it as the last removed logs
            try 
            {
                //load the corrupted file
                File file = new File(MAINLOGS);
                byte[] corruptFile = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(corruptFile);
                fis.close();
                //replace the current file with a backup
                logs = new HashMap<Integer, Log>();
                logs.put(1, new Log("-Server-", "Start", "New log list created", "FirstEntry".getBytes()));
                selectLatestBackup();
                //save old corrupted file for potential inspection
                FileOutputStream fos = new FileOutputStream(new File(ABANDONEDLOG));
                fos.write(corruptFile);
                fos.close();
            }
            catch (IOException e)
            {
                System.out.println();
                System.out.println("IOException :");
                System.out.println(e);
            }
        }
        else
        {
            //Print logs
            System.out.println(this);
            boolean tampered = false;
            //verify all logs against previous hash
            for(int i = 2; i <= logs.size(); i++)
            {
                if(!(Arrays.equals(logs.get(i).getPrevHash(), logs.get(i-1).hash())))
                {
                    //tampering has occured!
                    System.out.println("\n\nTampering may have occured with the following log \n" + i + "  -  " + logs.get(i));
                    tampered = true;
                }
            }
            //sugest switching to a backup if tampered
            if(tampered)
            {
                selectLatestBackup();
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
        saveLog(MAINLOGS, logs);
        //create a new backup
        saveLog(BACKUPLOGS + logs.size() + ".ser", logs);
        
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

    /**
     * find the latest backup that has not been tampered with
     */
    private void selectLatestBackup()
    {
        
            HashMap<Integer, Log> testLog = new HashMap<Integer, Log>();
            int current = logs.size() + 1;
            boolean tampered = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //if the the log size is 1 (meaning the file was likely corrupted, make current higher for checking)
            if(current == 2)
            {
                current = 1000;
            }
            //repeat until an untampered backup is found
            while(tampered)
            {
                tampered = false;
                //find latest backup
                File logFile = new File("");
                while((!(logFile.exists())) && (current > 0))
                {
                    current--;
                    logFile = new File(BACKUPLOGS + current + ".ser");
                }
                //if no valid backup found
                if(current < 0)
                {
                    try
                    {
                        System.out.println("\nNo backups exist");
                        //reset logs?
                        System.out.println("\nReseting will delete the current log \n\nReset logs? <Y/N>");
                        String choice = reader.readLine();
                        if (choice.equals("Y"))
                        {
                            //save deleted log for inspection
                            saveLog(ABANDONEDLOG, logs);
                            //create a new log
                            logs = new HashMap<Integer, Log>();
                            logs.put(1, new Log("-Server-", "Start", "New log list created", "FirstEntry".getBytes()));
                            saveLog(MAINLOGS, logs);
                            System.out.println("Logs Reset");
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println();
                        System.out.println("IOException :");
                        System.out.println(e);
                        tampered = true;
                    }
                }
                else
                {
                    try
                    {
                        //set as current templog
                        FileInputStream fis = new FileInputStream(logFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        testLog = (HashMap) ois.readObject();
                        ois.close();
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        System.out.println();
                        System.out.println("IOException :");
                        System.out.println(e);
                        tampered = true;
                    }
                    catch (ClassNotFoundException e)
                    {
                        System.out.println();
                        System.out.println("ClassNotFoundException :");
                        System.out.println(e);
                        tampered = true;
                    }
                    //verify current log
                    for(int i = 2; i <= testLog.size(); i++)
                    {
                        if(!(Arrays.equals(testLog.get(i).getPrevHash(), testLog.get(i-1).hash())))
                        {
                            //tampering has occured!
                            tampered = true;
                        }
                    }
                }
            }
            System.out.println("\n Latest untampered log : " + current);
            //set as current log?
            String choice = "Y";
            //if not being caused by failing to read the log file, allow server to have choice about backing up
            if(logs.size() > current)
            {
                try
                {
                    System.out.println("\n Setting this as your current log will delete the last " + (logs.size() - current) + " entries.\n\nSet as current log? <Y/N>");
                    choice = reader.readLine();
                }
                catch (IOException e)
                {
                    System.out.println();
                    System.out.println("IOException :");
                    System.out.println(e);
                    tampered = true;
                }
            }
            if(choice.equals("Y"))
            {
                //save deleted log for inspection
                saveLog(ABANDONEDLOG, logs);
                //load log
                logs = testLog;
                //save it into a file
                saveLog(MAINLOGS, logs);
                System.out.println("Backup loaded");
            }
            System.out.println(this);
        
    }

    /**
     * saves the hashmap as a file
     * @param filename the name of the file
     * @param loglist the hashmap to save
     */
    private void saveLog(String filename, HashMap loglist)
    {
        try 
        {
            File logFile = new File(filename);
            FileOutputStream fos = new FileOutputStream(logFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(loglist);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            System.out.println();
            System.out.println("IOException :");
            System.out.println(e);
        }
    }
}