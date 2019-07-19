package downloader.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import app.exploitr.nsg.youp3.DownloadsActivity;
import app.exploitr.nsg.youp3.MainActivity;
import app.exploitr.nsg.youp3.R;
import io.realm.Realm;
import utils.Helper;

public class DownloadExecutorService extends Service {

    Notification notification;
    Notification.Builder builder;
    private Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Helper.isDownloadInActive() && !MainActivity.isActive) {
            stopForeground(true);
            stopSelf();
            NotificationManagerCompat.from(getApplicationContext()).cancelAll();
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, DownloadsActivity.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this.getBaseContext(), channel(this).getId())
                    .setContentTitle("AutoStopping Y3 Service Running")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);
            notification = builder.build();
            startForeground(12, notification);
        } else {
            builder = new Notification.Builder(this)
                    .setContentTitle("AutoStopping Y3 Service Running")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            notification = builder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            startForeground(12, notification);
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
                    Downloader.download(DownloadExecutorService.this, object);
                }
                if (Helper.isDownloadInActive() && !MainActivity.isActive) {
                    stopForeground(true);
                    stopSelf();
                    NotificationManagerCompat.from(getApplicationContext()).cancel(12);
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static NotificationChannel channel(Context ctx) {
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "download_check_channel_id";
        CharSequence channelName = "Download Check Notification";
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100});
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notificationChannel;
    }
}
