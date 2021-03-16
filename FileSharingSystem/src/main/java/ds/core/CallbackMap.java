package main.java.ds.core;

import main.java.ds.Handlers.TimeoutCallback;

public class CallbackMap {
    private long timeoutTime;
    private TimeoutCallback callback;
    private long timeout;

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
