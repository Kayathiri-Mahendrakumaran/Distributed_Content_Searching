package main.java.ds.handlers;

import main.java.ds.Constants;
import main.java.ds.BSServerClient.ChannelMessage;
import main.java.ds.core.Neighbor;
import main.java.ds.core.RoutingTable;
import main.java.ds.core.TimeHandler;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LeaveHandler implements AbstractRequestHandler {

    private RoutingTable routingTable;
    private BlockingQueue<ChannelMessage> channelOut;
    private static LeaveHandler leaveHandler;

    public synchronized static LeaveHandler getInstance() {
        if (leaveHandler == null){
            leaveHandler = new LeaveHandler();
        }
        return leaveHandler;
    }

    @Override
    public void init(RoutingTable routingTable,
                     BlockingQueue<ChannelMessage> channelOut,
                     TimeHandler timeHandler) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
    }

    // sending neighbours LEAVE message
    public void informLeave () {
        String payload = String.format(Constants.LEAVE_FORMAT,
                this.routingTable.get_Address(),
                this.routingTable.get_Port());
        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5,payload);
        ArrayList<Neighbor> neighbours = routingTable.get_Neighbours_list();
        for (Neighbor n: neighbours) {
            ChannelMessage message = new ChannelMessage(n.get_Address(), n.get_Port(),rawMessage);
            sendRequest(message);
        }

    }

    // adding LEAVE message
    
    @Override  
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
