package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by exploitr on 15-08-2017.
 *
 */

public class DataStore {

    private final SharedPreferences dataStore;

    private static final String FULLSCREEN = "full";
    private static final String REMOVETOOLBAR = "reamer";
    private static final String DESKTOPMODE   = "desk";
    private static final String AUTOSEARCH = "autoSch";

    public DataStore(Context mContext) {
        dataStore = mContext.getSharedPreferences("youp3data", Context.MODE_PRIVATE);
    }

    /* this → just to make my eyes feel good */


    public boolean isFullScreen() {
        return dataStore.getBoolean(FULLSCREEN, false);
    }

    public void setFullScreen(boolean fullScreen) {
        dataStore.edit().putBoolean(FULLSCREEN, fullScreen).apply();
    }


    /* this → just to make my eyes feel good */

    public boolean isRemoveToolbar() {
        return dataStore.getBoolean(REMOVETOOLBAR, false);
    }

    public void setRemoveToolbar(boolean removeToolbar) {
        dataStore.edit().putBoolean(REMOVETOOLBAR, removeToolbar).apply();
    }


    /* this → just to make my eyes feel good */

    public boolean isDeskModeEnabled() {
        return dataStore.getBoolean(DESKTOPMODE, false);
    }

    public void setDeskModeEnabled(boolean what) {
        dataStore.edit().putBoolean(DESKTOPMODE, what).apply();
    }

    /* this → just to make my eyes feel good */

    public boolean isAutoSearchEnabled() {
        return dataStore.getBoolean(AUTOSEARCH, false);
    }

    public void setAutoSearchEnabled(boolean what) {
        dataStore.edit().putBoolean(AUTOSEARCH, what).apply();
    }

}
