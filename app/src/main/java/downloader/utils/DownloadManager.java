package downloader.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import app.exploitr.nsg.youp3.BottomSheetFragment;
import app.exploitr.nsg.youp3.R;
import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import downloads.FileHolder;
import utils.Helper;


public class DownloadManager {

    private static boolean toggleAdvDownload = false;
    private static final String EXT_BASIC = "webm";

    public void startAdvanceDownloadProcess(String ytUrl, @NotNull Activity activity, FragmentManager fragmentManager) {
        View pro = activity.getLayoutInflater().inflate(R.layout.progress_modal_layout, null);
        AlertDialog ar = new AlertDialog.Builder(activity)
                .setView(pro)
                .setCancelable(false)
                .show();
        pro.findViewById(R.id.dismiss).setOnClickListener(v -> {
            toggleAdvDownload = true;
            ar.cancel();
        });
        new YouTubeExtractor(activity) {
            @Override
            public void onExtractionComplete(final SparseArray<YtFile> ytFiles, final VideoMeta vMeta) {
                /*
                 * fix sudden stuck*/
                if (toggleAdvDownload) {
                    toggleAdvDownload = false;
                    ar.cancel();
                    return;
                }

                if (ytFiles == null || vMeta == null) {
                    Toast.makeText(activity, "Error processing url - Please retry", Toast.LENGTH_SHORT).show();
                    return;
                }

                /* Downloads will start here @ code redirection */
                BottomSheetFragment fragment = new BottomSheetFragment().prepare(vMeta, ytUrl);

                /*
                 *  480 : null : -1 : 30 : 135 : null : mp4      .getHeight()
                 *  720 : null : -1 : 30 : 136 : null : mp4      .getVideoCodec()
                 *  -1 : null : 128 : 30 : 140 : null : m4a      .getAudioBitrate()
                 *  144 : null : -1 : 30 : 160 : null : mp4      .getFps()
                 *  -1 : null : 128 : 30 : 171 : null : webm     .getItag()
                 *  240 : null : -1 : 30 : 242 : null : webm     .getAudioCodec()
                 *  360 : null : -1 : 30 : 243 : null : webm     .getExt()
                 *  */
                for (int i = 0; i < ytFiles.size(); i++) {
                    int key = ytFiles.keyAt(i);
                    if (ytFiles.get(key) != null) {
                        Format format = ytFiles.get(key).getFormat();
                        if (format.getHeight() != -1 && format.getAudioBitrate() != -1) {
                            fragment.setupDialogData(ytFiles.get(key), FileHolder.TYPE_VIDEO);
                        } else if (format.getHeight() != -1 && format.getAudioBitrate() == -1) {
                            fragment.setupDialogData(ytFiles.get(key), FileHolder.TYPE_VIDEO_SILENT);
                        } else if (format.getHeight() == -1 && format.getAudioBitrate() != -1) {
                            fragment.setupDialogData(ytFiles.get(key), FileHolder.TYPE_AUDIO);
                        }
                    } else {
                        ytFiles.remove(key);
                    }
                }

                fragment.show(fragmentManager, fragment.getTag());
                ar.cancel();
            }
        }.extract(ytUrl, true, true);
    }

    public void startBasicDownloadProcess(Activity mContext, String url, String urlTitle, String ytUrl) {
        List<String> list = Arrays.asList(
                "FileName : " + Helper.getTitle(mContext, urlTitle),
                "Path : " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        CharSequence[] cs = list.toArray(new CharSequence[0]);

        new AlertDialog.Builder(mContext)
                .setTitle("Download Audio")
                .setItems(cs, null)
                .setPositiveButton("Okay", (dialog, which) -> {

                    /* Ask user for download filename */
                    View fileName = mContext.getLayoutInflater().inflate(R.layout._download_filename, null);
                    EditText file = fileName.findViewById(R.id.fileName);
                    file.setText(Helper.getFilenameFromString(urlTitle));

                    new AlertDialog.Builder(mContext)
                            .setView(fileName)
                            .setPositiveButton("Ok", (dialog1, which1) -> {

                                QueueObject object = new QueueObject();
                                object.setId(new SecureRandom().nextInt(1024));
                                object.setName(file.getText().toString());
                                object.setUrl(url);
                                object.setExt(EXT_BASIC);
                                object.setYtUrl(ytUrl);

                                Queue.getInstance().add(object);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
