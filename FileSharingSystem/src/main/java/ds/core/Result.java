package  ds.core;

import  ds.Constants;

public class Result {
    private String location_address;
    private String fileName;
    private int location_port;
    private int tcpPort;
    private int num_hops;
    private long timeElapsed;

    public Result(String address, String fileName, int port, int hops, long timeElapsed) {
        this.location_address = address;
        this.fileName = fileName;
        this.location_port = port;
        this.tcpPort = port + Constants.FTP_PORT_OFFSET;
        this.num_hops = hops;
        this.timeElapsed = timeElapsed;
    }
    //get location address
    public String get_Address() {
        return location_address;
    }
    //get filename
    public String get_FileName() {
        return fileName;
    }
    //get location port
    public int get_Port() {
        return location_port;
    }
    //get TCP port
    public int get_TcpPort() {
        return tcpPort;
    }
    //get number of hops
    public int get_Hops() {
        return num_hops;
    }
    //get time taken
    public long get_TimeElapsed() {
        return timeElapsed;
    }
}
