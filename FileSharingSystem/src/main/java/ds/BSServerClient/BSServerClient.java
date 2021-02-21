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

    public boolean unRegister(String userName, String ipAddress, int port) throws IOException{

        String request = String.format(UNREG_FORMAT, ipAddress, port, userName);

        request = String.format(MSG_FORMAT, request.length() + 5, request);

        return  processBSUnregisterResponse(sendOrReceive(request));

    }



    private boolean processBSUnregisterResponse(String response){

        StringTokenizer stringTokenizer = new StringTokenizer(response, " ");

        String status = stringTokenizer.nextToken();

        if (!Constants.UNROK.equals(status)) {
            throw new IllegalStateException(Constants.UNROK + " not received");
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
