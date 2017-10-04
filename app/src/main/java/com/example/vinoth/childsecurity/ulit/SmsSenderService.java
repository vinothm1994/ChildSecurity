package com.example.vinoth.childsecurity.ulit;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsSenderService extends Service {
    private String PHONE_NO="121";
    private String LOCATION_DATA=null;

    public SmsSenderService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("vinoth","LOCATION_DATA"+LOCATION_DATA+PHONE_NO);
        Toast.makeText(getApplicationContext(), "location Started", Toast.LENGTH_LONG).show();
        LOCATION_DATA = intent.getStringExtra("LOCATION_DATA");
        PHONE_NO=intent.getStringExtra("PHONENO");

        Log.i("vinoth",PHONE_NO+""+LOCATION_DATA);
        //smsManager.sendTextMessage(PHONE_NO,null,LOCATION_DATA, null, null);
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        Toast.makeText(this,LOCATION_DATA,Toast.LENGTH_LONG).show();

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS

        smsManager.sendTextMessage(PHONE_NO, null, LOCATION_DATA, sentPendingIntent, deliveredPendingIntent);

        Log.i("vinoth",LOCATION_DATA+PHONE_NO);
        stopService(new Intent(getBaseContext(), LocationServiceGps.class));
        return START_STICKY;
    }
}
