package Book;

public class Launcher {
    private static Launcher launcher;
    private String url;
    private boolean shouldLoad;

    private Launcher() {
    }

    public static Launcher i() {
        if (launcher != null) {
            return launcher;
        } else {
            launcher = new Launcher();
            return launcher;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isShouldLoad() {
        return shouldLoad;
    }

    public void setShouldLoad(boolean shouldLoad) {
        this.shouldLoad = shouldLoad;
    }
}
