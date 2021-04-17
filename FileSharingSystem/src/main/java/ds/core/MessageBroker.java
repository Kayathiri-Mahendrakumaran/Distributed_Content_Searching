package  ds.core;

import  ds.BSServerClient.ChannelMessage;
import  ds.BSServerClient.UDPClient;
import  ds.BSServerClient.UDPServer;
import  ds.Handlers.*;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MessageBroker extends Thread{
    private final Logger LOG = Logger.getLogger(MessageBroker.class.getName());

    private volatile boolean action = true;
    public static final int ping_interval = 8000;
    public static final String ping_messageID = "rPingMessage";

    private final UDPClient client;
    private final UDPServer server;

    private BlockingQueue<ChannelMessage> channelOut;
    private BlockingQueue<ChannelMessage> channelIn;

    private FileHandler filehandler;
    private LeaveHandler leaveHandler;
    private PingHandler pingHandler;
    private RoutingTable routingTable;
    private SearchQueryHandler searchQueryHandler;
    private TimeHandler timeoutHandler = new TimeHandler();

    public MessageBroker(String address, int port) throws SocketException {
        channelIn = new LinkedBlockingQueue<ChannelMessage>();
        channelOut = new LinkedBlockingQueue<ChannelMessage>();
        DatagramSocket socket = new DatagramSocket(port);

        this.server = new UDPServer(socket, channelIn);
        this.client = new UDPClient(new DatagramSocket(), channelOut);

        this.routingTable = new RoutingTable(port, address);

        this.leaveHandler = LeaveHandler.getInstance();
        this.pingHandler = PingHandler.getInstance();
        this.filehandler = FileHandler.getInstance("");
        this.leaveHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);
        this.pingHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);
        this.searchQueryHandler = SearchQueryHandler.getInstance();
        this.searchQueryHandler.init(routingTable, channelOut, timeoutHandler);


        LOG.fine("Server got started ....");
        timeoutHandler.registering_Request(ping_messageID, new TimeoutCallback() {
            @Override
            public void onTimeout(String messageId) {
                sendRoutinePing();
            }

            @Override
            public void onResponse(String messageId) {
            }

        }, ping_interval);
    }

    @Override
    public void run(){
        this.server.start();
        this.client.start();
        this.initiate();
    }

    public void initiate() {
        while (action) {
            try {
                ChannelMessage message = channelIn.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    LOG.info("Message Received => " + message.getMessage()
                            + " from: " + message.getAddress()
                            + ":" + message.getPort());

                    AbstractResponseHandler abstractResponseHandler
                            = ResponseHandlerFactory.getResponseHandler(
                            message.getMessage().split(" ")[1],
                            this);
                    if (abstractResponseHandler != null){
                        abstractResponseHandler.handleResponse(message);
                    }
                }
                timeoutHandler.remove_TimeOut_Messages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    public void stopProcessing() {
//        this.action = false;
//        server.stopProcessing();
//    }

    public void send_Ping(int port, String address) {
        this.pingHandler.sendPing(address, port);
    }

    public BlockingQueue<ChannelMessage> getChannelIn() {
        return channelIn;
    }

    public BlockingQueue<ChannelMessage> getChannelOut() {
        return channelOut;
    }

    public RoutingTable get_RoutingTable() {
        return routingTable;
    }

    public TimeHandler get_TimeoutManager() {
        return timeoutHandler;
    }

    public void do_Search(String keyword){
        this.searchQueryHandler.search(keyword);
    }


    private void sendRoutinePing() {
        ArrayList<String> neighbor_list = routingTable.convert_toList();
        for (String n: neighbor_list) {
            String address = n.split(":")[0];
            int port = Integer.valueOf(n.split(":")[1]);
            send_Ping(port, address);

        }
    }

    public void sendLeave() {
        this.leaveHandler.informLeave();
    }

    public String getFiles() {
        return this.filehandler.getFileNames();
    }
}
