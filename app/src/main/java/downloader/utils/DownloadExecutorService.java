package downloader.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

import app.exploitr.nsg.youp3.DownloadsActivity;
import app.exploitr.nsg.youp3.MainActivity;
import app.exploitr.nsg.youp3.R;
import io.realm.Realm;
import utils.Helper;

public class DownloadExecutorService extends Service {

    public static final String download_update_intent = "_download_update";
    Notification notification;
    Notification.Builder builder;
    private Handler handler;
    private BroadcastReceiver downloadServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), download_update_intent)) {
                if (Helper.isDownloadInActive() && !MainActivity.isActive) {
                    stopForeground(true);
                    stopSelf();
                    NotificationManagerCompat.from(getApplicationContext()).cancelAll();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Realm.init(getApplicationContext());
        if (Helper.isDownloadInActive() && !MainActivity.isActive) {
            stopForeground(true);
            stopSelf();
            NotificationManagerCompat.from(getApplicationContext()).cancelAll();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(DownloadExecutorService.this, DownloadsActivity.class), 0);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadServiceReceiver, new IntentFilter(download_update_intent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "download_check_channel_id";
            CharSequence channelName = "Download Check Notification";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new Notification.Builder(this.getBaseContext(), notificationChannel.getId())
                    .setContentTitle("Background Download Service Running")
                    .setContentText("Auto-Close on complete")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);
            notification = builder.build();
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.FOREGROUND_SERVICE) ==
                    PackageManager.PERMISSION_GRANTED) {
                startForeground(12, notification);
            } else {
                Toast.makeText(this, "Service Permission Denial", Toast.LENGTH_SHORT).show();
            }
        } else {
            builder = new Notification.Builder(this)
                    .setContentTitle("Background Download Service Running")
                    .setContentText("Auto-Close on complete")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            notification = builder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            startForeground(11, notification);
        }
        startChecking();
        return START_STICKY;
    }

    private void startChecking() {
        handler = new Handler();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (QueueObject object : Queue.getInstance().getAvailableQueues()) {
                    if (object.isBasic()) {
                        new BasicDownloader(DownloadExecutorService.this).setQueueObject(object).start();
                    } else {
                        new AdvancedDownloader(DownloadExecutorService.this).setQueueObject(object).start();
                    }
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadServiceReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
