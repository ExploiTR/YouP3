package utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebStorage;
import android.webkit.WebView;

import com.koushikdutta.ion.Ion;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/*
 * Created by exploitr on 01-10-2017.
 */

public class Helper {

    private static final String API_KEY = "AIzaSyCcCIKRDOsePYwE88FZC5KNXG8KUpDr3oM"; //certificate restricted key

    public static void verb(@NonNull Object what) {
        Log.v("YouP3", what.toString());
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


    public static String getTitle(Context context, String url) {
        verb(url);
        String finalUrl = getQueryString(url);
        verb(finalUrl);
        String jsonContent = getJsonContent(context, finalUrl);
        verb(jsonContent);
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            JSONObject object = jsonArray.getJSONObject(0);
            JSONObject snippet = object.getJSONObject("snippet");
            String title = snippet.getString("title");
            verb(title);
            return Helper.getFilenameFromString(title);
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

   /* @unused
   public static boolean isConnectedFast(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info;
        if (connectivityManager != null) {
            info = connectivityManager.getActiveNetworkInfo();
        } else {
            return false;
        }
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }*/

    /**
     * @param uri Pass file uri
     * @return typeString
     */
    public static String getMimeType(String uri) {
        String type = null;
        String extension = FilenameUtils.getExtension(uri);
        if (extension != null && !extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /*public float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }*/

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

    public static void clearCookies(WebView mainView, boolean clearWebViewAlso) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                    @SuppressLint("NewApi") // Oho useless lint, I've already did it
                    @Override
                    public void onReceiveValue(Boolean value) {
                        if (value) {
                            CookieManager.getInstance().flush();
                        }
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
        String paw = blockedString.replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("[^a-zA-Z0-9.-]", " ")
                .replaceAll("^ +| +$|( )+", "$1")
                .replace("\"", "")
                .replace(",", ""); //REGEX;
        return paw.replace("null", "");
    }

    /* @unused
    private static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }*/

    private static String getQueryString(String videoUrl) {
        // "https://www.youtube.com/oembed?url=" + url + "&format=json";
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

    public static String getFormattedVideoItemString(int p, int kbps, int fps, String ext) {
        return (p == -1 ? "(Audio) " : "(Video) " + p + "P ") +
                (kbps == -1 ? "(Dash) " : " ") +
                fps +
                "fps " +
                "Ext : " +
                ext;
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
}
