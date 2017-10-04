package com.example.vinoth.childsecurity.ulit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootComplete extends BroadcastReceiver {
    public BootComplete() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AutoStartUp.class);

            context.startService(serviceIntent);

            Intent smsinten = new Intent(context, SmsReceiver.class);
            context.startService(smsinten);

            Log.i("vinoth","app started at boot ");

        }

    }

}
