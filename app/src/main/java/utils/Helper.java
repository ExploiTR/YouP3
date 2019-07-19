package utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.koushikdutta.ion.Ion;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import downloader.utils.Queue;
import downloads.RealmController;
import downloads.VideoInfo;

/*
 * Created by exploitr on 01-10-2017.
 */

public class Helper {

    private static final String API_KEY = "AIzaSyCcCIKRDOsePYwE88FZC5KNXG8KUpDr3oM"; //certificate restricted key

    public static void verb(@NonNull Object what) {
        Log.v("-_-", what.toString());
    }

    public static void verb(String tag, @NonNull Object what) {
        Log.v(tag, what.toString());
    }

    /*
     * Expecting byte-returns
     * So, for mb, 2 -> 1024/1024 -> kb/mb
     * */
    public static double fileLen(File path, int multiplier) {
        verb(path.length());
        verb(Math.pow(1024, multiplier));
        return path.length() / Math.pow(1024, multiplier);
    }

    public static String getTitle(Context context, String url) {
        String jsonContent = getJsonContent(context, getQueryString(url));
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            JSONObject object = jsonArray.getJSONObject(0);
            JSONObject snippet = object.getJSONObject("snippet");
            return Helper.getFilenameFromString(snippet.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        } else {
            return false;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @param uri Pass file uri
     * @return typeString
     */

    @SuppressWarnings("unused")
    public static String getMimeType(String uri) {
        String type = null;
        String extension = FilenameUtils.getExtension(uri);
        if (extension != null && !extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearCookies(WebView mainView, boolean clearWebViewAlso) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                // Oho useless lint, I've already did it
                CookieManager.getInstance().removeAllCookies(value -> {
                    if (value) {
                        CookieManager.getInstance().flush();
                    }
                });
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

    public static void clearCache(WebView mainView) {
        WebStorage.getInstance().deleteAllData();
        if (mainView != null) {
            verb("Clearing all");
            mainView.stopLoading();
            mainView.clearHistory();
            mainView.clearFormData();
            mainView.clearSslPreferences();
            mainView.clearCache(true);
            mainView.clearMatches();
            mainView.destroy();
        }
    }

    /**
     * @param blockedString Pass string with chars not allowed in a filename
     * @return fresh-And-Sweet String
     */
    public static String getFilenameFromString(String blockedString) {
        String paw = blockedString.replaceAll("[\\\\/:*?\"<>|-]", " ")
                .replaceAll(",", "")
                .trim()
                .replaceAll(" +", " ");
        return paw.replace("null", "");
    }


    public static boolean fileNameReady(String toString) {
        return !(toString.matches("[\\\\/:*?\"<>|]") || toString.contains("\0") || toString.contains("."));
    }

    private static String getQueryString(String videoUrl) {
        // "https://www.youtube.com/oembed?url=" + url + "&format=json"; another lengthy inefficient way
        String[] cow = videoUrl.split("v="); //https://www.youtube.com/watch? | SnnJg0jqr8A
        if (videoUrl.contains("&")) {  //https://www.youtube.com/watch?v= | SnnJg0jqr8A&app=desktop
            cow = cow[1].split("&"); //SnnJg0jqr8A | app=desktop
            videoUrl = cow[0];//SnnJg0jqr8A
            verb(videoUrl);
        } else {
            videoUrl = cow[1];//SnnJg0jqr8A
            verb(videoUrl);
        }
        return "https://www.googleapis.com/youtube/v3/videos?id=" + videoUrl + "&key=" +
                API_KEY +
                "&part=snippet";
    }

    private static String getJsonContent(Context context, String finalUrl) {
        try {
            String packageName = context.getPackageName();
            String SHA1 = getSHA1(context);

            return Ion.with(context)
                    .load(finalUrl)
                    .setHeader("X-Android-Package", packageName)
                    .setHeader("X-Android-Cert", SHA1)
                    .asString()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            return "error";
        }
    }

    private static String encode(byte[] byteArray) {
        char[] HEX = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        StringBuilder hexBuffer = new StringBuilder(byteArray.length * 2);
        for (byte aByteArray : byteArray)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(aByteArray >> (j * 4)) & 0xF]);
        return hexBuffer.toString();
    }

    @SuppressLint("PackageManagerGetSignatures") //minTargetApi 19
    private static String getSHA1(Context context) {
        try {
            Signature[] signatures = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            for (Signature signature : signatures) {
                md.update(signature.toByteArray());
            }
            return encode(md.digest());
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isDownloadInActive() {
        boolean ended = true;
        for (VideoInfo info : new RealmController().getVideoInfos()) {
            ended = info.isCompleted();
            if (!ended) break;
        }
        return Queue.getInstance().getTotalQueues() == 0 && ended;
    }
}
