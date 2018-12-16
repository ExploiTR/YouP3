package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by exploitr on 15-08-2017.
 */

public class DataStore {

    private static final String FULLSCREEN = "full";
    private static final String REMOVETOOLBAR = "reamer";
    private static final String DESKTOPMODE = "desk";
    private static final String CONV_SUPPORTED = "adrielcafe";
    private static final String NEW_USER = "new_userid";
    private static final String DOWNLOAD_MODE = "mode_of_download";
    private static final String PATH_DOWNLOAD = "_path_download";
    private static final String USE_DEFAULT_DOWNLOADER = "_def_ok";

    private static DataStore instance;
    private final SharedPreferences dataStore;


    private DataStore(Context mContext) {
        dataStore = mContext.getSharedPreferences("youp3data", Context.MODE_PRIVATE);
    }

    public static DataStore getInstance(Context mContext) {
        if (instance != null) {
            return instance;
        } else {
            instance = new DataStore(mContext);
            return instance;
        }
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

    public boolean isConversionSupported() {
        return dataStore.getBoolean(CONV_SUPPORTED, false);
    }

    public void setConversionSupported(boolean what) {
        dataStore.edit().putBoolean(CONV_SUPPORTED, what).apply();
    }

    /* this → just to make my eyes feel good */

    public boolean isNewUser() {
        return dataStore.getBoolean(NEW_USER, true);
    }

    public void setNewUserNoMore() {
        dataStore.edit().putBoolean(NEW_USER, false).apply();
    }

    /* this → just to make my eyes feel good */

    public boolean isAdvancedDownloadMode() {
        return dataStore.getBoolean(DOWNLOAD_MODE, false);
    }

    public void setAdvancedDownloadMode(boolean what) {
        dataStore.edit().putBoolean(DOWNLOAD_MODE, what).apply();
    }

    /* this → just to make my eyes feel good */

    public String getPathDownload() {
        return dataStore.getString(PATH_DOWNLOAD, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
    }

    public void setPathDownload(String what) {
        dataStore.edit().putString(PATH_DOWNLOAD, what).apply();
    }

    /* this → just to make my eyes feel good */

    public boolean shouldUseDefaultDownloader() {
        return dataStore.getBoolean(USE_DEFAULT_DOWNLOADER, false);
    }

    public void setShouldUseDefaultDownloader(boolean what) {
        dataStore.edit().putBoolean(USE_DEFAULT_DOWNLOADER, what).apply();
    }
}
