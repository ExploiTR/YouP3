package Book;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BookMark extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String url;
    private String favicon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }
}
