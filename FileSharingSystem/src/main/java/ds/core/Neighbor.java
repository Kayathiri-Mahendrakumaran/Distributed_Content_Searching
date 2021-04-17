package  ds.core;

public class Neighbor {

    private final String address;
    private final int port_num;
    private int client_Port;
    private int num_pingPongs;

    // constructor
    public Neighbor(int port, int clientPort, String address) {
        this.port_num = port;
        this.client_Port = clientPort;
        this.address = address;
        this.num_pingPongs = 0;
    }
    //check both are same
//    public boolean check_same(Neighbor newNeighbor) {
//        return (this.address.equals(newNeighbor.get_Address()) &
//                (newNeighbor.get_Port() == this.port_num | newNeighbor.get_ClientPort() == this.port_num));
//    }
    //check both are same
    public boolean check_same(int port, String address) {
        return (this.address.equals(address) &
                (this.port_num == port | this.client_Port == port));
    }
    //get neighbor port
    public int get_Port() {
        return port_num;
    }
    //get client port
    public int get_ClientPort() {
        return client_Port;
    }
    // get neighbor address
    public String get_Address() {
        return address;
    }
    //get pingpongs
    public int get_PingPongs() {
        return num_pingPongs;
    }
    //increment pingpongs
    public void Ping() {
        this.num_pingPongs++;
    }
    //get total address as string
    public String toString() {
        String string = this.address + "-" + this.port_num;
        return string;
    }
}
