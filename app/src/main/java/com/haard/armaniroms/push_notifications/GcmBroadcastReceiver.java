package com.haard.armaniroms.push_notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.haard.armaniroms.MainActivity;
import com.haard.armaniroms.R;
import com.haard.armaniroms.history_database.DatabaseHelper;

/**
 * Created by haxorware on 30/5/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    static final String TAG = "GCMDemo";
    public static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;


    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        String gcmmsg="";

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;


        try {
            DatabaseHelper db = new DatabaseHelper(context);


            String messageType = gcm.getMessageType(intent);
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + intent.getExtras().toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + intent.getExtras().toString());
            } else {
                gcmmsg = intent.getStringExtra("content");
                db.insertMsg(gcmmsg);
                sendNotification(gcmmsg);
            }
            setResultCode(Activity.RESULT_OK);
        }
        catch(Exception x){
            Log.w("DB", "Error");}

    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {
        String title=msg.substring(0, msg.indexOf(';'));
        String link=msg.substring(msg.indexOf(';')+1,msg.length());
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(!link.startsWith("http://") && !link.startsWith("https://"))
            link="http://"+link;
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);




        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_normal_light)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(link))
                        .setContentText(link);
        mBuilder.setDefaults(defaults);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        NOTIFICATION_ID++;
    }
}
