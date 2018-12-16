package downloader.utils;

@SuppressWarnings("WeakerAccess")
public class QueueObject {

    private String url,ytUrl, name, size, ext;
    private int id;
    private boolean basic,de_fault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isBasic() {
        return basic;
    }

    public void setBasic(boolean basic) { //!basic = advanced
        this.basic = basic;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getYtUrl() {
        return ytUrl;
    }

    public void setYtUrl(String ytUrl) {
        this.ytUrl = ytUrl;
    }

    public boolean isDe_fault() {
        return de_fault;
    }

    public void setDe_fault(boolean de_fault) {
        this.de_fault = de_fault;
    }
}
