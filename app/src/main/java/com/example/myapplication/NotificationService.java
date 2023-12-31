package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class NotificationService extends Service {

    //String used to hold the channel ID for the notifications
    private static final String CHANNEL_ID = "Foreground Location Channel";

    //Integer for the notification ID
    public static final int NOTIFICATION_ID = 1;


    //Debug tag
    public static final String TAG = "NotifServiceTag";

    //Shared preference editor
    SharedPreferences userSharedPref;

    //clients id
    String user_id;

    //Socket used for communicating with the server
    private Socket mSocket;
    {
        try {
            //Creates an IO socket using server route
            mSocket = IO.socket("https://pandaexpress-rating-backend-group5.onrender.com");
        } catch (URISyntaxException e) {}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        userSharedPref = getSharedPreferences("saved_session", MODE_PRIVATE);
        Log.d("SharedPrefTest", userSharedPref.getString("user_id", "NOOOOO"));

        if (userSharedPref.contains("user_id")) {
            String s = userSharedPref.getString("user_id", "DEFAULT");

            if (s.equals("DEFAULT") == false) {
                user_id = s;
            }
        }




        //Creates a "Listener" for the socket
        //When the client socket receives message from server it will run function fn

        mSocket.on("likedRating" + user_id , fn ->{

            //Debug
            Log.d(TAG, "New Rating from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("Someone liked your rating!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnTapAction())
                    .setDeleteIntent(setOnDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(NOTIFICATION_ID,notification);
        });




        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        //When onDestroy is triggered, shut down the socket and turn off the listener
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("likedRating" + user_id);
    }

    public PendingIntent setOnTapAction(){

        //Create intent for the login page (HomeActivity)
        Intent i = new Intent(this, HomeActivity.class);

        //Create pending intent for notification to use
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnDismissAction(){

        //Create intent with action to delete pending intent
        Intent i = new Intent("dismiss_broadcast");

        //Create pending intent for notification to use
        //(This pending intent just stops pending intents)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }




    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Calls method to create a notification channel
        createNotificationChannel();

        //Attempts to connect client socket to server socket
        mSocket.connect();
    }

    public void createNotificationChannel() {

        //Creates a new notification channel called channel to send notification through
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notify App Main Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        //Creates a new instance of notificationManager
        NotificationManager manager = getSystemService(NotificationManager.class);

        //Create notificationChannel using channel
        manager.createNotificationChannel(channel);

        //Debug
        Log.d(TAG, "Main Channel created");
    }


    private Notification createForegroundNotification() {
        // Create a notification channel if necessary
        createNotificationChannel();

        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Listening for updates...")
                .setSmallIcon(android.R.drawable.star_big_on)
                .build();

        //Return the notification
        return notification;
    }
}
