package  ds.Handlers;

import  ds.Constants;
import  ds.BSServerClient.ChannelMessage;
import  ds.core.RoutingTable;
import  ds.core.Result;
import  ds.core.TimeHandler;
import  ds.utils.StringEncoderDecoder;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class QueryHitHandler implements AbstractResponseHandler {

    private static final Logger LOG = Logger.getLogger(QueryHitHandler.class.getName());

    private RoutingTable routingTable;

    private BlockingQueue<ChannelMessage> channelOut;

    private TimeHandler timeHandler;
    private static QueryHitHandler queryHitHandler;

    private Map<String, Result> searchResult;
    private long searchInitiatedTime;

    private QueryHitHandler(){

    }

    public static synchronized QueryHitHandler getInstance(){
        if (queryHitHandler == null){
            queryHitHandler = new QueryHitHandler();
        }

        return queryHitHandler;
    }

    @Override
    public synchronized void handleResponse(ChannelMessage message) {
        LOG.fine("Received SEROK : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        int filesCount = Integer.parseInt(stringToken.nextToken());
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String addressKey = String.format(Constants.ADDRESS_KEY_FORMAT, address, port);

        int hops = Integer.parseInt(stringToken.nextToken());

        while(filesCount > 0){

            String fileName = StringEncoderDecoder.decode(stringToken.nextToken());

            if (this.searchResult != null){
                if(!this.searchResult.containsKey(addressKey + fileName)){
                    this.searchResult.put(addressKey + fileName,
                            new Result(address, fileName, port, hops,
                                    (System.currentTimeMillis() - searchInitiatedTime)));

                }
            }

            filesCount--;
        }
    }

    @Override
    public void init(RoutingTable routingTable, BlockingQueue<ChannelMessage> channelOut, TimeHandler timeHandler) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeHandler = timeHandler;
    }

    public void setSearchResult(Map<String, Result> searchResult) {
        this.searchResult = searchResult;
    }

    public void setSearchInitiatedTime(long currentTimeinMillis){
        this.searchInitiatedTime = currentTimeinMillis;
    }

}
