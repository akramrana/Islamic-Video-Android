package com.akramhossain.islamicvideo.Notification;

/**
 * Created by Lenovo on 9/5/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.akramhossain.islamicvideo.BrowseActivity;
import com.akramhossain.islamicvideo.MainActivity;
import com.akramhossain.islamicvideo.R;
import com.akramhossain.islamicvideo.VideoPlayActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 200;
    private static final String PUSH_NOTIFICATION = "pushNotification";
    private static final String CHANNEL_ID = "my_channel_01";
    private static final String URL = "url";
    private static final String ACTIVITY = "activity";
    Map<String, Class> activityMap = new HashMap<>();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        //Populate activity map
        activityMap.put("MainActivity", MainActivity.class);
        activityMap.put("VideoPlayActivity", VideoPlayActivity.class);
        activityMap.put("BrowseActivity", BrowseActivity.class);
    }

    /**
     * Displays notification based on parameters
     *
     * @param notificationVO
     * @param resultIntent
     */
    public void displayNotification(NotificationVO notificationVO, Intent resultIntent) {

        String message = notificationVO.getMessage();
        String title = notificationVO.getTitle();
        String iconUrl = notificationVO.getIconUrl();
        String action = notificationVO.getAction();
        String destination = notificationVO.getActionDestination();
        Bitmap iconBitMap = null;
        if (iconUrl != null) {
            iconBitMap = getBitmapFromURL(iconUrl);
        }
        final int icon = R.mipmap.ic_launcher;

        PendingIntent resultPendingIntent;

        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }

        if (URL.equals(action)) {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));

            resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, pendingIntentFlags);

        } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
            resultIntent = new Intent(mContext, activityMap.get(destination));

            resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, pendingIntentFlags);
        } else {
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, pendingIntentFlags);
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "my_channel";
            String Description = "Islamic Video";

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(mChannel);
        }


        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        // Asynchronously fetch the icon bitmap
        fetchBitmapAsync(iconUrl, bitmap -> {
            Notification notification;
            if (bitmap == null) {
                // Default notification style
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.addLine(message);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent).setStyle(inboxStyle).setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon)).setContentText(message).build();
            } else {
                // Big picture style for notifications with an image
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(message);
                bigPictureStyle.bigPicture(bitmap);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentIntent(resultPendingIntent).setStyle(bigPictureStyle).setLargeIcon(bitmap).build();
            }
            // Display the notification
            notificationManager.notify(NOTIFICATION_ID, notification);
        });
    }


    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchBitmapAsync(String strURL, OnBitmapLoadedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                listener.onBitmapLoaded(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onBitmapLoaded(null);
            }
        });
    }

    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            //Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + mContext.getPackageName() + "/raw/notification.mp3");
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface OnBitmapLoadedListener {
        void onBitmapLoaded(Bitmap bitmap);
    }
}
