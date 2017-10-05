package utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import app.exploitr.nsg.youp3.R;

/*
 * Created by exploitr on 23-09-2017.
 */

public class NotifyCall {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    private int id = (int) (Math.random() + 1);

    public NotifyCall(Context context, String title, String contentText) {
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        build = new NotificationCompat.Builder(context);
        build.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_download_on);
    }

    public void callProgress(int max, int progress) {
        build.setProgress(max, progress, false);
        Notification notification = build.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        mNotifyManager.notify(id, notification);
    }


    public void removeProgressAndSetIntent(PendingIntent contentIntent) {
        build.setProgress(0, 0, false);
        build.setContentIntent(contentIntent);
        build.setAutoCancel(true);
        mNotifyManager.notify(id, build.build());
    }

    public void cancelAll() {
        Notification notification = build.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(id, build.build());
        // IDK why `cancelAll()` not working when FLAG_NO_CLEAR is set.
        mNotifyManager.cancelAll();
    }
}
