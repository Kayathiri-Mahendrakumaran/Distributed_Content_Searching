package  ds.FileManager;

import java.net.Socket;


public class FTP_Client {

    public FTP_Client(int port, String ip, String fileName) throws Exception {

        Socket socket = new Socket(ip, port);
        long start_time = System.currentTimeMillis();
        System.out.println("Connecting...");
        Thread t = new Thread(new FileReceiver(fileName,socket));
        t.start();
        long termination_time = System.currentTimeMillis();
    }


}