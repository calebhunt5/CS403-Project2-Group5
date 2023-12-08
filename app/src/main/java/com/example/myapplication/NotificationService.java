package com.example.myapplication;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class NotificationService extends Service {
    public String user_id;

    private static final String CHANNEL_ID = "Foreground Location Channel";

    public static final int NOTIFICATION_ID = 222;


    public static final String TAG = "NotifServiceTag";

    DismissReceiver receiver;

    SharedPreferences userSharedPref;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://pandaexpress-rating-backend-group5.onrender.com");
        } catch (URISyntaxException e) {}
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification for running in the foreground
        Notification foregroundNotification = createForegroundNotification();

        Log.d("SharedPrefTest", "here?");
        // Start the service in the foreground and show the notification
        startForeground(NOTIFICATION_ID, foregroundNotification);

        userSharedPref = getSharedPreferences("email", MODE_PRIVATE);
        Log.d("SharedPrefTest", userSharedPref.getString("email", "NOOOOO"));

        if (userSharedPref.contains("email")) {
            String s = userSharedPref.getString("email", "DEFAULT");

            if (s.equals("DEFAULT") == false) {
                user_id = s;
            }
        }

        mSocket.on("likedRating" + user_id, fn ->{
            Log.d(TAG, "New Garage Sale from server");
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("New garage sale near you!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnTapAction())
                    .setDeleteIntent(setOnDismissAction())
                    .build();
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(NOTIFICATION_ID,notification);
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("newGarageSale");
    }

    public PendingIntent setOnTapAction(){
        Intent i = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );
        return pendingIntent;
    }

    public PendingIntent setOnDismissAction(){
        Intent i = new Intent("dismiss_broadcast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );
        return pendingIntent;
    }


    public NotificationService() {
        Log.d(TAG, "HELP");


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Hello?");
        createNotificationChannel();
        Log.d(TAG, "1");
        DismissReceiver receiver = new DismissReceiver();
        Log.d(TAG, "2");
        IntentFilter filter = new IntentFilter("dismiss_broadcast");
        Log.d(TAG, "3");
        registerReceiver(receiver,filter);
        Log.d(TAG, "4");
        mSocket.connect();
    }

    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notify App Main Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        Log.d(TAG, "Main Channel created");
    }

    class DismissReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case "dismiss_broadcast":
                    Toast.makeText(context,"Notification was dismissed",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private Notification createForegroundNotification() {
        // Create a notification channel if necessary
        createNotificationChannel();

        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Listening for garage sale updates...")
                .setSmallIcon(android.R.drawable.star_big_on)
                .build();

        return notification;
    }
}
