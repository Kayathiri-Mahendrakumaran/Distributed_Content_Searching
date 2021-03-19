package  ds.FileManager;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;

public class FileReceiver implements Runnable {

    private BufferedReader bufferedReader = null;
    private String fileName;
    private Socket socket;

    public FileReceiver(String fileName, Socket serverSocket) {
        this.socket = serverSocket;
        this.fileName = fileName;
    }


    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeUTF(fileName);
            dOut.flush();
            receiveFile();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream serverData = new DataInputStream(socket.getInputStream());

            String fileName = serverData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = serverData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = serverData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            serverData.close();

            System.out.println("File " + fileName + " successfully downloaded.");


        } catch (IOException ex) {
            System.err.println("Internal server error. Terminating the connection.");
            ex.printStackTrace();
        }
    }

}
