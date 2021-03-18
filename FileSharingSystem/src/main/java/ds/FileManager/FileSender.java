package  ds.FileManager;

import  ds.core.FileHandler;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class FileSender implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;

    private final Logger LOG = Logger.getLogger(FileSender.class.getName());

    private String userName;

    public FileSender(Socket clientSocket, String userName) {
        this.clientSocket = clientSocket;
        this.userName = userName;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            String fileName = dIn.readUTF();

            if (fileName != null) {
                sendFile(FileHandler.getInstance("").getFile(fileName));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendFile(File file) {
        try {
            //handle file read
            File myFile = file;
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fileInputStream = new FileInputStream(myFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            dataInputStream.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            fileInputStream.close();
            LOG.fine("File " + file.getName() + " sent to client.");
        } catch (Exception e) {
            LOG.severe("File does not exist!");
            e.printStackTrace();
        }
    }
}