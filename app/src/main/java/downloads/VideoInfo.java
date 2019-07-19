package downloads;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
 * Created by exploitr on 27-09-2017.
 */

public class VideoInfo extends RealmObject {

    @PrimaryKey
    private int id;
    private String path;
    private String name;
    private String size;
    private String url;
    private String ytUrl;
    private boolean completed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getYtUrl() {
        return ytUrl;
    }

    public void setYtUrl(String ytUrl) {
        this.ytUrl = ytUrl;
    }
}
