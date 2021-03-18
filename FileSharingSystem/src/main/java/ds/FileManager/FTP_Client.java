package  ds.FileManager;//import javafx.scene.control.TextArea;

import java.net.Socket;
import javafx.scene.control.TextArea;

import  ds.FileManager.FileReceiver;

public class FTP_Client {

    public FTP_Client(int port, String ip, String fileName) throws Exception {

        Socket socket = new Socket(ip, port);
        long start_time = System.currentTimeMillis();
        System.out.println("Connecting...");
        Thread t = new Thread(new FileReceiver(fileName,socket));
        t.start();
        long termination_time = System.currentTimeMillis();
    }

    //    UI enabled.
    public FTP_Client(String ip, int port, String fileName, TextArea textArea) throws Exception {

        long start_time = System.currentTimeMillis();
        Socket serverSocket = new Socket(ip, port);

        textArea.setText("Connecting...");
        Thread t = new Thread(new FileReceiver(fileName, serverSocket, textArea));
        t.start();
        long termination_time = System.currentTimeMillis();
    }
}