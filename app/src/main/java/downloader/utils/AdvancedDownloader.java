package downloader.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import downloads.DownloadHistoryManager;
import downloads.RealmController;
import utils.DataStore;
import utils.Helper;
import utils.SuperToast;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class AdvancedDownloader {

    public static final String DOWNLOAD_STARTED_FAILED_UNKNOWN = "!_started_!";
    public static final String DOWNLOAD_PROGRESS_CHANGED = "!_progress_changed_!";
    private static long curTime = System.currentTimeMillis();
    private static long elapsedTime = 0;
    private QueueObject downloadObject;
    private Context mContext;
    private int FINAL_ID;
    private int DOWNLOAD_ID;
    private String title;
    private String extension = "mp3";
    private String link;
    private String file_size;
    private File finalFile;
    private File finalFileDir;

    public AdvancedDownloader(Context context) {
        this.FINAL_ID = new SecureRandom().nextInt(1024);
        this.mContext = context;
    }

    public AdvancedDownloader setQueueObject(QueueObject object) {
        this.downloadObject = object;
        return this;
    }

    public void start() {
        link = downloadObject.getUrl();
        title = downloadObject.getName();
        file_size = downloadObject.getSize();
        DOWNLOAD_ID = downloadObject.getId();
        extension = downloadObject.getExt();

        Queue.getInstance().remove(DOWNLOAD_ID);

        title = Helper.getFilenameFromString(title);
        finalFile = new File(DataStore.getInstance(mContext).getPathDownload() + "/" + title + "." + extension);
        finalFileDir = new File(DataStore.getInstance(mContext).getPathDownload());

        if (finalFile.exists()) {
            finalFile.delete();
        }

        try {
            finalFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (DataStore.getInstance(mContext).shouldUseDefaultDownloader()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
            request.setDescription(title);
            request.setTitle("Downloading Now");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationUri(Uri.fromFile(finalFile));
            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            Toast.makeText(mContext, "Download Placed (DownloadManager)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNull(title, extension, link, file_size)) {
            makeShortToast("Download Error!");
        } else {
            if (finalFile.exists()) {
                finalFile.delete();
            }
            if (finalFileDir.exists() || finalFileDir.mkdir()) {
                FINAL_ID = PRDownloader.download(link, finalFileDir.getAbsolutePath(), title + "." + extension)
                        .build()
                        .setOnStartOrResumeListener(() -> {
                            makeShortToast("Download Placed!");
                            new Handler().postDelayed(() -> LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DOWNLOAD_STARTED_FAILED_UNKNOWN)), 500);
                        })
                        .setOnCancelListener(() -> {
                            makeShortToast("Download Canceled!");
                            if (new RealmController().getVideoInfo(FINAL_ID) != null) {
                                new DownloadHistoryManager().removeInfoById(FINAL_ID);
                            }
                        })
                        .setOnPauseListener(() -> makeShortToast("Download Paused!"))
                        .setOnProgressListener(progressZero -> {
                            if (elapsedTime > 1000) {

                                long current = progressZero.currentBytes;
                                long total = progressZero.totalBytes;

                                int percent = (int) ((float) current / total * 100);

                                new DownloadHistoryManager()
                                        .push(FINAL_ID,
                                                finalFile.getAbsolutePath(),
                                                title,
                                                file_size,
                                                false,
                                                percent,
                                                downloadObject.getYtUrl()
                                        );

                                Intent paw = new Intent(DOWNLOAD_PROGRESS_CHANGED);
                                paw.putExtra("progress", percent);
                                paw.putExtra("id", FINAL_ID);
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(paw);

                                elapsedTime = 0L;
                                curTime = System.currentTimeMillis();
                            } else {
                                elapsedTime = System.currentTimeMillis() - curTime;
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                new DownloadHistoryManager()
                                        .push(FINAL_ID,
                                                finalFile.getAbsolutePath(),
                                                title,
                                                file_size,
                                                true,
                                                100,
                                                downloadObject.getYtUrl()
                                        );
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DOWNLOAD_STARTED_FAILED_UNKNOWN));
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DownloadExecutorService.download_update_intent));
                                makeShortToast("Complete");
                            }

                            @Override
                            public void onError(Error error) {
                                if (error.isConnectionError()) {
                                    makeShortToast("Can't Download : Connection Error For ID :" + FINAL_ID);
                                } else if (error.isServerError()) {
                                    makeShortToast("Can't Download : Server Error For ID :" + FINAL_ID);
                                }
                                if (new RealmController().getVideoInfo(FINAL_ID) != null) {
                                    new DownloadHistoryManager().removeInfoById(FINAL_ID);
                                }
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DOWNLOAD_STARTED_FAILED_UNKNOWN));
                            }
                        });
            }
        }
    }

    private boolean isNull(String one, String two, String three, String four) {
        return TextUtils.isEmpty(one) && TextUtils.isEmpty(two) && TextUtils.isEmpty(three) && TextUtils.isEmpty(four);
    }

    private void makeShortToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperToast.makeText(mContext, text, Toast.LENGTH_SHORT);
            }
        });
    }
}
