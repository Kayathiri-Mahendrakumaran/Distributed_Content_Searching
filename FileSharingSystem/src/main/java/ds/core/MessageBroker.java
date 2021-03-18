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

    private volatile boolean process = true;
    public static final int ping_interval = 8000;
    public static final String ping_messageID = "rPingMessage";


    private final UDPServer server;
    private final UDPClient client;

    private BlockingQueue<ChannelMessage> channelIn;
    private BlockingQueue<ChannelMessage> channelOut;

    private FileHandler filehandler;
    private LeaveHandler leaveHandler;
    private PingHandler pingHandler;
    private RoutingTable routingTable;
    private SearchQueryHandler searchQueryHandler;
    private TimeHandler timeoutHandler = new TimeHandler();

    public MessageBroker(String address, int port) throws SocketException {
        channelIn = new LinkedBlockingQueue<ChannelMessage>();
        DatagramSocket socket = new DatagramSocket(port);

        this.server = new UDPServer(channelIn, socket);

        channelOut = new LinkedBlockingQueue<ChannelMessage>();
        this.client = new UDPClient(channelOut, new DatagramSocket());
        this.routingTable = new RoutingTable(address, port);

        this.pingHandler = PingHandler.getInstance();
        this.leaveHandler = LeaveHandler.getInstance();
        this.filehandler = FileHandler.getInstance("");
        this.pingHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);
        this.leaveHandler.init(this.routingTable, this.channelOut, this.timeoutHandler);
        this.searchQueryHandler = SearchQueryHandler.getInstance();
        this.searchQueryHandler.init(routingTable, channelOut, timeoutHandler);


        LOG.fine("Server got started ....");
        timeoutHandler.registering_Request(ping_messageID, ping_interval, new TimeoutCallback() {
            @Override
            public void onTimeout(String messageId) {
                sendRoutinePing();
            }

            @Override
            public void onResponse(String messageId) {
            }

        });
    }

    @Override
    public void run(){
        this.server.start();
        this.client.start();
        this.process();
    }

    public void process() {
        while (process) {
            try {
                ChannelMessage message = channelIn.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    LOG.info("Received Message: " + message.getMessage()
                            + " from: " + message.getAddress()
                            + " port: " + message.getPort());

                    AbstractResponseHandler abstractResponseHandler
                            = ResponseHandlerFactory.getResponseHandler(
                            message.getMessage().split(" ")[1],
                            this
                    );

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

    public void stopProcessing() {
        this.process = false;
        server.stopProcessing();
    }

    public void sendPing(String address, int port) {
        this.pingHandler.sendPing(address, port);
    }

    public void doSearch(String keyword){
        this.searchQueryHandler.search(keyword);
    }

    public BlockingQueue<ChannelMessage> getChannelIn() {
        return channelIn;
    }

    public BlockingQueue<ChannelMessage> getChannelOut() {
        return channelOut;
    }

    public TimeHandler getTimeoutManager() {
        return timeoutHandler;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }


    private void sendRoutinePing() {
        ArrayList<String> neighbor_list = routingTable.convert_toList();
        for (String n: neighbor_list) {
            String address = n.split(":")[0];
            int port = Integer.valueOf(n.split(":")[1]);
            sendPing(address, port);

        }
    }

    public void sendLeave() {
        this.leaveHandler.informLeave();
    }

    public String getFiles() {
        return this.filehandler.getFileNames();
    }
}
