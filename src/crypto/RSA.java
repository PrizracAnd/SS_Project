package crypto;

import com.sun.istack.internal.Nullable;

import javax.crypto.*;
import java.security.*;
import java.util.List;

public class RSA {
    public final static String NAME_OF_RSA = "RSA";
    private Cipher cipher;
    private String cryptoSystem;

    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public RSA( ) {
        this.cryptoSystem = "RSA";
        try {
            this.cipher = Cipher.getInstance(cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public RSA(@Nullable String cryptoSystem ) {
        if(cryptoSystem == null) this.cryptoSystem = "RSA";
        try {
            this.cipher = Cipher.getInstance(this.cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
    //-----Constructors end-------

    //////////////////////////////////////////////////////////
    ///  PSWM FIXME
    /////////////////////////////////////////////////////////
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String s = "Hello world";
        System.out.println(s + "\n");

        Cipher cipher = Cipher.getInstance("RSA");

        /* Генерация пары ключей */
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");

        SecureRandom random = new SecureRandom();
        pairGenerator.initialize(768, random);

        KeyPair keyPair = pairGenerator.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        /* Зашифрование и вывод */
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(s.getBytes());
        for (byte b : bytes) System.out.print(b);

        /* Расшифрование и вывод */
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes2 = decryptCipher.doFinal(bytes);
        System.out.println("\n");
        for (byte b : bytes2) System.out.print((char)b);

    }


    //////////////////////////////////////////////////////////
    ///  Methods of cryptoSystem
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public String getCryptoSystem() {
        return cryptoSystem;
    }

    public void setCryptoSystem(String cryptoSystem) {
        this.cryptoSystem = cryptoSystem;
        try {
            this.cipher = Cipher.getInstance(cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
    //-----end----------------------


    //////////////////////////////////////////////////////////
    ///  Methods getKeys
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public Key[] getKeys(){
        Key[] keys = new Key[2];

        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance(this.cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        SecureRandom random = new SecureRandom();
        try {
            pairGenerator.initialize(768, random);
            KeyPair keyPair = pairGenerator.generateKeyPair();
            keys[0] = keyPair.getPublic();
            keys[1] = keyPair.getPrivate();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return keys;
    }

    public Key[] getKeys(int keySize){
        Key[] keys = new Key[2];
        if(keySize < 256) keySize = 768;

        KeyPairGenerator pairGenerator = null;
        try {
            pairGenerator = KeyPairGenerator.getInstance(this.cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        SecureRandom random = new SecureRandom();
        try {
            pairGenerator.initialize(keySize, random);
            KeyPair keyPair = pairGenerator.generateKeyPair();
            keys[0] = keyPair.getPublic();
            keys[1] = keyPair.getPrivate();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return keys;
    }
    //-----end----------------------


    //////////////////////////////////////////////////////////
    ///  Method encrypt
    /////////////////////////////////////////////////////////
    public byte[] encrypt (byte[] openText, Key key){
        byte[] bytes = new byte[0];

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            bytes = this.cipher.doFinal(openText);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return bytes;
    }


    //////////////////////////////////////////////////////////
    ///  Method decrypt
    /////////////////////////////////////////////////////////
    public byte[] decrypt (byte[] encryptText, Key key){
        byte[] bytes = new byte[0];

        try {
            this.cipher.init(Cipher.DECRYPT_MODE, key);
            bytes = this.cipher.doFinal(encryptText);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}
