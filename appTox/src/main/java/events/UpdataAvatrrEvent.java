package events;

public class UpdataAvatrrEvent {

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public UpdataAvatrrEvent(String filePath) {
        this.filePath = filePath;
    }
}
