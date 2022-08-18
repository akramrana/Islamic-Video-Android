package com.akramhossain.islamicvideo.Service;

import android.content.Intent;
import android.util.Log;

import com.akramhossain.islamicvideo.Notification.NotificationUtils;
import com.akramhossain.islamicvideo.Notification.NotificationVO;
import com.akramhossain.islamicvideo.VideoPlayActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService  {
    private static final String TAG = "MyFirebaseMsgingService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";
    private static final String TARGET_ID = "target_id";

    @Override
    public void handleIntent(Intent intent) {
        Log.e(TAG, "handleIntent");
        try
        {
            if (intent.getExtras() != null)
            {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MyFirebaseMessagingService");
                for (String key : intent.getExtras().keySet())
                {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }
                onMessageReceived(builder.build());
            }
            else
            {
                super.handleIntent(intent);
            }
        }
        catch (Exception e)
        {
            super.handleIntent(intent);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        /*if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();

            Log.d(TAG, "data payload: " + data.toString());
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String imageUrl = remoteMessage.getData().get("image");
            handleNotification(remoteMessage.getNotification(), imageUrl);
        }*/

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String imageUrl = remoteMessage.getData().get("image");
            String targetId = remoteMessage.getData().get("target_id");
            handleNotification(remoteMessage.getNotification(), imageUrl, targetId);
        }

    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification, String iconUrl, String targetId) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();

        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setTargetId(targetId);

        //Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        Intent resultIntent = new Intent(getApplicationContext(), VideoPlayActivity.class);
        resultIntent.putExtra("video_id", targetId);
        resultIntent.putExtra("video_name", title);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        String targetId = data.get(TARGET_ID);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);
        notificationVO.setTargetId(targetId);

        Intent resultIntent = new Intent(getApplicationContext(), VideoPlayActivity.class);
        resultIntent.putExtra("video_id", targetId);
        resultIntent.putExtra("video_name", title);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();

    }
}
