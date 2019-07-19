package Book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.SparseArray;

import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class VideoScreen {

    private static VideoScreen screen;
    private completeListener listener;
    private String bow = "data:image/jpeg;base64,R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs=";
    private String url;

    private VideoScreen(String urlZero) {
        this.url = urlZero;
    }

    public static VideoScreen load(String urlOne) {
        screen = new VideoScreen(urlOne);
        return screen;
    }

    @SuppressLint("StaticFieldLeak")
    public VideoScreen start(final Context context) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                AsyncTask.execute(() -> {
                    try {
                        Bitmap bacterial = Ion.with(context).load(vMeta.getThumbUrl()).asBitmap().get();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bacterial.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        bow = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        listener.onComplete(bow);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        listener.onComplete(bow);
                    }
                });
            }
        }.extract(url, false, false);
        screen.url = null;
        return this;
    }

    public void setOnCompleteListener(completeListener completeListener) {
        this.listener = completeListener;
    }

    public interface completeListener {
        void onComplete(String what);
    }
}
