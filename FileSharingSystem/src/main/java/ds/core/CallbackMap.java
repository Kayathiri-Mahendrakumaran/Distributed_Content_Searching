package  ds.core;

import  ds.Handlers.*;

public class CallbackMap {
    public long timeoutTime;
    private TimeoutCallback callback;
    public long timeout;

    public CallbackMap(long timeout, TimeoutCallback callback) {
        this.timeout = timeout;
        this.callback = callback;
        this.timeoutTime = System.currentTimeMillis() + timeout;
    }
    public boolean checkTimeout(String messageID) {
        if (System.currentTimeMillis() >= timeoutTime) {
            callback.onTimeout(messageID);
            return true;
        }
        return false;
    }
}
