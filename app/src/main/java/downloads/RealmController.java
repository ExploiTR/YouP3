package downloads;

/*
 * Created by exploitr on 27-09-2017.
 */

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import java.util.Objects;

import app.exploitr.nsg.youp3.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;

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
                realm.where(VideoInfo.class).findAll().deleteAllFromRealm(); //TODO impl
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
            Objects.requireNonNull(realm.where(VideoInfo.class).equalTo("id", id).findFirst()).deleteFromRealm();
        } catch (Exception ex) {
            ex.getSuppressed();
            ex.printStackTrace();
            if (MainActivity.isBuildFinal) {
                Crashlytics.logException(ex);
            }
        }
        realm.commitTransaction();
    }

    public boolean hasVideoInfos() {
        return realm.where(VideoInfo.class).findAll().size() != 0;
    } //TODO check

}
