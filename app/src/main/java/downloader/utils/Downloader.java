package downloader.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Locale;

import app.exploitr.nsg.youp3.DownloadsActivity;
import downloads.DownloadHistoryManager;
import downloads.RealmController;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import utils.DataStore;
import utils.Helper;
import utils.IPC;


/* To be called from DownloadExecutorService */

//todo fix some videos.
class Downloader {

    static void download(Context context, QueueObject object) {
        Queue.getInstance().remove(object.getId());
        final int FINAL_ID = object.getId();
        new RxDownloader(context).download(
                createRequest(
                        context,
                        object.getUrl(),
                        object.getName() + "." + object.getExt()
                ))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        new DownloadHistoryManager()
                                .push(FINAL_ID,
                                        "Downloading Now.",
                                        object.getName(),
                                        "0 MB",
                                        false,
                                        object.getYtUrl()
                                );
                        makeShortToast("Download Started", context);
                        EventBus.getDefault().post(new IPC());
                    }

                    @Override
                    public void onNext(String finalFilePath) {
                        File content = new File(Uri.parse(finalFilePath).getPath());
                        new DownloadHistoryManager()
                                .push(FINAL_ID,
                                        content.getAbsolutePath(),
                                        object.getName(),
                                        String.format(Locale.US, "%.2f", Helper.fileLen(content, 2)) + "MB",
                                        true,
                                        object.getYtUrl()
                                );
                        EventBus.getDefault().post(new IPC());
                    }

                    @Override
                    public void onError(Throwable e) {
                        makeShortToast("Can't Download : Connection Error For ID :" + FINAL_ID, context);
                        if (new RealmController().getVideoInfo(FINAL_ID) != null) {
                            new DownloadHistoryManager().removeInfoById(FINAL_ID);
                        }
                        EventBus.getDefault().post(new IPC());
                    }

                    @Override
                    public void onComplete() {
                        makeShortToast("Complete", context);
                    }
                });
    }

    private static void makeShortToast(String text, Context mContext) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show());
    }

    private static DownloadManager.Request createRequest(Context context, @NonNull String url, @NonNull String filename) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(filename)
                .setTitle("Downloading Now")
                .setMimeType("*/*")
                .setDestinationInExternalPublicDir(
                        /*
                         *  /storage/emulated/0/Download/
                         *  /Download/
                         * */
                        DataStore.getInstance(context).getPathDownload()
                                .replace(
                                        Environment.getExternalStorageDirectory().getAbsolutePath(), ""),
                        filename)
                .setNotificationVisibility(1);
        request.allowScanningByMediaScanner();
        return request;
    }

}
