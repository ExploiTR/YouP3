package realm;

/*
 * Created by exploitr on 27-09-2017.
 */

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import io.realm.Realm;
import io.realm.RealmResults;
import model.VideoInfo;

public class RealmController {

    private final Realm realm;

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

    public void clearAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(VideoInfo.class).findAll().deleteAllFromRealm();
            }
        });
    }

    public RealmResults<VideoInfo> getVideoInfos() {
        return realm.where(VideoInfo.class).findAll();
    }

    public VideoInfo getVideoInfo(int id) {
        return realm.where(VideoInfo.class).equalTo("id", id).findFirst();
    }

    public void deleteVideoInfo(int id) {
        realm.beginTransaction();
        try {
            realm.where(VideoInfo.class).equalTo("id", id).findFirst().deleteFromRealm();
        } catch (Exception ex) {
            ex.getSuppressed();
            ex.printStackTrace();
            Crashlytics.logException(ex);
        }
        realm.commitTransaction();
    }

    public boolean hasVideoInfos() {
        return realm.where(VideoInfo.class).findAll().size() != 0;
    }


}
