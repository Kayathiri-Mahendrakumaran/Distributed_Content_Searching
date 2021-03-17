package main.java.ds.handlers;

import main.java.ds.BSServerClient.ChannelMessage;
import main.java.ds.core.RoutingTable;
import main.java.ds.core.TimeHandler;

import java.util.concurrent.BlockingQueue;

interface AbstractMessageHandler {
    void init (
            RoutingTable routingTable,
            BlockingQueue<ChannelMessage> channelOut,
            TimeHandler timeHandler);

}
