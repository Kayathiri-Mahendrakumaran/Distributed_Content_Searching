package ds.BSServerClient;

public class ChannelMessage {
    private final String message;
    private final int port;
    private final String address;

    public ChannelMessage(String message, int port, String address) {
        this.message = message;
        this.port = port;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return message;
    }

    public String getAddress() {
        return address;
    }
}
