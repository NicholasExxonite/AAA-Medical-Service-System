
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.nio.ByteBuffer;

public class Converter 
{


    public Converter()
    {

    }

    /**
     * Encrypts a message
     * @param input the plain message
     * @param key the secret key
     * @return the encrypted message
     */
    public byte[] encrypt(byte[] input, SecretKey key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            cipher.update(input);
            return (cipher.doFinal());
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println();
            System.out.println("NoSuchAlgorithmException :");
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
        catch(InvalidAlgorithmParameterException e)
        {
            System.out.println();
            System.out.println("InvalidAlgorithmParameterException :");
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
        return null;
    }

    /**
     * Decrypts a message
     * @param input the encrypted message
     * @param key the secret key
     * @return the decrypted message
     */
    public byte[] decrypt(byte[] input, SecretKey key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            return (cipher.doFinal(input));
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println();
            System.out.println("NoSuchAlgorithmException :");
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
        catch(InvalidAlgorithmParameterException e)
        {
            System.out.println();
            System.out.println("InvalidAlgorithmParameterException :");
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
        return null;
    }

    /**
     * Converts a byte array into a long number
     * @param input the byte array
     * @param size the number of bytes to use
     * @return the converted long
     */
    public long byteArrayToLong(byte[] input, int size)
    {
        ByteBuffer buff = ByteBuffer.allocate(size);
        buff.put(input);
        buff.flip();
        return buff.getLong();
    }

    /**
     * Converts a long number into a byte array
     * @param input the long number
     * @param size the number of bytes to use
     * @return the converted byte array
     */
    public byte[] longToByteArray(long input, int size)
    {
        ByteBuffer buff = ByteBuffer.allocate(size);
        buff.putLong(input);
        return buff.array();
    }

    /**
     * Converts an int to an encrypted byte array
     * @param input the variable being encrypted
     * @param key the key used to encrypt it
     * @return the encrypted variable as a byte array
     */
    public byte[] encryptInt(int input, SecretKey key)
    {
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES);
        buff.putInt(input);
        return (encrypt(buff.array(),key));
    }
    
    /**
     * Converts an encrypted byte array to an int
     * @param input the array being decrypted
     * @param key the key used to decrypt it
     * @return the decrypted variable
     */
    public int decryptInt(byte[] input, SecretKey key)
    {
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES);
        buff.put(decrypt(input, key));
        buff.flip();
        return buff.getInt();
    }

    /**
     * Converts a string to an encrypted byte array
     * @param input the variable being encrypted
     * @param key the key used to encrypt it
     * @return the encrypted variable as a byte array
     */
    public byte[] encryptString(String input, SecretKey key)
    {
        return encrypt(input.getBytes(), key);
    }
    
    /**
     * Converts an encrypted byte array to a string
     * @param input the array being decrypted
     * @param key the key used to decrypt it
     * @return the decrypted variable
     */
    public String decryptString(byte[] input, SecretKey key)
    {
        byte[] decrypted = decrypt(input, key);
        return(new String(decrypted));
    }
}