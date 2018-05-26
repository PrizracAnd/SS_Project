package cs_support;

import db_support.IConstants;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


/* Нужно дописать, чтобы можно было вводить данные при запущенном сервере */
public class Client implements IConstants{


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-----------------------
    public Client(){
        String message;
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT);
             BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
             BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
             Scanner cs = new Scanner(System.in))
        {
            do {
                message = cs.nextLine();
                printWriter.println(message);
                printWriter.flush();

            }while (!message.equals(EXIT_COMMAND));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-----Constructors end-------------------------


    //////////////////////////////////////////////////////////
    ///  PSWM
    /////////////////////////////////////////////////////////
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

    class ServerListener implements Runnable{

        BufferedInputStream bis;
        BufferedReader reader;


        //////////////////////////////////////////////////////////
        ///  Constructors
        /////////////////////////////////////////////////////////
        ServerListener(BufferedReader br, BufferedInputStream bis){
            this.reader = br;
            this.bis = bis;
        }


        //////////////////////////////////////////////////////////
        ///  Method run
        /////////////////////////////////////////////////////////
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null){
                    System.out.print(message.equals("\0") ? CLIENT_PROMPT : message + "\n");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
