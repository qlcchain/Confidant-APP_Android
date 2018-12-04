package events;

public class ToxStatusEvent {
    //连接状态，0已经连接，1正在连接，2未连接,3网络错误
    private int status;
    public ToxStatusEvent(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
