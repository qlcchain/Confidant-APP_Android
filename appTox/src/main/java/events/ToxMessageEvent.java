package events;

public class ToxMessageEvent {

    private String message;
    private String key;
    public ToxMessageEvent(String message,String key) {
        this.message = message;
        this.key = key;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
