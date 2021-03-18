package  ds.core;

import  ds.Constants;

public class Result {
    private String fileName;
    private String location_address;
    private int location_port;
    private int tcpPort;
    private int num_hops;
    private long timeElapsed;

    public Result(String fileName, String address, int port, int hops, long timeElapsed) {
        this.fileName = fileName;
        this.location_address = address;
        this.location_port = port;
        this.tcpPort = port + Constants.FTP_PORT_OFFSET;
        this.num_hops = hops;
        this.timeElapsed = timeElapsed;
    }
    //get filename
    public String getFileName() {
        return fileName;
    }
    //get location address
    public String getAddress() {
        return location_address;
    }
    //get location port
    public int getPort() {
        return location_port;
    }
    //get TCP port
    public int getTcpPort() {
        return tcpPort;
    }
    //get number of hops
    public int getHops() {
        return num_hops;
    }
    //get time taken
    public long getTimeElapsed() {
        return timeElapsed;
    }
}
