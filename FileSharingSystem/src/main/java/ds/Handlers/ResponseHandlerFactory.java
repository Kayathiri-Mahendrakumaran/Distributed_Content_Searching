package   ds.Handlers;

import   ds.core.MessageBroker;

import java.util.logging.Logger;

public class ResponseHandlerFactory {

    private static final Logger LOG = Logger.getLogger(ResponseHandlerFactory.class.getName());

    public static AbstractResponseHandler getResponseHandler(String keyword,
                                                             MessageBroker messageBroker){
        switch (keyword){
            case "PING":
                AbstractResponseHandler pingHandler = PingHandler.getInstance();
                pingHandler.init(
                        messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager()
                );
                return pingHandler;

            case "BPING":
                AbstractResponseHandler bPingHandler = PingHandler.getInstance();
                bPingHandler.init(
                        messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager()
                );
                return bPingHandler;

            case "PONG":
                AbstractResponseHandler pongHandler = PongHandler.getInstance();
                pongHandler.init(
                        messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager()
                );
                return pongHandler;

            case "BPONG":
                AbstractResponseHandler bpongHandler = PongHandler.getInstance();
                bpongHandler.init(
                        messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager()
                );
                return bpongHandler;

            case "SER":
                AbstractResponseHandler searchQueryHandler = SearchQueryHandler.getInstance();
                searchQueryHandler.init(messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager());
                return searchQueryHandler;

            case "SEROK":
                AbstractResponseHandler queryHitHandler = QueryHitHandler.getInstance();
                queryHitHandler.init(messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager());
                return queryHitHandler;

            case "LEAVE":
                AbstractResponseHandler leaveHandler = PingHandler.getInstance();
                leaveHandler.init(
                        messageBroker.get_RoutingTable(),
                        messageBroker.getChannelOut(),
                        messageBroker.get_TimeoutManager()
                );
                return leaveHandler;
            default:
                LOG.severe("Unknown keyword received in Response Handler : " + keyword);
                return null;
        }
    }
}
