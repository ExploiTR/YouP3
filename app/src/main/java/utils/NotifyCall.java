package utils;

/*
 * Created by exploitr on 23-09-2017.
 * */


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import app.exploitr.nsg.youp3.R;

public class NotifyCall {

    @SuppressLint("StaticFieldLeak") //TODO Fix
    private static NotifyCall instance;
    private static int id;
    private NotificationManagerCompat mNotifyManager;
    private NotificationCompat.Builder build;
    private Notification buildO;
    private NotificationManager manager;
    private boolean isO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    private NotificationChannel notificationChannel;
    private Context mContext;

    private NotifyCall(Context context, String title, String contentText, int idNew) {
        mContext = context;
        mNotifyManager = NotificationManagerCompat.from(context);
        build = new NotificationCompat.Builder(context, String.valueOf(idNew));
        build.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_convert);
        id = idNew;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "conversion_channel";
            CharSequence channelName = "Mp3 Conversion Check Notification";
            notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static NotifyCall instance(Context context, String title, String contentText, int idNew) {
        if (instance != null && idNew == id) {
            return instance;
        } else {
            instance = new NotifyCall(context, title, contentText, idNew);
            return instance;
        }
    }

    public static NotifyCall getInstance() {
        return instance;
    }

    public void callWithWithProgress(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildO = new Notification.Builder(mContext, notificationChannel.getId())
                    .setContentTitle("Conversion Running")
                    .setSmallIcon(R.drawable.ic_convert)
                    .setOngoing(true)
                    .setProgress(100, progress, false)
                    .build();
            manager.notify(id, buildO);
        } else {
            build.setOngoing(true);
            build.setProgress(100, progress, false);
            Notification notification = build.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotifyManager.notify(id, notification);
        }
    }

    public void cancelById(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = buildO;
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotifyManager.notify(id, buildO);
            manager.cancel(id);
        } else {
            Notification notification = build.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotifyManager.notify(id, build.build());
            mNotifyManager.cancel(id);
        }
    }


}
