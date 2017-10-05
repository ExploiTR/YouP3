package download.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

import realm.DownloadHistoryManager;
import utils.NotifyCall;

import static utils.L.getMimeType;

/**
 * Created by exploitr on 15-08-2017.
 * <p>
 * TODO
 */

public class Downloader {

    private Context mContext;
    private String title;
    private View mView;
    private String fileExtension;
    private int downloadId;

    public Downloader(String sTitle, String extension, Context context, View view) {

        downloadId = (int) (Math.ceil(System.nanoTime() / 10000) / 1000);

        this.mContext = context;
        this.title = sTitle.replaceAll("[^a-zA-Z0-9.-]", " ").replaceAll("^ +| +$|( )+", "$1").replace("\"", ""); //REGEX
        this.mView = view;
        this.fileExtension = extension;

        new DownloadHistoryManager().push(
                downloadId,
                Environment.getExternalStorageDirectory() + "/YouP3/" + title + "." + fileExtension,
                title,
                false,
                "0 MB");
    }


    public void checkAndGet(final String link, final String fileSize) {

        Ion.with(mContext)
                .load(link)
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        new NotifyCall(mContext, "Download Task Running", "Downloading :" + title).callProgress((int) total / 10000, (int) downloaded / 10000);
                    }
                })
                .group(downloadId) //Grouping
                .write(new File(Environment.getExternalStorageDirectory() + "/YouP3/" + title + "." + fileExtension))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, final File file) {

                        new DownloadHistoryManager().push(
                                downloadId,
                                new DownloadHistoryManager().getFilePathById(downloadId),
                                new DownloadHistoryManager().getFileNameById(downloadId),
                                true,
                                fileSize
                        );

                        new NotifyCall(mContext, "", "").cancelAll();

                        Snackbar.make(mView, "Download Completed : " + file.getName(), Snackbar.LENGTH_LONG).setAction("Play Now", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Uri path = Uri.fromFile(file);
                                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setDataAndType(path, getMimeType(path.toString()));

                                    PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                    new NotifyCall(mContext, "Download Finished ", file.getName()).removeProgressAndSetIntent(contentIntent);
                                    mContext.startActivity(Intent.createChooser(intent, "Open : " + file.getName()));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    ex.getSuppressed();
                                    Crashlytics.logException(ex.getCause());
                                }
                            }
                        }).show();
                    }
                });

    }

}
