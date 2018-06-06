package crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AES {
    public final static String NAME_OF_AES = "AES";

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String s = "Hello world";
        System.out.println(s + "\n");

        Cipher cipher = Cipher.getInstance("AES");

        /* один из способов генерации ключа*/
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey key = kgen.generateKey();

        /* другой способ - передаем свой ключ */
//      SecretKeySpec key2 = new SecretKeySpec("Abc12345Bac54321".getBytes(), "AES");

        /* Зашифрование и вывод */
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(s.getBytes());
        for (byte b : bytes) System.out.print(b);

        /* Расшифрование и вывод */
        Cipher decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes2 = decryptCipher.doFinal(bytes);
        System.out.println("\n");
        for (byte b : bytes2) System.out.print((char)b);

    }
}
