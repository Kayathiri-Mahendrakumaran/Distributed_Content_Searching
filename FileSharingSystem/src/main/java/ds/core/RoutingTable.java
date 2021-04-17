package  ds.core;

import  ds.Constants;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RoutingTable {
    private final Logger LOG = Logger.getLogger(RoutingTable.class.getName());
    private ArrayList<Neighbor> neighbours_list;
    private final int port;
    private final String address;
    // Constants
    public static final int max_neighbors = 6;

    public RoutingTable(int port, String address) {
        this.neighbours_list = new ArrayList<>();
        this.port = port;
        this.address = address;
    }
    //get address
    public String get_Address() {
        return address;
    }

    //get port
    public int get_Port() {
        return port;
    }

    //get neighbor list
    public ArrayList<Neighbor> get_Neighbours_list() {
        return neighbours_list;
    }

    //get neighbor count
    public synchronized int getCount() {
        return neighbours_list.size();
    }

    //add neighbor
    public synchronized int add_Neighbour(int port, int clientPort, String address) {
        //check the neighbor already exists
        for (Neighbor neighbor: neighbours_list) {
            if (neighbor.check_same(port, address)){
                neighbor.Ping();
                return neighbours_list.size();
            }
        }
        //check max num of neighbour
        if (neighbours_list.size() >= max_neighbors) {
            return 0;
        }
        //creating new neighbor
        Neighbor newN = new Neighbor(port, clientPort, address);
        neighbours_list.add(newN);

        LOG.fine("Neighbor is added => address:" + address + " port number:" + port);
        return neighbours_list.size();
    }

    //remove neighbor
    public synchronized int remove_Neighbour(int port, String address) {
        Neighbor n_Remove = null;
        for (Neighbor neighbour: neighbours_list) {
            if (neighbour.check_same(port, address)) {
                n_Remove = neighbour;
            }
        }
        if (n_Remove != null) {
            neighbours_list.remove(n_Remove);
            LOG.fine("Neighbor is removed => address:" + address + " port number:" + port);
            // return the size of the neighbors
            return neighbours_list.size();
        }
        // there is no related neighbour
        return 0;
    }

    //return as a string
    public synchronized String toString() {
        String data = "Address: " + address + ":" + port + "\n";
        data += "Total neighbours: " + neighbours_list.size() + "\n";
        data += "Neighbors " + "\n";

        data += "===========================" + "\n";
        for (Neighbor n :neighbours_list) {
            data +=
                    "Address: " + n.get_Address() + " Port: " + n.get_Port() + " Pings: " + n.get_PingPongs() + "\n";
        }
        data += "===========================" + "\n";
        return data;
    }

    //check whether it is a neighbor
    public boolean is_Neighbour(String address, int port) {
        for (Neighbor n: neighbours_list) {
            if (n.check_same(port, address)) {
                return  true;
            }
        }
        return false;
    }
    //get other neighbors
    public ArrayList<String> getOtherNeighbours(String address, int port) {
        ArrayList<String> temp = new ArrayList<>();
        for (Neighbor n: neighbours_list) {
            if(!n.check_same(port, address)) {
                temp.add(n.toString());
            }
        }
        return temp;
    }
    //print summary
    public synchronized void print_summary() {
        System.out.println(this.toString());
    }
    //convert to list
    public synchronized ArrayList<String> convert_toList() {
        ArrayList<String> temp = new ArrayList<>();
        for (Neighbor n: neighbours_list) {
            temp.add(n.toString());
        }
        return temp;
    }
}
