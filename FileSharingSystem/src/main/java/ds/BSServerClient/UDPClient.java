package  ds.BSServerClient;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;


public class UDPClient extends Thread {
    private volatile boolean action = true;
    private final DatagramSocket dgramSocket;
    private final BlockingQueue<ChannelMessage> channelOut;

    public UDPClient(DatagramSocket socket, BlockingQueue<ChannelMessage> channelOut) {
        this.channelOut = channelOut;
        this.dgramSocket = socket;
    }

    @Override
    public void run() {
        while (action) {
            try {
                ChannelMessage message = channelOut.take();
                String address = message.getAddress();
                int port = message.getPort();
                String payload = message.getMessage();
                DatagramPacket packet = new DatagramPacket(
                        payload.getBytes(),
                        payload.length(),
                        InetAddress.getByName(address),
                        port
                );
                dgramSocket.send(packet);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        dgramSocket.close();
    }
}


