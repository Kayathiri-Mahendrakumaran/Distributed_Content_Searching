package  ds.Handlers;

public interface TimeoutCallback {
    void onTimeout(String messageId);
    void onResponse(String messageId);
}
