package com.example.vinoth.childsecurity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {


    private static final int PICK_CONTACT = 100;
    private GoogleMap mMap;

    private int i=1;
    private PolygonOptions polygonOptions;
    private Polygon polygon;
    private int j=0;
    private SharedPreferences sharedpreference;
    private ArrayList<LatLng> latLngs;
    private EditText etPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         sharedpreference=getSharedPreferences("mydata", Context.MODE_PRIVATE);
         latLngs =new ArrayList<LatLng>();

         polygonOptions =new PolygonOptions();
        etPhoneNumber=(EditText)findViewById(R.id.etPhoneNumber);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        googleMap.isMyLocationEnabled();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        try {
            Gson gson = new Gson();
            String json = sharedpreference.getString("MyObject", "");
            Log.i("vinoth",json);
            ArrayList<LatLng> locationdata1 = gson.fromJson(json,new TypeToken<List<LatLng>>(){}.getType() );
            polygon=mMap.addPolygon(new PolygonOptions().addAll(locationdata1).strokeColor(Color.RED).fillColor(0x4000A300).strokeWidth(1));

        }
        catch (Exception e){
            e.printStackTrace();
        }
            }

    @Override
    public void onMapLongClick(LatLng point) {

        Toast.makeText(this, point.latitude+" "+point.longitude, Toast.LENGTH_SHORT).show();
        mMap.addMarker(new MarkerOptions().position(point).title("marker place"+i++));
        latLngs.add(point);
    }



    public void save (View view){
        SharedPreferences.Editor prefsEditor = sharedpreference.edit();
        Gson gson = new Gson();
        String json = gson.toJson(latLngs);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
        String phone=etPhoneNumber.getText().toString();
        prefsEditor.putString("PHONENO", phone);
        prefsEditor.commit();


    }
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
       polygon=mMap.addPolygon(polygonOptions.addAll(latLngs).strokeColor(Color.RED).fillColor(0x4000A300).strokeWidth(1));

        return true;

    }
    public void readcontact(View view){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/people"));
            startActivityForResult(intent, PICK_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    startManagingCursor(c);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
                        String number = c.getString(c.getColumnIndexOrThrow(Contacts.People.NUMBER));
                        Toast.makeText(this,  name + " has number " + number, Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }

    }

}
