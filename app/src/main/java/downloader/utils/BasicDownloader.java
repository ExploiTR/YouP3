package downloader.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.security.SecureRandom;

import downloads.DownloadHistoryManager;
import downloads.RealmController;
import utils.Helper;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class BasicDownloader {

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

    private BasicDownloader(Context context) {
        this.FINAL_ID = new SecureRandom().nextInt(1024);
        this.mContext = context;
    }

    public static BasicDownloader getInstance(Context context) {
        return new BasicDownloader(context);
    }

    public BasicDownloader setQueueObject(QueueObject object) {
        this.downloadObject = object;
        return this;
    }

    public void start() {
        link = downloadObject.getUrl();
        title = downloadObject.getName();
        file_size = downloadObject.getSize();
        DOWNLOAD_ID = downloadObject.getId();

        Queue.getInstance().remove(DOWNLOAD_ID);

        title = Helper.getFilenameFromString(title);

        if (isNull(title, extension, link, file_size)) {
            makeShortToast("Download Error!");
        } else {
            finalFile = new File(Environment.getExternalStorageDirectory() + "/YouP3/" + title + "." + extension);
            finalFileDir = new File(Environment.getExternalStorageDirectory() + "/YouP3/");
            if (finalFile.exists()) {
                finalFile.delete();
            }
            if (finalFileDir.exists() || finalFileDir.mkdir()) {
                FINAL_ID = PRDownloader.download(link, finalFileDir.getAbsolutePath(), title + "." + extension)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                makeShortToast("Download Placed!");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DOWNLOAD_STARTED_FAILED_UNKNOWN));
                                    }
                                }, 500);
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                makeShortToast("Download Canceled!");
                                if (new RealmController().getVideoInfo(FINAL_ID) != null) {
                                    new DownloadHistoryManager().removeInfoById(FINAL_ID);
                                }
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                makeShortToast("Download Paused!");
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progressZero) {
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
                                                    percent
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
                                                100
                                        );
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(DOWNLOAD_STARTED_FAILED_UNKNOWN));
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
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
