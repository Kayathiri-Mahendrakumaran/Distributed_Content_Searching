package main.java.ds.core;

public class Neighbor {

    private final String address;
    private final int port_num;
    private int client_Port;
    private int pingPongs;

    public Neighbor(String address, int port, int clientPort) {
        this.address = address;
        this.port_num = port;
        this.client_Port = clientPort;
        this.pingPongs = 0;
    }

    public boolean check_same(Neighbor newNeighbor) {
        return (this.address.equals(newNeighbor.get_Address()) &
                (newNeighbor.get_Port() == this.port_num | newNeighbor.get_ClientPort() == this.port_num));
    }

    public boolean check_same(String address, int port) {
        return (this.address.equals(address) &
                (this.port_num == port | this.client_Port == port));
    }

    public String get_Address() {
        return address;
    }

    public int get_Port() {
        return port_num;
    }

    public int get_ClientPort() {
        return client_Port;
    }

    public int get_PingPongs() {
        return pingPongs;
    }

    public void Ping() {
        this.pingPongs++;
    }

    public String toString() {
        return this.address + ":" + this.port_num;
    }
}
