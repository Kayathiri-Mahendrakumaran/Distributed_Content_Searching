package  ds.Handlers;

import  ds.BSServerClient.ChannelMessage;
import  ds.core.RoutingTable;
import  ds.core.TimeHandler;

import java.util.concurrent.BlockingQueue;

interface AbstractMessageHandler {
    void init (
            RoutingTable routingTable,
            BlockingQueue<ChannelMessage> channelOut,
            TimeHandler timeHandler);

}
