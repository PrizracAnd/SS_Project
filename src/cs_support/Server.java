package cs_support;

import db_support.IConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//

public class Server implements IConstants{


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-----------------------
    public Server(){
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println(SERVER_START);
            while (true) {
                Socket socket = serverSocket.accept();              // принимаем запросы
                new Thread(new MyServer(socket)).start();           // для каждого запроса - новый поток
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(SERVER_STOP);
    }
    //-----Constructors end-------------------------


    //////////////////////////////////////////////////////////
    ///  PSWM
    /////////////////////////////////////////////////////////
    public static void main(String[] args){
        new Server();
    }


    class MyServer implements Runnable {
        Socket socket;

        public MyServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream())) {
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println("Type 'exit' to exit");
                while (scanner.hasNextLine()) {
                    String str = scanner.nextLine();
                    printWriter.println("You write: " + str);
                    if (str.equals("exit")) break;
                    System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
