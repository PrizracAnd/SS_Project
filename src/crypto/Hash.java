import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String str = "hello world";

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] bytes = md5.digest(str.getBytes());
        for (byte b : bytes) System.out.print(b);

        // Можно вывести в 16-ричном формате:
        System.out.println("\n");
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes){
            builder.append(String.format("%02X ", b));
        }
        System.out.println(builder.toString());
    }
}
