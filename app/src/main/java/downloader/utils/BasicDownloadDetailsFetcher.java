package downloader.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import app.exploitr.nsg.youp3.R;
import utils.Helper;

public class BasicDownloadDetailsFetcher extends AsyncTask<String, Void, JSONObject> {

    private String url, urlTitle, ytUrl;
    private WeakReference<Activity> mContext;
    private AlertDialog ar;

    public BasicDownloadDetailsFetcher(String longUrl, String ytURL, String longTitle, Activity context) {
        urlTitle = longTitle;
        ytUrl = ytURL;
        url = longUrl;
        mContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        ar = new AlertDialog.Builder(mContext.get())
                .setView(R.layout.progress_modal_layout)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        final JSONObject object = new JSONObject();
        try {
            object.put("size", Helper.getFileSizeFromUrl(url));
            object.put("title", Helper.getTitle(mContext.get(), urlTitle));
        } catch (JSONException se) {
            se.printStackTrace();
        }
        return object;
    }

    @Override
    protected void onPostExecute(final JSONObject container) {
        ar.dismiss();
        String sizeMb = "0", title = "";
        try {
            sizeMb = container.get("size").toString();
            title = container.get("title").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> list = Arrays.asList(
                "FileName : " + title,
                "File Size : " + sizeMb + "MB",
                "Path : " + Environment.getExternalStorageDirectory() + "/YouP3/");
        CharSequence[] cs = list.toArray(new CharSequence[0]);

        final String finalTitle = title;//copy final temp var
        final String finalSizeMb = sizeMb;//copy final temp var

        new AlertDialog.Builder(mContext.get())
                .setTitle("Download Audio")
                .setItems(cs, null)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        QueueObject object = new QueueObject();
                        object.setId(new SecureRandom().nextInt(1024));
                        object.setName(finalTitle);
                        object.setSize(String.valueOf(finalSizeMb) + "MB");
                        object.setUrl(url);
                        object.setYtUrl(ytUrl);
                        object.setBasic(true);

                        Queue.getInstance().add(object);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}