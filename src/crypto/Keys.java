package crypto;

import java.security.Key;

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


    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    public Key getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }

    public long getId_db() {
        return id_db;
    }

    public void setId_db(long id_db) {
        this.id_db = id_db;
    }
}
