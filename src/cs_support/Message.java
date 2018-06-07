package cs_support;

import com.sun.deploy.util.ArrayUtil;
import com.sun.istack.internal.Nullable;
import crypto.*;
import db_support.Account;
import sun.security.provider.SecureRandom;

import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Formats of messages:
 *  - All: messageCode(0), messageNumber(1-2), userName(3-34);
 *  - Get_PK: all;
 *  - Send_PK: all + PK
 *  - Get_PK_USER: all + ip;
 *  - Send_PK_USER: all + PK;
 *  - Get_LIST_USER: all;
 *  - SEND_LIST_USER: all + Post: [username - 32 bt, ip - 30 bt],[--||--], ... , [--||--];
 *  - Get_STATUS: all;
 *  - Send_STATUS: all;
 *  - Send_FILE: all + Post: encryptFile
 */

public class Message {
    //-----Constants begin----------
    public final static int C216 = 65536;

    public final static byte GET_MESSAGE = -1;
    public final static byte GET_PK = 0;
    public final static byte SEND_PK = 1;
    public final static byte INCOM_PK = 1;
    public final static byte GET_PK_USER = 2;
    public final static byte SEND_PK_USER = 3;
    public final static byte INCOM_PK_USER = 3;
    public final static byte GET_LIST_USER = 4;
    public final static byte SEND_LIST_USER = 5;
    public final static byte INCOM_LIST_USER = 5;
    public final static byte GET_STATUS = 6;
    public final static byte SEND_STATUS = 7;
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
        decryptMessage();
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  Getters/Setters
    /////////////////////////////////////////////////////////
    //-----Getters/Setters begin----

    public byte[] getMessage(byte messageCode, @Nullable String ip, @Nullable byte[] messagePost) {
        if(messageCode > GET_MESSAGE) createMessage(messageCode, ip, messagePost);
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
        decryptMessage();
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
    private void createMessage(byte messageCode, @Nullable String ip, @Nullable byte[] messagePost){
//        SecureRandom random = new SecureRandom();
        RSA rsa = new RSA();
        byte[] bytes;

        switch (messageCode){
            case GET_PK:                 //---GET_PK

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

            case SEND_PK:

                bytes = getBasicMessage(SEND_PK, true, messagePost);
                this.message = rsa.encrypt(bytes, publicKey);

                break;

            case SEND_PK_USER:

                bytes = getBasicMessage(SEND_PK_USER, true, messagePost);
                this.message = rsa.encrypt(bytes, publicKey);

                break;
            case SEND_LIST_USER:

                bytes = getBasicMessage(SEND_LIST_USER, true, messagePost);
                this.message = rsa.encrypt(bytes, publicKey);

                break;
            case SEND_STATUS:

                bytes = getBasicMessage(SEND_STATUS, true);
                this.message = rsa.encrypt(bytes, publicKey);

                break;

            case SEND_FILE:

                bytes = getBasicMessage(SEND_FILE, true, messagePost);
                byte[] hashBytes = (new Hash()).getHash(bytes);
                bytes = addByteArray(bytes, hashBytes);
                bytes = rsa.encrypt(bytes, publicKey);
                this.message = bytes;

                break;

            default:
                break;
        }
    }


    //////////////////////////////////////////////////////////
    ///  Methods getBasicMessage
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
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

    private byte[] getBasicMessage(byte messageCode, boolean isUpdateMessageNumber, byte[] messagePost){
        if(isUpdateMessageNumber){
            new SecureRandom().engineNextBytes(this.messageNumber);
        }

        byte[] bytes = new byte[35];
        bytes[0] = messageCode;
        System.arraycopy(bytes, 1, this.messageNumber, 0, 2);
        System.arraycopy(bytes, 3, this.userName, 0, 32);
        return addByteArray(bytes, messagePost);
    }
    //-----End----------------------

    //////////////////////////////////////////////////////////
    ///  Method decryptMessage
    /////////////////////////////////////////////////////////
    private void decryptMessage(){
        RSA rsa = new RSA();
        this.message = rsa.decrypt(this.message, this.secretKey);
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

    public List<Account> getFromMessageUserList(){
        List<Account> accountList = new ArrayList<Account>();

        if(this.message.length > 35) {
            int k = 35;
            while ((k + 62) <= this.message.length){
                byte[] bytes = new byte[32];
                System.arraycopy(bytes, 0, this.message, k, 32);
                int l = 0;
                for (int i = 31; i > -1; i--){
                    if(bytes[i] != 0){
                        l = i + 1;
                        break;
                    }
                }
                System.arraycopy(bytes, 0, bytes, 0, l);
                Account account = new Account(new String(bytes));
                k += 32;

                System.arraycopy(bytes, 0, this.message, k, 30);
                l = 0;
                for (int i = 29; i > -1; i--){
                    if(bytes[i] != 0){
                        l = i + 1;
                        break;
                    }
                }
                System.arraycopy(bytes, 0, bytes, 0, l);
                account.setIpAddress(new String(bytes));

                accountList.add(account);
            }
        }

        return accountList;
    }

    public byte[] getFromMessageEncryptPost() {
        byte[] bytes = getFromMessagePost();
        if (bytes.length > 32) {
            System.arraycopy(bytes, 0, bytes, 0, bytes.length - 32);
        }else bytes = new byte[0];

        return bytes;
    }

    public boolean getFromMessageIsHashOk(){
        boolean l = false;
        if(this.message.length > 67){
            byte[] hashBetes = new  byte[32];
            System.arraycopy(hashBetes, 0, this.message, this.message.length - 32, 32);
            byte[] bytes = new byte[this.message.length - 32];
            System.arraycopy(bytes, 0, this.message, 0, this.message.length - 32);
            l = Arrays.equals(hashBetes,(new Hash()).getHash(bytes));
        }

        return l;
    }
    //-----End----------------------


    //////////////////////////////////////////////////////////
    ///  Methods util
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public long[] utilGetKeysForGOST_FromKey(Key key){
        long[] keys = new long[8];
        Arrays.fill(keys, (long) 0);
        byte[] bytesKey = key.getEncoded();
        byte[] bytes = new byte[64];

        if(bytesKey.length == 0){
            return keys;
        }


        if (bytesKey.length >= 64){
            System.arraycopy(bytes, 0, bytesKey, 0, 64);
        }else {
            System.arraycopy(bytes, 0, bytesKey, 0, bytesKey.length);
            int k = bytesKey.length;
            while (k < 64){
                for(byte byteKey: bytesKey){
                    bytes[k] = (byte)(byteKey + 1);
                    k++;
                    if(k == 64){
                        break;
                    }
                }
            }
        }

        int k = 0;
        for(long item: keys){
            for (int i = 0; i < 8; i++){
                if(k >= bytes.length) break;
                item |= (long)bytes[k] << (i * 8);
                k++;
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

    public byte[] utilGetDecpritTextFromPost(byte[] encryptText, long[] keys, int[][] sBox){
        byte[] openText = new byte[0];
        List<Long> decryptList = new ArrayList<Long>();

        if(encryptText.length > 8){
            long sp = 0;
            for (int i =0; i < 8; i++){
                sp |= (long)encryptText[i] << (i * 8);
            }

            int k = 8;
            while (k < encryptText.length) {
                List<Long> dataList = new ArrayList<Long>();
                do {
                    long item = 0;
                    for (int i = 0; i < 8; i++) {
                        item |= (long) encryptText[k] << (8 * i);
                        k++;
                        if (k >= encryptText.length) break;
                    }
                    dataList.add(item);
                } while (dataList.size() < 128 && k < encryptText.length);

                GammaForGOST_Parallel gfgp = new GammaForGOST_Parallel(dataList, keys, sBox, sp);
                gfgp.setNumberOfThread(17);
                decryptList.addAll(gfgp.cryptForGama());
            }

            openText = new byte[encryptText.length - 8];
            k = 0;
            while (k < openText.length){
                for(long item: decryptList){
                    for(int i = 0; i < 8; i++){
                        openText[k] = (byte)((item >>> (8 * i)) % 256);
                        k++;
                        if(k >= openText.length) break;
                    }
                    if(k >= openText.length) break;
                }
            }

        }

        return openText;
    }

    public byte[] utilGetMessageFromUserList(List<Account> accounts){
        byte[] bytes = new byte[62 * accounts.size()];

        int k = 0;

        for(Account item: accounts){
            byte[] bt = item.getUserName().getBytes();
            System.arraycopy(bytes, k, bt, 0, (bt.length > 32) ? 32 : bt.length);
            k += 32;

            bt = item.getIpAddress().getBytes();
            System.arraycopy(bytes, k, bt, 0, (bt.length > 30) ? 30 : bt.length);
            k += 30;
        }

        return bytes;
    }



    //-----End----------------------
}
