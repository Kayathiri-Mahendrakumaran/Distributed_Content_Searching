package  ds.core;

import java.util.logging.Logger;
import java.util.HashMap;
import ds.Handlers.TimeoutCallback;
import java.util.Map;
import java.util.ArrayList;


public class TimeHandler {

    public static final String R_PING_MESSAGE_ID = "rPingMessage";

    private final Logger LOGGER = Logger.getLogger(TimeHandler.class.getName());
    private Map<String, CallbackMap> requests = new HashMap<String, CallbackMap>();

    public void registering_Request(String messageId, TimeoutCallback callback, long timeout) {
        requests.put(messageId, new CallbackMap(timeout, callback));
    }

    public void registering_Response(String messageId) {
        LOGGER.fine("Response is being registered : " + messageId);
        requests.remove(messageId);
    }

    public void remove_TimeOut_Messages() {

        ArrayList<String> tempArray = new ArrayList<>();

        for (String mesg_Id: requests.keySet()) {

            if(requests.get(mesg_Id).checkTimeout(mesg_Id)) {
                if(mesg_Id.equals(R_PING_MESSAGE_ID)) {

                    requests.get(mesg_Id).timeoutTime = requests.get(mesg_Id).timeoutTime + requests.get(mesg_Id).timeout;

                }else {

                    tempArray.add(mesg_Id);
                }
            }
        }
        // Removing done request
        for (String messageId: tempArray) {
            requests.remove(messageId);
        }
    }
}
