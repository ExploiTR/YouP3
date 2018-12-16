package utils;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static utils.Helper.verb;

public class FFMpegWrapper {

    private static FFMpegWrapper wrapper;
    private static String duration = "00:00:00:00";
    private static String current = "00:00:00:00";

    public static FFMpegWrapper getWrapper() {
        if (wrapper != null)
            return wrapper;
        else
            wrapper = new FFMpegWrapper();
        return wrapper;
    }

    private static File getConvertedFile(File originalFile) {
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], "mp3");
        return new File(filePath);
    }

    private static long getDurationTimeMills() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse(duration);
        return date.getTime();
    }

    private static long getCurrentTimeMills() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse(current);
        return date.getTime();
    }

    private static int getProgressInternal() throws ParseException {
        return (int) (((float) (getCurrentTimeMills() * 100)) / getDurationTimeMills());
    }

    public boolean isRunning(Context mContext) {
        FFmpeg ffmpeg = FFmpeg.getInstance(mContext);
        try {
            ffmpeg.execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onSuccess(String message) {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            return true;
        }
        return false;
    }

    public String getWebmToMp3Command(File webm) {
        return "-y -i " + webm.getAbsolutePath() + " " + getConvertedFile(webm)
                + " -preset ultrafast -codec:a libmp3lame -qscale:a 0";
    }

    /* Duration: 00:06:00.34, start: 0.007000, bitrate: 133 kb/s
     * size=59kB time=00:00:03.74 bitrate= 129.3kbits/s speed=1.23x
     */
    public int getProgress(String message) throws ParseException {
        if (message.contains("Duration")) {
            duration = message.substring(12, 23).replaceAll("\\s+", "");
            verb(duration);
        }
        if (message.contains("time=")) {
            current = message.split("time=")[1].split("bitrate")[0].replaceAll("\\s+", "");
            verb(current);
        }
        return getProgressInternal();
    }

    public void eraseInstance() {
        wrapper = null;
    }

}
