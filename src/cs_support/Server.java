import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true){
                Socket socket = serverSocket.accept();              // принимаем запросы
                new Thread(new MyServer(socket)).start();           // для каждого запроса - новый поток
            }
        }
    }
}

class MyServer implements Runnable{
    Socket socket;

    public MyServer (Socket socket) {this.socket = socket;}

    @Override
    public void run(){
        try (Scanner scanner = new Scanner (socket.getInputStream()) ) {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("Type 'exit' to exit");
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                printWriter.println("You write: " + str);
                if(str.equals("exit")) break;
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}