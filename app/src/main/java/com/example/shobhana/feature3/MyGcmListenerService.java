package com.example.shobhana.feature3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import com.example.shobhana.feature3.StudentMapsActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String PREFS_NAME = "LoginPrefs";
    //private GoogleMap mMap;

    public MyGcmListenerService() {

            Log.i(TAG,"GCM listener");

    }


    @Override
    public void onMessageReceived(String from, Bundle data) {


        super.onMessageReceived(from, data);
        String message ;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if((message=data.getString("message"))!=null){

            Log.i(TAG ,from);
            Log.i(TAG, message);
            sendNotification(message);
        }

        else {
            if ((message = data.getString("location")) != null) {

                Log.i(TAG, from);
                System.out.println("IN GCM LOCATION");
                Log.i(TAG, message);
                sendNotification(message);
                String[] c = message.split(",");
                Double lat=Double.parseDouble(c[0]);
                Double lng=Double.parseDouble(c[1]);
                LatLng loc=new LatLng(lat,lng);
                System.out.println("locatin in gcm listener: " + loc);

                String CacheDir = Environment.getExternalStorageDirectory()+"/Location_coordinates.txt";
                File f=new File(CacheDir);
                try {
                    Writer fw=new FileWriter(f,true);
                    fw.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MapsMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
    }


}
