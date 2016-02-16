package drive.solution.com.drivecoach.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import drive.solution.com.drivecoach.utils.Utils;

/**
 * Created by M1032185 on 11/19/2015.
 */
public class LocationTracker extends Service {

    public static final String LOCATION_UPDATE = "com.driver.solution.location_update";
    public static final String TAG_LOCATION_LAT = "TAG_LOCATION_LAT";
    public static final String TAG_LOCATION_LNG = "TAG_LOCATION_LNG";

    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_TIMESTAMP = "timestamp";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_REG_NUM = "regno";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 500;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000; // 1 minute
    Context mContext;
    LocationManager locationManager;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    boolean isGpsEnabled = false;
    boolean isNetworkEnabled = false;

    GPSLocationListener locationListener;

    public LocationTracker() {
        super();
    }

    private ArrayList<LatLng> routePoints;


    public LocationTracker(Context context) {
        getLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        routePoints = new ArrayList<LatLng>();
        getLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationListener != null && locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    public void getLocation() {

        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                return;
            } else {

                if (isGpsEnabled) {
                    locationListener = new GPSLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isNetworkEnabled) {
                    locationListener = new GPSLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

//            Toast.makeText(getBaseContext(), location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_LONG).show();

//            sendDataToServer(location);

            sendLocationUpdates(location);
        }

        private void sendLocationUpdates(Location location) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            routePoints.add(latLng);

            Intent intent = new Intent(LocationTracker.LOCATION_UPDATE);
            intent.putParcelableArrayListExtra("points", routePoints);
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            intent.putExtra("altitude", location.getAltitude());

            sendBroadcast(intent);
        }

        private void sendDataToServer(Location location) {

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(TAG_LATITUDE, location.getLatitude());
                jsonObj.put(TAG_LONGITUDE, location.getLongitude());
                jsonObj.put(TAG_TIMESTAMP, Utils.getTimeStamp());
                jsonObj.put(TAG_IMEI, Utils.getIMEINumber(mContext));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
