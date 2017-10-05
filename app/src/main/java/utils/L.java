package utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebStorage;
import android.webkit.WebView;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static app.exploitr.nsg.youp3.MainActivity.isConnectionFast;

/*
 * Created by exploitr on 01-10-2017.
 */

public class L {

    public static void verb(String what) {
        Log.v("YouP3", what);
    }

    public static int getFileSizeFromUrl(final String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                HttpURLConnection connection;
                long contentLength = (long) Math.pow(10, 6);
                try {
                    connection = (HttpURLConnection) (new URL(url)).openConnection();
                    if (connection != null) {
                        connection.connect();
                    }

                    if (connection != null) {
                        contentLength = Long.parseLong(connection.getHeaderField("Content-Length"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    e.getSuppressed();
                }

                return (int) (contentLength / Math.pow(10, 6));
            }
        };
        Future<Integer> future = executor.submit(callable);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            e.getSuppressed();
            return 0;
        }
    }

    /*public float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }*/

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isConnectedFast(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }

    /**
     * @param uri Pass file uri
     * @return typeString
     */
    public static String getMimeType(String uri) {
        String type = null;
        String extension = FilenameUtils.getExtension(uri);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMail(Context mContext) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return "empty@nomail.com";
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(WebView mainView, boolean clearWebViewAlso) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } else {
                CookieManager.getInstance().removeAllCookie();
            }
        } catch (Exception c) {
            c.printStackTrace();
        }

        WebStorage.getInstance().deleteAllData();

        if (clearWebViewAlso) {
            if (mainView != null) {
                verb("Clearing all");
                mainView.stopLoading();
                mainView.clearHistory();
                mainView.clearFormData();
                mainView.clearSslPreferences();
                mainView.clearCache(true);
                mainView.clearMatches();
            }
        }
    }
}
