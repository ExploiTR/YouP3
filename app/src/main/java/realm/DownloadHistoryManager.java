package realm;

/*
 * Created by exploitr on 25-09-2017.
 *
 * I've created a new instance for each getters.
 * I don't know but think If the data is updated at the middle,
 * an old instance wouldn't return the updated data
 */

import org.apache.commons.io.FilenameUtils;

import io.realm.Realm;
import model.VideoInfo;

public class DownloadHistoryManager {


    private Realm realm;

    public DownloadHistoryManager() {
        realm = Realm.getDefaultInstance();
    }

    private RealmController getController() {
        return new RealmController();
    }

    public void push(int id, String path,String name, boolean completed, String sizeWithUnit) {

        VideoInfo info = new VideoInfo();

        info.setId(id);
        /*IDK but link would be the same*/
        info.setPath(path);
        info.setName(name);
        info.setCompleted(completed);
        info.setSize(sizeWithUnit);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();

    }


    public String getFilePathById(int id) {
        return getController().getVideoInfo(id).getPath();
    }

    public String getFileNameById(int id) {
        return FilenameUtils.getBaseName(getController().getVideoInfo(id).getPath());
    }

    public int getTotalInfoCount() {
        return getController().getVideoInfos().size();
    }


    public boolean isDownloadCompleted(int id) {
        return getController().getVideoInfo(id).isCompleted();
    }

    public String getSize(int id) {
        return getController().getVideoInfo(id).getSize();
    }

    public void removeInfoById(int id) {
        getController().deleteVideoInfo(id);
    }
}
