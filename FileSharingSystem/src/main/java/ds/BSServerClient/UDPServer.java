package  ds.BSServerClient;


import java.net.DatagramSocket;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.net.DatagramPacket;


public class UDPServer extends Thread {

    private final BlockingQueue<ChannelMessage> channelIn;
    private final DatagramSocket dgramSocket;
    private volatile boolean process = true;

    public UDPServer(DatagramSocket socket, BlockingQueue<ChannelMessage> channelIn) {
        this.dgramSocket = socket;
        this.channelIn = channelIn;
    }

    @Override
    public void run() {
        while (process) {
            try {
                byte[] response = new byte[65536];
                DatagramPacket dgramPacket = new DatagramPacket(response, response.length);
                dgramSocket.receive(dgramPacket);

                String address = ((dgramPacket.getSocketAddress().toString()).substring(1)).split(":")[0];
                String body = new String(response, 0, response.length);

                int port = Integer.parseInt(((dgramPacket.getSocketAddress().toString()).substring(1)).split(":")[1]);

                ChannelMessage message = new ChannelMessage(body, port, address);
                channelIn.put(message);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        dgramSocket.close();
    }
}
