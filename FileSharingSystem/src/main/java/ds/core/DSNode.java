package ds.core;

import ds.Constants;
import ds.BSServerClient.BSServerClient;
import ds.FileManager.FTP_Client;
import ds.FileManager.FTP_Server;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.logging.Logger;

public class DSNode {

    private final Logger LOGGER = Logger.getLogger(DSNode.class.getName());

    private BSServerClient bsServerClient;
    private String ip_Address;
    private String user_Name;
    private int port;

    private SearchManager searchManager;
    private MessageBroker messageBroker;

    private FTP_Server ftpServer;

    public DSNode(String user_Name) throws Exception {

        try (final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.ip_Address = socket.getLocalAddress().getHostAddress();

        } catch (Exception e){
            throw new RuntimeException("Could not find host address");
        }

        this.port = getFreePort();
        this.user_Name = user_Name;

        this.ftpServer = new FTP_Server(user_Name,this.port + Constants.FTP_PORT_OFFSET);
        Thread t = new Thread(ftpServer);
        t.start();
        this.messageBroker = new MessageBroker(ip_Address, port);
        this.bsServerClient = new BSServerClient();


        this.searchManager = new SearchManager(this.messageBroker);

        messageBroker.start();

        LOGGER.severe("DSNode started on IP :" + ip_Address + " and Port :" + port);

    }

    public void start() {
        List<InetSocketAddress> targets = this.registerDSNode();
        if(targets != null) {
            for (InetSocketAddress target: targets) {
                messageBroker.sendPing(target.getAddress().toString().substring(1), target.getPort());
            }

        }
    }

    private List<InetSocketAddress> registerDSNode() {
        List<InetSocketAddress> targets = null;

        try{
            targets = this.bsServerClient.register(this.user_Name, this.ip_Address, this.port);

        } catch (IOException e) {
            LOGGER.severe("Registering DSNode failed");
            e.printStackTrace();
        }
        return targets;

    }

    public void unregisterDSNode() {
        try{
            this.bsServerClient.unRegister(this.ip_Address, this.user_Name, this.port);
            this.messageBroker.sendLeave();

        } catch (IOException e) {
            LOGGER.severe("Unregistering DSNode failed");
            e.printStackTrace();
        }
    }

    public void printRoutingTable(){
        this.messageBroker.getRoutingTable().print_summary();
    }

    public int search(String keyword){
        return this.searchManager.searchFiles(keyword);
    }

    public int getPort(){
        return port;
    }

    public void getFile(int fileOption) {
        try {
            Result fileDetail = this.searchManager.get_file_details(fileOption);
            System.out.println("The file you requested is " + fileDetail.getFileName());
            FTP_Client ftpClient = new FTP_Client(fileDetail.getTcpPort(), fileDetail.getAddress(),
                    fileDetail.getFileName());

            System.out.println("Waiting for file download...");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
            }
            return port;
        } catch (IOException e) {
            LOGGER.severe("Obtaining free port failed");
            throw new RuntimeException("Obtaining free port failed");
        }
    }
}
