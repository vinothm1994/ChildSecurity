package com.example.vinoth.childsecurity.ulit;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class LocationServiceGps extends IntentService {
    public static final String BROADCAST_ACTION = "vinoth";
    public static final String PHONE_NO = "PHONE_NO";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    int counter = 0;
    private int i=0;
    private double Latitude=0;
    private double Longitude=0;
    private SharedPreferences sharedpreference;
    private String mPhoneNo = null;


    public LocationServiceGps() {
        super("location _ser");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "location Started", Toast.LENGTH_LONG).show();
        sharedpreference= getSharedPreferences("mydata",Context.MODE_PRIVATE);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          getlocation();
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service onStartCommand Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    public void getlocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            mPhoneNo = intent.getStringExtra(PHONE_NO);
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);

        Log.v("STOP_SERVICE", "DONE");
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
        locationManager.removeUpdates(listener);
        super.onDestroy();
    }

    public class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(final Location loc)
        {
            Log.i("vinoth", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                Latitude = loc.getLatitude();
                Longitude= loc.getLongitude();
                boolean c = isInsideBox(new LatLng(loc.getLatitude(), loc.getLongitude()));
                String data=null;
                if ((Latitude!=0)&&(Longitude!=0)){
                    if (TextUtils.isEmpty(mPhoneNo))
                        mPhoneNo = sharedpreference.getString("PHONENO", "");
                    data = c ? "Inside Box" : "OutSideBox";
                    String url = "http://maps.google.com/maps?q=" + Latitude + "," + Longitude;
                    data += "\n\n" + url;
                Intent i=new Intent(getBaseContext(),SmsSenderService.class);
                 i.putExtra("LOCATION_DATA",data);
                    i.putExtra("PHONENO", mPhoneNo);
                 getBaseContext().startService(i);
                }
                Log.i("vinoth",data);

            }
        }


        private boolean isInsideBox(LatLng latLng) {
            Gson gson1 = new Gson();
            String locationPoints = sharedpreference.getString("MyObject", "");
            if (TextUtils.isEmpty(locationPoints))
                return false;
            List<LatLng> listlatlag = gson1.fromJson(locationPoints, new TypeToken<List<LatLng>>() {
            }.getType());
            boolean c=pointInPolygon(latLng,listlatlag);
            Log.i("vinothh", String.valueOf(c));
            return c;
        }



        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }
        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
        public boolean pointInPolygon(LatLng point, List<LatLng> path) {
            // ray casting alogrithm http://rosettacode.org/wiki/Ray-casting_algorithm
            Log.i("viot  sizeh", String.valueOf(path.size()));

            int crossings = 0;
            try {
                //List<LatLng> path = polygon1.getPoints();
                //path.remove(path.size()-1); //remove the last point that is added automatically by getPoints()



                // for each edge
                for (int i=0; i < path.size(); i++) {
                    LatLng a = path.get(i);

                    Log.i("vioth", String.valueOf(a.latitude));
                    int j = i + 1;
                    //to close the last edge, you have to take the first point of your polygon
                    if (j >= path.size()) {
                        j = 0;
                    }
                    LatLng b = path.get(j);
                    if (rayCrossesSegment(point, a, b)) {
                        crossings++;
                    }
                }

                // odd number of crossings?

            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            return (crossings % 2 == 1);
        }

        public boolean rayCrossesSegment(LatLng point, LatLng a,LatLng b) {
            // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
            double px = point.longitude,
                    py = point.latitude,
                    ax = a.longitude,
                    ay = a.latitude,
                    bx = b.longitude,
                    by = b.latitude;
            if (ay > by) {
                ax = b.longitude;
                ay = b.latitude;
                bx = a.longitude;
                by = a.latitude;
            }
            // alter longitude to cater for 180 degree crossings
            if (px < 0 || ax <0 || bx <0) { px += 360; ax+=360; bx+=360; }
            // if the point has the same latitude as a or b, increase slightly py
            if (py == ay || py == by) py += 0.00000001;


            // if the point is above, below or to the right of the segment, it returns false
            if ((py > by || py < ay) || (px > Math.max(ax, bx))){
                return false;
            }
            // if the point is not above, below or to the right and is to the left, return true
            else if (px < Math.min(ax, bx)){
                return true;
            }
            // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
            else {
                double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
                double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
                return (blue >= red);
            }

        }

    }
}