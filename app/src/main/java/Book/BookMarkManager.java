package Book;

/*
 * Created by exploitr on 25-09-2017.
 *
 * I've created a new instance for each getters.
 * I don't know but think If the data is updated at the middle,
 * an old instance wouldn't return the updated data
 */

import io.realm.Realm;

public class BookMarkManager {

    private Realm realm;

    public BookMarkManager() {
        realm = Realm.getDefaultInstance();
    }

    private BookRealmController getController() {
        return new BookRealmController();
    }

    public void push(int id, String title, String url, String favicon) {
        BookMark info = new BookMark();

        info.setId(id);
        info.setTitle(title);
        info.setUrl(url);
        info.setFavicon(favicon);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();
    }


    public String getTitleById(int id) {
        return getController().getBookMark(id).getTitle();
    }

    public int getTotalInfoCount() {
        return getController().getBookMarks().size();
    }

    public void removeInfoById(int id) {
        getController().deleteBookMark(id);
    }
}
