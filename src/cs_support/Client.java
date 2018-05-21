import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;


/* Нужно дописать, чтобы можно было вводить данные при запущенном сервере */
public class Client {
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9000), 2000);

           InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());       // ?
           Scanner scanner = new Scanner(inputStreamReader);                                           // ?


            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
}
