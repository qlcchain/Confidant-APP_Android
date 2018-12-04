package events;

public class ToxMessageEvent {

    private String message;
    public ToxMessageEvent(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
