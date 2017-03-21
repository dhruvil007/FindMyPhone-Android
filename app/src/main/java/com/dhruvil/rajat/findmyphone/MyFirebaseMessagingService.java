package com.dhruvil.rajat.findmyphone;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Message from: " + remoteMessage.getFrom());

        String text = "";
        boolean ring = false;
        boolean location = false;
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message: " + remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getData() != null) {
            Log.e(TAG, "Message: " + remoteMessage.getData().get("message"));
            text = remoteMessage.getData().get("message");
            ring = Boolean.parseBoolean(remoteMessage.getData().get("ring"));
            Log.e(TAG, "" + ring);
            location = Boolean.parseBoolean(remoteMessage.getData().get("location"));
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fromFCMService", true);
        intent.putExtra("fromFCMServiceText", text);
        intent.putExtra("fromFCMServiceRing", ring);
        intent.putExtra("fromFCMServiceLocation", location);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
