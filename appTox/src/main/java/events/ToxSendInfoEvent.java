package events;

public class ToxSendInfoEvent {
    private String info;
    public ToxSendInfoEvent(String info) {
        this.info = info;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
}
