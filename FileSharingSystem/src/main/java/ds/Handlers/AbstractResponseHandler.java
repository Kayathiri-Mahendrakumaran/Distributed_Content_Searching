package  ds.Handlers;

import  ds.BSServerClient.ChannelMessage;

public interface AbstractResponseHandler extends AbstractMessageHandler {

    void handleResponse(ChannelMessage message);
}
