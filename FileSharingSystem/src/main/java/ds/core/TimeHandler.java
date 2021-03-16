package main.java.ds.core;

import main.java.ds.Handlers.TimeoutCallback;
import main.java.ds.core.CallbackMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TimeHandler {

    public static final String R_PING_MESSAGE_ID = "rPingMessage";

    private final Logger LOG = Logger.getLogger(TimeHandler.class.getName());
    private Map<String, CallbackMap> requests = new HashMap<String, CallbackMap>();

    public void registering_Request(String messaageId, long timeout, TimeoutCallback callback) {
        requests.put(messaageId, new CallbackMap(timeout, callback));
    }

    public void registering_Response(String messageId) {
        LOG.fine("Response is being registered : " + messageId);
        requests.remove(messageId);
    }

    // public void checkForTimeout() {
    public void remove_TimeOut_Messages() {
        ArrayList<String> temporary = new ArrayList<>();
        for (String msg_ID: requests.keySet()) {
            if(requests.get(msg_ID).checkTimeout(msg_ID)) {
                if(msg_ID.equals(R_PING_MESSAGE_ID)) {
                    requests.get(msg_ID).timeoutTime = requests.get(msg_ID).timeoutTime
                            + requests.get(msg_ID).timeout;
                }else {
                    temporary.add(msg_ID);
                }
            }
        }
        for (String messageId: temporary) {
            requests.remove(messageId);
        }
    }
}
