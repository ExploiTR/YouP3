package Book;

/*
 * Created by exploitr on 27-09-2017.
 */
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

    public RealmResults<BookMark> getBookMarks() {
        return realm.where(BookMark.class).findAll();
    }

    BookMark getBookMark(int id) {
        return realm.where(BookMark.class).equalTo("id", id).findFirst();
    }

    void deleteBookMark(int id) {
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

}

