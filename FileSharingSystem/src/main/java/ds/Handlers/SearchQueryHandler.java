package  ds.Handlers;

import  ds.Constants;
import  ds.BSServerClient.ChannelMessage;
import  ds.core.FileHandler;
import  ds.core.Neighbor;
import  ds.core.RoutingTable;
import  ds.core.TimeHandler;
import  ds.utils.StringEncoderDecoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SearchQueryHandler implements AbstractResponseHandler, AbstractRequestHandler {

    private final Logger LOG = Logger.getLogger(SearchQueryHandler.class.getName());

    private RoutingTable routingTable;

    private BlockingQueue<ChannelMessage> channelOut;

    private TimeHandler timeHandler;
    private static SearchQueryHandler searchQueryHandler;
    private FileHandler fileHandler;

    private SearchQueryHandler(){
        fileHandler = FileHandler.getInstance("");
    }

    public synchronized static SearchQueryHandler getInstance(){
        if (searchQueryHandler == null){
            searchQueryHandler = new SearchQueryHandler();
        }
        return searchQueryHandler;
    }

    public void search(String keyword) {

        String payload = String.format(Constants.QUERY_FORMAT,
                this.routingTable.get_Address(),
                this.routingTable.get_Port(),
                StringEncoderDecoder.encode(keyword),
                Constants.HOP_COUNT);

        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

        ChannelMessage initialMessage = new ChannelMessage(
                rawMessage, this.routingTable.get_Port(), this.routingTable.get_Address()
        );

        this.handleResponse(initialMessage);
    }

    @Override
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(RoutingTable routingTable, BlockingQueue<ChannelMessage> channelOut,
                     TimeHandler timeHandler) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeHandler = timeHandler;
    }

    @Override
    public void handleResponse(ChannelMessage message) {
        LOG.fine("Received SER : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String fileName = StringEncoderDecoder.decode(stringToken.nextToken().trim());
        int hops = Integer.parseInt(stringToken.nextToken().trim());

        //search for the file in the current node
        Set<String> resultSet = fileHandler.searchFile(fileName);
        int fileNamesCount = resultSet.size();

        if (fileNamesCount != 0) {

            StringBuilder fileNamesString = new StringBuilder("");

            Iterator<String> itr = resultSet.iterator();

            while(itr.hasNext()){
                fileNamesString.append(StringEncoderDecoder.encode(itr.next()) + " ");
            }

            String payload = String.format(Constants.QUERY_HIT_FORMAT,
                    fileNamesCount,
                    routingTable.get_Address(),
                    routingTable.get_Port(),
                    Constants.HOP_COUNT- hops,
                    fileNamesString.toString());

            String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

            ChannelMessage queryHitMessage = new ChannelMessage(rawMessage, port, address
            );

            this.sendRequest(queryHitMessage);
        }

        //if the hop count is greearchater than zero send the message to all neighbours again

        if (hops > 0){
            ArrayList<Neighbor> neighbours = this.routingTable.get_Neighbours_list();

            for(Neighbor neighbour: neighbours){

                //skip sending search query to the same node again
                if (neighbour.get_Address().equals(message.getAddress())
                        && neighbour.get_ClientPort() == message.getPort()) {
                    continue;
                }

                String payload = String.format(Constants.QUERY_FORMAT,
                        address,
                        port,
                        StringEncoderDecoder.encode(fileName),
                        hops - 1);

                String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

                ChannelMessage queryMessage = new ChannelMessage(rawMessage, neighbour.get_Port(), neighbour.get_Address()
                );

                this.sendRequest(queryMessage);
            }
        }
    }
}
