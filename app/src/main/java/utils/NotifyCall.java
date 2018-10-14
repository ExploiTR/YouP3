package utils;

/*
 * Created by exploitr on 23-09-2017.
 * @unused


public class NotifyCall {

    private static NotifyCall instance;
    private static int id;
    private NotificationManagerCompat mNotifyManager;
    private NotificationCompat.Builder build;

    private NotifyCall(Context context, String title, String contentText, int idNew) {
        mNotifyManager = NotificationManagerCompat.from(context);
        build = new NotificationCompat.Builder(context, String.valueOf(idNew));
        build.setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_download_on);
        id = idNew;
    }

    public static NotifyCall instance(Context context, String title, String contentText, int idNew) {
        if (instance != null && idNew == id) {
            return instance;
        } else {
            instance = new NotifyCall(context, title, contentText, idNew);
            return instance;
        }
    }

    public void callWithIntent(PendingIntent intent) {
        build.setOngoing(true);
        build.setContentIntent(intent);
        Notification notification = build.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(id, notification);
    }

    public void callWithWithProgress(long full, long now) {
        build.setOngoing(true);
        build.setProgress(
                Integer.parseInt(String.valueOf(full).substring(0, 3)),
                Integer.parseInt(String.valueOf(now).substring(0, 3)), false);
        Notification notification = build.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(id, notification);
    }


    public void cancelById(int id) {
        Notification notification = build.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(id, build.build());
        mNotifyManager.cancel(id);
    }

    public void cancelAll() {
        Notification notification = build.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(id, build.build());
        // IDK why `cancelAll()` not working when FLAG_NO_CLEAR is set.
        mNotifyManager.cancelAll();
    }

}
*/