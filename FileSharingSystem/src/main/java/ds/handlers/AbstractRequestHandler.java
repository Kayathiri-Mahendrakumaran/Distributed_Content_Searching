package main.java.ds.handlers;

import main.java.ds.BSServerClient.ChannelMessage;

public interface AbstractRequestHandler extends AbstractMessageHandler {

    void sendRequest(ChannelMessage message);
}
