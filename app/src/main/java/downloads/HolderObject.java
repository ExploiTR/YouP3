package downloads;

import at.huber.youtubeExtractor.YtFile;

public class HolderObject {
    private YtFile file;
    private int type;

    public HolderObject setFile(YtFile ytFile) {
        this.file = ytFile;
        return this;
    }

    public YtFile getYtFile() {
        return file;
    }

    public int getType() {
        return type;
    }

    public HolderObject setType(int typex) {
        this.type = typex;
        return this;
    }
}
