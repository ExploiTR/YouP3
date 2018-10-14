package downloader.utils;

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
import android.support.annotation.Nullable;

import app.exploitr.nsg.youp3.DownloadsActivity;
import app.exploitr.nsg.youp3.R;

public class DownloadExecutorService extends Service {

    Handler handler;
    PendingIntent pendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pendingIntent = PendingIntent.getActivity(this,1,new Intent(DownloadExecutorService.this, DownloadsActivity.class),0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "download_check_channel_id";
            CharSequence channelName = "Download Check Notification";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            Notification notification = new Notification.Builder(this.getBaseContext(), notificationChannel.getId())
                    .setContentTitle("Downloads Running")
                    .setContentText("Background Downloads Are Active!")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setProgress(0, 100, false)
                    .build();
            startForeground(12, notification);
        } else {
            Notification notification = new Notification.Builder(this).setContentTitle("Downloads Running")
                    .setContentText("Background Downloads Are Active!")
                    .setSmallIcon(R.drawable.ic_download_on)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setProgress(0, 100, false).build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
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
                        BasicDownloader.getInstance(DownloadExecutorService.this)
                                .setQueueObject(object).start();
                    } else {
                        AdvancedDownloader.getInstance(DownloadExecutorService.this)
                                .setQueueObject(object).start();
                    }
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
}
