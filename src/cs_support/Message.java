package cs_support;

import com.sun.deploy.util.ArrayUtil;
import com.sun.istack.internal.Nullable;
import crypto.AES;
import crypto.GOST;
import crypto.GammaForGOST_Parallel;
import crypto.RSA;
import sun.security.provider.SecureRandom;

import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Formats of message:
 *  - All: messageCode(0), messageNumber(1-2), userName(3-34);
 *  - Get_PK: all;
 *  - Get_PK_USER: all + ip;
 */

public class Message {
    //-----Constants begin----------
    public final static int C216 = 65536;

    public final static byte GET_MESSAGE = -1;
    public final static byte GET_PK = 0;
    public final static byte INCOM_PK = 1;
    public final static byte GET_PK_USER = 2;
    public final static byte INCOM_PK_USER = 3;
    public final static byte GET_LIST_USER = 4;
    public final static byte INCOM_LIST_USER = 5;
    public final static byte GET_STATUS = 6;
    public final static byte INCOM_STATUS = 7;
    public final static byte SEND_FILE = 8;
    public final static byte INCOM_FILE = 8;
    //-----Constants end------------

    private byte[] message;
    private byte[] messageNumber = new byte[2];
    private byte[] userName = new byte[32];
    private Key secretKey;
    private Key publicKey;


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public Message(){

    }

    public Message(String userName, Key publicKey) {
        this.publicKey = publicKey;
        setUsername(userName);
    }

    public Message(byte[] message, Key secretKey) {
        this.message = message;
        this.secretKey = secretKey;
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  Getters/Setters
    /////////////////////////////////////////////////////////
    //-----Getters/Setters begin----

    public byte[] getMessage(byte messageCode, @Nullable String ip, @Nullable byte[] fileMessage) {
        if(messageCode > GET_MESSAGE) createMessage(messageCode, ip, fileMessage);
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public int getMessageNumber() {
        int mn = 0;
        mn |= this.messageNumber[1];
        mn <<= 8;
        mn |= this.messageNumber[0];

        return mn;
    }

    public void setMessageNumber(int mn) {
        this.messageNumber[0] = (byte)(mn % 256);
        this.messageNumber[1] = (byte)((mn >>> 8) % 256);
    }

    public String getUsername() {
        int k = 0;

        for (int i = 31; i > -1; i--){
            if(this.userName[i] != 0){
                k = i + 1;
                break;
            }
        }

        byte[] bytes = new byte[k];
        System.arraycopy(bytes, 0, this.userName, 0, k);

        return new String(bytes);
    }

    public void setUsername(String username) {
        byte[] bytes = username.getBytes();

        for (int i = 0; i < 32; i++){
            if(i < bytes.length){
                this.userName[i] = bytes[i];
            }else this.userName[i] = 0;
        }
    }

    public Key getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    //-----Getters/Setters end------


    //////////////////////////////////////////////////////////
    ///  Method createMessage
    /////////////////////////////////////////////////////////
    private void createMessage(byte messageCode, @Nullable String ip, @Nullable byte[] fileMessage){
//        SecureRandom random = new SecureRandom();
        RSA rsa = new RSA();
        byte[] bytes;

        switch (messageCode){
            case GET_PK:                 //---GET_PK
//                random.engineNextBytes(this.messageNumber);
                bytes = getBasicMessage(GET_PK, true);
                byte[] bytes1 = new byte[32];
                Arrays.fill(bytes1, (byte) 0);
                System.arraycopy(bytes, 3, bytes1, 0, bytes1.length);
                this.message = bytes;
                break;
            case GET_PK_USER:
                bytes = getBasicMessage(GET_PK_USER, true);
                byte[] ipBytes = ip.getBytes();
                bytes = addByteArray(bytes, ipBytes);
                this.message = rsa.encrypt(bytes, publicKey);
                break;
            case GET_LIST_USER:
                bytes = getBasicMessage(GET_LIST_USER, true);
                this.message = rsa.encrypt(bytes, publicKey);
                break;
            case GET_STATUS:
                bytes = getBasicMessage(GET_STATUS, true);
                this.message = rsa.encrypt(bytes, publicKey);
                break;
            case SEND_FILE:
                bytes = getBasicMessage(SEND_FILE, true);
                bytes = addByteArray(bytes, fileMessage);
                this.message = rsa.encrypt(bytes, publicKey);
                break;
            default:
                break;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Method getBasicMessage
    /////////////////////////////////////////////////////////
    private byte[] getBasicMessage(byte messageCode, boolean isUpdateMessageNumber){
        if(isUpdateMessageNumber){
            new SecureRandom().engineNextBytes(this.messageNumber);
        }

        byte[] bytes = new byte[35];
        bytes[0] = messageCode;
        System.arraycopy(bytes, 1, this.messageNumber, 0, 2);
        System.arraycopy(bytes, 3, this.userName, 0, 32);
        return bytes;
    }



    //////////////////////////////////////////////////////////
    ///  Method addByteArray
    /////////////////////////////////////////////////////////
    public byte[] addByteArray(byte[] bytes, byte[] bytes1){
        byte[] rez = new byte[bytes.length + bytes1.length];

        System.arraycopy(rez, 0, bytes, 0, bytes.length);
        System.arraycopy(rez, bytes.length, bytes1, 0, bytes1.length);

        return rez;
    }

    //////////////////////////////////////////////////////////
    ///  Methods getFromMessage
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public byte getFromMessageCode(){
//        byte bt = 0;

        if(this.message.length > 0){
          return this.message[0];
        }else return -1;
    }

    public int getFromMessageNumber(){
        if(this.message.length < 3){
            return -1;
        }else {
            byte[] bytes = new byte[2];
            System.arraycopy(bytes, 0, this.messageNumber, 0 , 2);
            System.arraycopy(this.messageNumber, 0, this.message, 1 , 2);
            int num = getMessageNumber();
            System.arraycopy(this.messageNumber, 0, bytes, 0 , 2);
            return num;
        }
    }

    public String getFromMessageUsername(){
        if(this.message.length < 35){
            return null;
        }else {
            byte[] bytes = new byte[32];
            System.arraycopy(bytes, 0, this.userName, 0 , 32);
            System.arraycopy(this.userName, 0, this.message, 3 , 32);
            String name = getUsername();
            System.arraycopy(this.userName, 0, bytes, 0 , 32);
            return name;
        }
    }

    public byte[] getFromMessagePost(){
        if(this.message.length > 35){
            byte[] bytes = new byte[this.message.length - 35];
            System.arraycopy(bytes, 0, this.message, 35, this.message.length - 35);
            return bytes;
        }else return new byte[0];
    }

    public Key getFromMessageKey(@Nullable String cryptoSystem){
        byte[] bytes = getFromMessagePost();

        if(cryptoSystem == null) {
            cryptoSystem = RSA.NAME_OF_RSA;
        }else {
            switch (cryptoSystem) {
                case RSA.NAME_OF_RSA:
                    break;
                case AES.NAME_OF_AES:
                    break;
                default:
                    cryptoSystem = RSA.NAME_OF_RSA;
                    break;
            }
        }

        return new SecretKeySpec(bytes, 0, bytes.length, cryptoSystem);
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Methods util
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public byte[] utilGetKeysForGOST_FromKey(Key key){
        byte[] keys = new byte[8];
        byte[] bytesKey = key.getEncoded();

        if(bytesKey.length == 0){
            Arrays.fill(keys, (byte) 0);
            return keys;
        }


        if (bytesKey.length > 7){
            System.arraycopy(keys, 0, bytesKey, 0, 8);
        }else {
            System.arraycopy(keys, 0, bytesKey, 0, bytesKey.length);
            int k = bytesKey.length;
            while (k < 8){
                for(byte byteKey: bytesKey){
                    keys[k] = (byte)(byteKey + 1);
                    k++;
                    if(k == 8){
                        break;
                    }
                }
            }
        }

        return keys;
    }

    public byte[] utilGetEncryptPost(byte[] openText, long[] keys, int[][] sBox){
        byte[] encryptText = new byte[openText.length];
        List<Long> encryptList = new ArrayList<Long>();
        int k = 0;
        long sp = 0;

        while (k < openText.length){
            List<Long> dataList = new ArrayList<Long>();
            do{
                long item = 0;
                for (int i = 0; i < 8; i++){
                    item |= (long) openText[k] << (8 * i);
                    k++;
                    if(k >= openText.length) break;
                }
                dataList.add(item);
            }while (dataList.size() < 128 && k < openText.length);

            if (sp == 0) {
                GammaForGOST_Parallel gfgp = new GammaForGOST_Parallel(dataList, keys, sBox);
                gfgp.setNumberOfThread(17);
                encryptList.addAll(gfgp.cryptForGama());
                sp = gfgp.getSynchronizedPost();
            }else {
                GammaForGOST_Parallel gfgp = new GammaForGOST_Parallel(dataList, keys, sBox, sp);
                gfgp.setNumberOfThread(17);
                encryptList.addAll(gfgp.cryptForGama());
            }
        }

        k = 0;
        while (k < encryptText.length){
            for(long item: encryptList){
                for(int i = 0; i < 8; i++){
                    encryptText[k] = (byte)((item >>> (8 * i)) % 256);
                    k++;
                    if(k >= encryptText.length) break;
                }
                if(k >= encryptText.length) break;
            }
        }

        byte[] spBytes = new  byte[8];
        for(int i = 0; i < 8; i++) {
            spBytes[i] = (byte) ((sp >>> (8 * i)) % 256);
        }

        return addByteArray(spBytes, encryptText);

    }

    //-----End----------------------
}
