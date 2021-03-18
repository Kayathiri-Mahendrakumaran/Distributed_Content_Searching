package  ds.FileManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class FTP_Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientsocket;
    private final Logger LOG = Logger.getLogger(FTP_Server.class.getName());
    private String userName;

    public FTP_Server(String userName, int port) throws Exception {
        serverSocket = new ServerSocket(port);
        this.userName = userName;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {

            try {
                clientsocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread t = new Thread(new FileSender(clientsocket, userName));
            t.start();
        }
    }
}
