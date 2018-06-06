package crypto;

import com.sun.istack.internal.Nullable;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Keys {
    private Key publicKey;
    private Key secretKey;
    private long id_db;

    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public Keys(){
        this.id_db = -1;
    }

    public Keys(Key publicKey, Key secretKey) {
        this();
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    public Keys(Key publicKey, Key secretKey, long id_db) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
        this.id_db = id_db;
    }
    //-----Constructors end---------



    //////////////////////////////////////////////////////////
    ///  Getters/Setters
    /////////////////////////////////////////////////////////
    //-----Getters/Setters begin----
    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    public void setPublicKeyFromString(String publicKey, @Nullable String cryptoSystem) {
        setPublicKey(convertStringToKey(publicKey, cryptoSystem));
    }

    public Key getSecretKey() {
        return secretKey;
    }

    public String getSecretKeyString(){
        return convertKeyToString(this.secretKey);
    }

    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }

    public void setSecretKeyFromString(String publicKey, @Nullable String cryptoSystem) {
        setSecretKey(convertStringToKey(publicKey, cryptoSystem));
    }

    public long getId_db() {
        return id_db;
    }

    public void setId_db(long id_db) {
        this.id_db = id_db;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyString(){
        return convertKeyToString(this.publicKey);
    }
    //-----Getters/Setters end------


    //////////////////////////////////////////////////////////
    ///  Methods convertKey
    /////////////////////////////////////////////////////////
    //-----convertKey begin---------
    private String convertKeyToString(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private Key convertStringToKey(String str, @Nullable String cryptoSystem){
        byte[] bytes = Base64.getDecoder().decode(str);

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
    //-----convertKey end-----------
}
