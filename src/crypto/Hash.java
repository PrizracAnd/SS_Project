package crypto;

import com.sun.istack.internal.Nullable;
import org.omg.CORBA.portable.UnknownException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public final static String MD5 = "MD5";
    public final static String SHA1 = "SHA-1";
    public final static String SHA256 = "SHA-256";
    public final static String SHA512 = "SHA-512";

    private MessageDigest messageDigest;
    private String cryptoSystem;


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------

    public Hash() {
        this.cryptoSystem = SHA256;
        try {
            this.messageDigest = MessageDigest.getInstance(this.cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Hash(@Nullable String cryptoSystem) {
        if (cryptoSystem == null){
            this.cryptoSystem = SHA256;
        } else{
            this.cryptoSystem = cryptoSystem;
        }
        try {
            this.messageDigest = MessageDigest.getInstance(this.cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  PSWM FIXME
    /////////////////////////////////////////////////////////
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String str = "hello world hello world hello world hello world";

        MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] bytes = sha1.digest(str.getBytes());
        for (byte b : bytes) System.out.print(b);

        // Можно вывести в 16-ричном формате:
        System.out.println("\n");
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes){
            builder.append(String.format("%02X ", b));
        }
        System.out.println(builder.toString());
    }


    //////////////////////////////////////////////////////////
    ///  Method getHash
    /////////////////////////////////////////////////////////

    public byte[] getHash(byte[] text){
        byte[] bytes = new byte[0];

        try {
            bytes = this.messageDigest.digest(text);
        }catch (UnknownException e){
            e.printStackTrace();
        }

        return bytes;
    }
}
