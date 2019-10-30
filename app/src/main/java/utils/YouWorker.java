package utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import app.exploitr.nsg.youp3.ui_access.MainActivity;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class YouWorker extends MultiDexApplication {
    
    public static boolean IS_CONVERSION_SUPPORTED = false;
    @Override
    public void onCreate() {

        if (MainActivity.isBuildFinal) {
            Fabric.with(this, new Crashlytics());
            setUserForLogging();
        }
    
        /*Initializing Realm*/
        Realm.init(this);
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
        });
    }

    private void setUserForLogging() {
        Crashlytics.setUserName(Build.DEVICE);
        Crashlytics.setUserIdentifier(Build.FINGERPRINT);
    }


}
