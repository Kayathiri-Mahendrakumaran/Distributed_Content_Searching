package main.java.ds.handlers;

import main.java.ds.BSServerClient.ChannelMessage;

public interface AbstractResponseHandler extends AbstractMessageHandler {

    void handleResponse(ChannelMessage message);
}
