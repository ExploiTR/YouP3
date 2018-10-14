package Book;

/*
 * Created by exploitr on 27-09-2017.
 */

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import java.util.Objects;

import app.exploitr.nsg.youp3.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;

public class BookRealmController {

    private final Realm realm;

    public BookRealmController() {
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

    public void clearAll() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(BookMark.class).findAll().deleteAllFromRealm(); //TODO impl
            }
        });
    }

    public RealmResults<BookMark> getBookMarks() {
        return realm.where(BookMark.class).findAll();
    }

    public BookMark getBookMark(int id) {
        return realm.where(BookMark.class).equalTo("id", id).findFirst();
    }

    public void deleteBookMark(int id) {
        realm.beginTransaction();
        try {
            Objects.requireNonNull(realm.where(BookMark.class).equalTo("id", id).findFirst()).deleteFromRealm();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (MainActivity.isBuildFinal) {
                Crashlytics.logException(ex);
            }
        }
        realm.commitTransaction();
    }

    public boolean hasBookMarks() {
        return realm.where(BookMark.class).findAll().size() != 0;
    } //TODO check

}

