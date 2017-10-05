package model;

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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
