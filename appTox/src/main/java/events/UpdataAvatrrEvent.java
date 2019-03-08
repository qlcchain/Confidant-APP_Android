package events;

public class UpdataAvatrrEvent {

    private String filePath;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    private boolean complete;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UpdataAvatrrEvent(String filePath,boolean complete) {
        this.filePath = filePath;
        this.complete = complete;
    }
}
