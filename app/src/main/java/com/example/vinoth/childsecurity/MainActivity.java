package com.example.vinoth.childsecurity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vinoth.childsecurity.ulit.LocationServiceGps;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST = 10001;
    private EditText etPassword;
    private EditText etUsername;

    public void check(View view) {

        String pass = etPassword.getText().toString();
        String user = etUsername.getText().toString();

        if ((pass.equals("admin")) && (user.equals("admin"))) {
            Toast.makeText(this, "password correct", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "password wrong", Toast.LENGTH_SHORT).show();
        }
        setTitle("HOME");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        etPassword = (EditText) findViewById(R.id.etPasswort);
        etUsername = (EditText) findViewById(R.id.etUsername);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    },
                    MY_PERMISSIONS_REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }

        }
    }


    public void sentMyLocation(View view) {
        Intent serviceIntent = new Intent(this, LocationServiceGps.class);
        startService(serviceIntent);
    }
}


