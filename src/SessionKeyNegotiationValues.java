import javax.crypto.SecretKey;
import java.security.PublicKey;

public class SessionKeyNegotiationValues 
{

    private SecretKey sessionKey;
    private long clientRand;
    private long serverRand;
    private long secretRand;
    private boolean clientFinished = false;
    

    public SessionKeyNegotiationValues()
    {

    }

    public SecretKey getKey()
    {
        return sessionKey;
    }

    public long getClientRandom()
    {
        return clientRand;
    }

    public long getServerRandom()
    {
        return serverRand;
    }

    public long getSecretRandom()
    {
        return secretRand;
    }

    public boolean isClientFinished()
    {
        return clientFinished;
    }

    public void setKey(SecretKey input)
    {
        sessionKey = input;
    }

    public void setClientRandom(long input)
    {
        clientRand = input;
    }

    public void setServerRandom(long input)
    {
        serverRand = input;
    }

    public void setSecretRandom(long input)
    {
        secretRand = input;
    }

    public void clientIsFinished()
    {
        clientFinished = true;
    }

    public long getTotalRandom()
    {
        return (clientRand + serverRand + secretRand);
    }

    
}