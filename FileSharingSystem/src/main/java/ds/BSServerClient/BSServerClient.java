package ds.BSServerClient;

import java.net.DatagramSocket;
import  ds.Constants;
import java.net.InetAddress;
import java.io.IOException;
import java.net.*;
import java.net.DatagramPacket;
import java.util.logging.Logger;
import java.util.*;


public class BSServerClient {

    private DatagramSocket dgramSocket;

    private String BServer_IPAddress;
    private int BServer_Port;

    private final Logger LOGGER = Logger.getLogger(BSServerClient.class.getName());
    private final String MESSAGE_FORMAT = "%04d %s";
    private final String REGISTER_FORMAT = "REG %s %s %s";
    private final String UNREGISTER_FORMAT = "UNREG %s %s %s";
    public static final String REGOK = "REGOK";
    public static final String UNROK = "UNROK";

    public static final int TIMEOUT_REGISTRATION = 10000;

    public BSServerClient() throws IOException{

        dgramSocket = new DatagramSocket();

        Properties bsProperties = new Properties();
        try {
            bsProperties.load(getClass().getClassLoader().getResourceAsStream(
                    Constants.BS_PROPERTIES));

        } catch (IOException e) {
            LOGGER.fine("Error opening " + Constants.BS_PROPERTIES);
            throw new RuntimeException("Error opening " + Constants.BS_PROPERTIES);
        } catch (NullPointerException e) {
            LOGGER.fine("Not found " + Constants.BS_PROPERTIES);
            throw new RuntimeException("Not found " + Constants.BS_PROPERTIES);
        }

        this.BServer_IPAddress = bsProperties.getProperty("bootstrap.ip");
        this.BServer_Port = Integer.parseInt(bsProperties.getProperty("bootstrap.port"));

        bsProperties.getProperty("bootstrap.ip");
        System.out.println("Connecting to BServer at "+this.BServer_IPAddress+":"+this.BServer_Port);
    }

    public List<InetSocketAddress> register(String ipAddress, String userName,  int port) throws IOException {

        String request = String.format(REGISTER_FORMAT, ipAddress, port, userName);

        request = String.format(MESSAGE_FORMAT, request.length() + 5, request);

        return  processBSResponse(request);

    }

    public boolean unRegister( String ipAddress, String userName, int port) throws IOException{

        String request = String.format(UNREGISTER_FORMAT, ipAddress, port, userName);

        request = String.format(MESSAGE_FORMAT, request.length() + 5, request);

        return  processBSUnregisterResponse(request);

    }

    private List<InetSocketAddress> processBSResponse(String request) throws IOException {
        DatagramPacket sendingPacket = new DatagramPacket(request.getBytes(),
                request.length(), InetAddress.getByName(BServer_IPAddress), BServer_Port);

        dgramSocket.setSoTimeout(TIMEOUT_REGISTRATION);

        dgramSocket.send(sendingPacket);

        byte[] buffer = new byte[65536];

        DatagramPacket received = new DatagramPacket(buffer, buffer.length);

        dgramSocket.receive(received);

        String response = new String(received.getData(), 0, received.getLength());

        StringTokenizer stringToken = new StringTokenizer(response, " ");

        String length = stringToken.nextToken();

        String status = stringToken.nextToken();

        if (!REGOK.equals(status)) {
            throw new IllegalStateException(REGOK + " not received");
        }

        int nodesCount = Integer.parseInt(stringToken.nextToken());

        List<InetSocketAddress> gNodes = null;

        switch (nodesCount) {
            case 0:
                LOGGER.fine("Successful - No other nodes in the network");
                gNodes = new ArrayList<>();
                break;

            case 1:
                LOGGER.fine("No of nodes found : 1");

                gNodes = new ArrayList<>();

                while (stringToken.hasMoreTokens()) {
                    gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                            Integer.parseInt(stringToken.nextToken())));
                }
                break;

            case 2:
                LOGGER.fine("No of nodes found : 2");

                gNodes = new ArrayList<>();

                while (stringToken.hasMoreTokens()) {
                    gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                            Integer.parseInt(stringToken.nextToken())));
                }
                break;

            case 9999:
                LOGGER.severe("Failed. There are errors in your command");
                break;
            case 9998:
                LOGGER.severe("Failed, already registered to you, unRegister first");
                break;
            case 9997:
                LOGGER.severe("Failed, registered to another user, try a different IP and port");
                break;
            case 9996:
                LOGGER.severe("Failed, can’t register. BS full.");
                break;
            default:
                throw new IllegalStateException("Invalid status code");
        }

        return gNodes;
    }

    private boolean processBSUnregisterResponse(String request) throws IOException{
        DatagramPacket sendingPacket = new DatagramPacket(request.getBytes(),
                request.length(), InetAddress.getByName(BServer_IPAddress), BServer_Port);

        dgramSocket.setSoTimeout(TIMEOUT_REGISTRATION);

        dgramSocket.send(sendingPacket);

        byte[] buffer = new byte[65536];

        DatagramPacket received = new DatagramPacket(buffer, buffer.length);

        dgramSocket.receive(received);

        String response = new String(received.getData(), 0, received.getLength());

        StringTokenizer stringTokenizer = new StringTokenizer(response, " ");

        String length = stringTokenizer.nextToken();
        String status = stringTokenizer.nextToken();

        if (!UNROK.equals(status)) {
            throw new IllegalStateException(UNROK + " not received");
        }

        int code = Integer.parseInt(stringTokenizer.nextToken());

        switch (code) {
            case 0:
                LOGGER.fine("Successfully unregistered");
                return true;

            case 9999:
                LOGGER.severe("Error while un-registering. " +
                        "IP and port may not be in the registry or command is incorrect");
            default:
                return false;
        }
    }

}
