package com.example.vinoth.childsecurity.ulit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private static String senderNum=null;
    private  static String message=null;
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                assert pdusObj != null;
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    senderNum = currentMessage.getDisplayOriginatingAddress();
                     message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,"senderNum: " + senderNum + ", message: " + message, duration);
                    toast.show();
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
        if (!TextUtils.isEmpty(senderNum) && !TextUtils.isEmpty(message)) {
            Log.i("vinoth","sms recevred");
            if (message.contains("Find")) {
                Log.i("vinoth","Starting  Location Stated ");
                Intent serviceIntent = new Intent(context, LocationServiceGps.class);
                serviceIntent.putExtra(LocationServiceGps.PHONE_NO, senderNum);
                context.startService(serviceIntent);

            }else {
                Log.i("vinoth"," not march smss");
            }
        }
        else {
            Log.i("vinoth","checking the marcdhh");
        }

    }

}
