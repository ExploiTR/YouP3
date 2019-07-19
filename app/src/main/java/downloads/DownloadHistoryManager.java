package downloads;

/*
 * Created by exploitr on 25-09-2017.
 *
 * I've created a new instance for each getters.
 * I don't know but think If the data is updated at the middle,
 * an old instance wouldn't return the updated data
 */

import io.realm.Realm;

public class DownloadHistoryManager {

    private Realm realm;


    public DownloadHistoryManager() {
        realm = Realm.getDefaultInstance();
    }

    private RealmController getController() {
        return new RealmController();
    }

    public void push(int id, String path, String name, String sizeWithUnit, boolean complete, String ytUrl) {

        VideoInfo info = new VideoInfo();

        info.setId(id);
        /*IDK but link would be the same*/
        info.setPath(path);
        info.setName(name);
        info.setSize(sizeWithUnit);
        info.setCompleted(complete);
        info.setYtUrl(ytUrl);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();

    }

    public int getTotalInfoCount() {
        return getController().getVideoInfos().size();
    }

    public void removeInfoById(int id) {
        getController().deleteVideoInfo(id);
    }
}
