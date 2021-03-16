package main.java.ds.core;

import main.java.ds.Constants;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RoutingTable {
    private final Logger LOG = Logger.getLogger(RoutingTable.class.getName());
    private ArrayList<Neighbor> neighbours_list;
    private final String address;
    private final int port;

    public RoutingTable(String address, int port) {
        this.address = address;
        this.port = port;
        this.neighbours_list = new ArrayList<>();
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
    public synchronized int add_Neighbour(String address, int port, int clientPort) {
        //check the neighbor already exists
        for (Neighbor n: neighbours_list) {
            if (n.check_same(address, port)){
                n.Ping();
                return neighbours_list.size();
            }
        }
        //check max num of neighbour
        if (neighbours_list.size() >= Constants.MAX_NEIGHBOURS) {
            return 0;
        }
        //creating new neighbor
        Neighbor newN = new Neighbor(address, port, clientPort);
        neighbours_list.add(newN);

        LOG.fine("Neighbor is added => address:" + address + " port number:" + port);
        return neighbours_list.size();
    }

    //remove neighbor
    public synchronized int removeNeighbour(String address, int port) {
        Neighbor toRemove = null;
        for (Neighbor n: neighbours_list) {
            if (n.check_same(address, port)) {
                toRemove = n;
            }
        }
        if (toRemove != null) {
            neighbours_list.remove(toRemove);
            return neighbours_list.size();
        }
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
            if (n.check_same(address, port)) {
                return  true;
            }
        }
        return false;
    }
    //get other neighbors
    public ArrayList<String> getOtherNeighbours(String address, int port) {
        ArrayList<String> temp = new ArrayList<>();
        for (Neighbor n: neighbours_list) {
            if(!n.check_same(address, port)) {
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
