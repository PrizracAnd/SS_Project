import javax.crypto.*;
import java.security.*;

public class RSA {
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
}
