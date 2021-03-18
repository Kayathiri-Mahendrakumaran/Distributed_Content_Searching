package  ds.Handlers;

import  ds.BSServerClient.ChannelMessage;

public interface AbstractRequestHandler extends AbstractMessageHandler {

    void sendRequest(ChannelMessage message);
}
