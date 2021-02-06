import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.io.Serializable;

public class Log  implements Serializable
{
    private String logTime;
    private String userName;
    private String logMessageType;
    private String logMessage;
    private byte[] previousHashDigest;

    /**
     * sets the values for a log
     * @param user the username of the person who triggered a log event
     * @param messageType the type of log event
     * @param message details of the log event
     * @param previousDigest the hash digest of the previous log
     */
    public Log(String user, String messageType, String message, byte[] previousDigest)
    {
        DateFormat dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        logTime = dateTime.format(date); 
        userName = user;
        logMessageType = messageType;
        logMessage = message;
        previousHashDigest = previousDigest;
    }

    /**
     * formats the log as a string
     * @return the formatted log
     */
    public String toString()
    {
        return(logTime + " : " + userName + " : " + logMessageType + " : " + logMessage);
    }

    /**
     * returns the hash digest for the current log
     */
    public byte[] hash()
    {
        try 
        {
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = ByteBuffer.allocate(logTime.getBytes().length + userName.getBytes().length + logMessageType.getBytes().length + logMessage.getBytes().length + previousHashDigest.length);
            buffer.put(logTime.getBytes());
            buffer.put(userName.getBytes());
            buffer.put(logMessageType.getBytes());
            buffer.put(logMessage.getBytes());
            buffer.put(previousHashDigest);
            byte[] fullMessage = buffer.array();
            digester.update(fullMessage);
            return digester.digest();
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println();
            System.out.println("NoSuchAlgorithmException :");
            System.out.println(e);
        }
        return null;
    }

    /**
     * returns the hash digestfor the previous log
     */
    public byte[] getPrevHash()
    {
        return previousHashDigest;
    }
}