package main.java.ds.BSServerClient;

import main.java.ds.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.util.logging.Logger;


public class BSServerClient {

    private final Logger LOGGER = Logger.getLogger(BSServerClient.class.getName());

    private int BS_Server_Port;
    private String BS_Server_IPAddress;

    private final String REG_FORMAT = "REG %s %s %s";
    private final String UNREG_FORMAT = "UNREG %s %s %s";
    private final String MSG_FORMAT = "%04d %s";

    private static final String REGOK = "REGOK";
    private static final String UNROK = "UNROK";
    private static final int TIMEOUT_REG = 10000;

    private DatagramSocket datagramSocket;

    public BSServerClient() throws Exception{

        datagramSocket = new DatagramSocket();

        Properties BS_Server_Properties = new Properties();

        try {
            BS_Server_Properties.load(getClass().getClassLoader().getResourceAsStream(
                    "Bootstrap.properties"));

        } catch (Exception e) {
            LOGGER.severe("Could not load " + "Bootstrap.properties");
            throw new RuntimeException("Could not load " + "Bootstrap.properties");
        }

        this.BS_Server_IPAddress = BS_Server_Properties.getProperty("bootstrap.ip");
        this.BS_Server_Port = Integer.parseInt(BS_Server_Properties.getProperty("bootstrap.port"));
    }

    public List<InetSocketAddress> register(String userName, String ipAddress, int port) throws IOException {

        String request = String.format(REG_FORMAT, ipAddress, port, userName);

        request = String.format(MSG_FORMAT, request.length() + 5, request);

        return  processBSResponse(sendOrReceive(request));

    }

    private String sendOrReceive(String request) throws IOException {
        DatagramPacket sendingPacket = new DatagramPacket(request.getBytes(),
                request.length(), InetAddress.getByName(BS_Server_IPAddress), BS_Server_Port);

        datagramSocket.setSoTimeout(TIMEOUT_REG);

        datagramSocket.send(sendingPacket);

        byte[] buffer = new byte[65536];

        DatagramPacket received = new DatagramPacket(buffer, buffer.length);

        datagramSocket.receive(received);

        return new String(received.getData(), 0, received.getLength());
    }

    public boolean unRegister(String userName, String ipAddress, int port) throws IOException{

        String request = String.format(UNREG_FORMAT, ipAddress, port, userName);

        request = String.format(MSG_FORMAT, request.length() + 5, request);

        return  processBSUnregisterResponse(sendOrReceive(request));

    }

    private List<InetSocketAddress> processBSResponse(String response) {

        StringTokenizer stringToken = new StringTokenizer(response, " ");

        String status = stringToken.nextToken();

        if (!REGOK.equals(status)) {
            throw new IllegalStateException(REGOK + " not received");
        }

        int nodesCount = Integer.parseInt(stringToken.nextToken());

        List<InetSocketAddress> gNodes = null;


        if (nodesCount == 0) {
            LOGGER.severe("Successful - No other nodes in the network");
            gNodes = new ArrayList<>();
        }

        else if ( nodesCount == 1){
            LOGGER.severe("No of nodes found : 1");

            gNodes = new ArrayList<>();

            while (stringToken.hasMoreTokens()) {
                gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                        Integer.parseInt(stringToken.nextToken())));
            }
        }

        else if ( nodesCount == 2){
            LOGGER.fine("No of nodes found : 2");

            gNodes = new ArrayList<>();

            while (stringToken.hasMoreTokens()) {
                gNodes.add(new InetSocketAddress(stringToken.nextToken(),
                        Integer.parseInt(stringToken.nextToken())));
            }
        }

        else if (  nodesCount == 9999){
            LOGGER.severe("Failed. There are errors in your command");
        }
        else if ( nodesCount == 9998){
            LOGGER.severe("Failed, already registered to you, unRegister first");
        }
        else if ( nodesCount == 9997){
            LOGGER.severe("Failed, registered to another user, try a different IP and port");
        }
        else if (nodesCount == 9996){
            LOGGER.severe("Failed, can’t register. BS full.");
        }
        else{
            throw new IllegalStateException("Invalid status code");
        }


        return gNodes;
    }

    private boolean processBSUnregisterResponse(String response){

        StringTokenizer stringTokenizer = new StringTokenizer(response, " ");

        String status = stringTokenizer.nextToken();

        if (!UNROK.equals(status)) {
            throw new IllegalStateException(UNROK + " not received");
        }

        int code = Integer.parseInt(stringTokenizer.nextToken());


            if (code == 0){
                LOGGER.severe("Successfully unregistered");
                return true;
            }


            else if (code == 9999){
                LOGGER.severe("Error while un-registering. " +
                        "IP and port may not be in the registry or command is incorrect");
                return false;
            }

            else{
                return false;
            }
    }




}
