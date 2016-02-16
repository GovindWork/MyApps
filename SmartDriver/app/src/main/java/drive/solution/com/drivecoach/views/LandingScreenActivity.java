package drive.solution.com.drivecoach.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.R;
import drive.solution.com.drivecoach.services.LocationTracker;
import drive.solution.com.drivecoach.transactions.ApiService;
import drive.solution.com.drivecoach.utils.Utils;

public class LandingScreenActivity extends AppCompatActivity implements LocationListener {

    private static final int INIT_TIMER = 60000;

    public static final String FRAGMENT_GOOGLE_MAP_TAG = "FRAGMENT_GOOGLE_MAP_TAG";

    private static final String WEB_CLIENT_ID = "1012324710349-d2f7cpu7pq5dieh1v2ecth4ceskb22nd.apps.googleusercontent.com";

    boolean locked = true;
    SharedPreferences prefs;
    // [START auth_variable_references]
    private static final int REQUEST_OAUTH = 1;
    private OnDataPointListener heartRateListener = null;// heart rate listener

    private OnDataPointListener mListenerLocation = null;
    private static final int RC_SIGN_IN = 0;
    private MenuItem menus;
    private static final int RC_USER_LOGIN = 1;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private TextToSpeech textToSpeech;
    private TextView textUserName;
    private TextView textemailId;
    private LinearLayout layoutSingIn;
    private TextView textNotify;
    private LinearLayout layoutSignOut;

    private LinearLayout layoutHealthStatus;

    private LinearLayout layoutBtnJourney;

    private Button btnSignIn;
    private Handler mHandler;
    private Button btnSignOut;

    private Button btnStartJourney;
    private Button btnEndJourney;
    private Button btnStopJourney;

    private ImageView imageHeartBeat;

    private TextView textWelcomeMsg;

    private EditText editTextDOB;

    private TextView textHeartRate;
    private TextView currentTime;

    private String userId;

    public static final String TAG = "DC1";

    private static final LatLng SYDNEY = new LatLng(-33.88, 151.21);

    private ArrayList<LatLng> mMarkerPoints;
    private MarkerOptions markerOptions;
    private final String LINE_SEPARATOR = "\n";
    private GoogleMap.OnMapClickListener onMapClickListener;
    private GoogleMap.OnMarkerClickListener onMarkerClickListener;
    private MapViewerListener mListener;

    private boolean authInProgress = false;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    // Separate object to handle the logic for Server Auth Code exchange, which is optional
    //  private ServerAuthHandler mServerAuthHandler;

    private GoogleApiClient mClient = null;

    LocationRequest mLocationRequest;
    Location mCurrentLocation;


    FitInsertSessionsTimerTask fitTimerTask;
    Timer fitInsertTimer;

    Long sessionStartTime;
    Long sessionLastTime;

    Session session = null;

    private JSONArray jsonArray;

    ProgressDialog dialog;

    private MenuItem menuUpload;

    public static final String SESSION_NAME = "JourneySession2";
    public static final String SESSION_IDENTIFIER = "JourneySesId2";
    public static final String SESSION_DESCRIPTION = "JourneySesDesc";
    public static String HEART_RATE = "0.0";
    public static float ALTITUDE = 50;
    public static String UserName;
    public static String emailid;

    DataSource heartRateDs, locationDs, locationDs2;
    DataSet dataSet = null;
    DataSet dataSetLocation = null;
    DataSet dataSetLoc = null;

    private ArrayList<LatLng> routePoints;

    boolean isMapReady;

    private int MINUTES = 2;
    private int MILI_SECONDS = 60000;
    private int UPDATE_INTERVAL = MINUTES * MILI_SECONDS;

    private Polyline line;

    double latitude;
    double longitude;
    double prevLatitude;
    double prevLongitude;
    double altitude;
    LatLng latLng;

    private EditText editUserHeightInchs;
    private EditText editUserHeightft;
    private EditText editUserWeight;


    Session inSertFitSession;

    private Context mContext;

    Timer updateLocationTimer;

    Timer heartRateUpdateTimer;

    Timer heartRateCheckTimer;

    FitInsertSessionsTimerTask task;

    LocationTimerTask locationTimerTask;

    HeartRateTimerTask heartRateTimerTask;

    HeartRateCheckTimerTask heartRateCheckTimerTask;

    SessionListener sessionListener;

    Timer sessionTimer;

    BroadcastReceiver locationReciever = null;

    BroadcastReceiver authorizeReceiver = null;

    private Location location;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
        if (mMap != null) {
            mMap.clear();

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        Utils.showToast(getApplicationContext(), "" + location.getLatitude() + "" + location.getLongitude());

        /*if (routePoints.size() > 0) {
            LatLng tempLatLang = routePoints.get(routePoints.size() - 1);
            double distance = Utils.directDistance(tempLatLang.latitude, tempLatLang.longitude, latLng.latitude, latLng.longitude);
            Utils.showToast(getApplicationContext(), "Distance : " + distance);
            if (distance > 20.0) {
                routePoints.add(latLng);
            }
        } else {
            routePoints.add(latLng);
        }*/

            routePoints.add(latLng);

            PolylineOptions pOptions = new PolylineOptions()
                    .width(10)
                    .color(Color.BLUE)
                    .geodesic(false);
            for (int z = 0; z < routePoints.size(); z++) {
                LatLng point = routePoints.get(z);
                pOptions.add(point);
            }
            line = mMap.addPolyline(pOptions);

            MarkerOptions mp = new MarkerOptions();

            mp.position(routePoints.get(routePoints.size() - 1));
//                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck));

            mp.title("my position");

            mMap.addMarker(mp);

            LatLng latLng1 = routePoints.get(routePoints.size() - 1);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng1, 17));
        }
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
        Log.d(TAG, "has accuracy" + location.hasAccuracy());
        Log.d(TAG, "Accuracy" + location.getAccuracy());

        JSONObject jsonObject = new JSONObject();
        try {

            if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("altitude", location.getAltitude());
                jsonObject.put("timestamp", Utils.getTimeStamp());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private float getDistance(double lat1, double lang1, double lat2, double lang2) {

//        lat1 = 12.9185718;
//        lang1 = 77.5029701;
//
//        lat2 = 12.9185868;
//        lang2 = 77.5029981;

        Location startLocation = new Location("Start");
        startLocation.setLatitude(lat1);
        startLocation.setLongitude(lang1);

        Location endLocation = new Location("End");
        endLocation.setLatitude(lat2);
        endLocation.setLongitude(lang2);

        float distance = startLocation.distanceTo(endLocation);

        Log.d(TAG, "DISTANCE" + distance);

        return distance;
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client.disconnect();
    }


    public interface MapViewerListener {
        public void onMapReady();
    }

    SQLiteDatabase db;

    HashMap<String, String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        //application context.
        mContext = this;

        //Creates dummy user info.
        createUsers();

        // gets vehicle registration number.
        String regNumber = Utils.getFromPrefrences(getApplicationContext(), "regNo");
        if (regNumber.equalsIgnoreCase("null")) {
            getSupportActionBar().setTitle("Reg No. ");
        } else {
            getSupportActionBar().setTitle("Reg No. " + regNumber);
        }

        //Setting up location timer.
//        locationTimerTask = new LocationTimerTask();
//        updateLocationTimer = new Timer();
//        updateLocationTimer.schedule(locationTimerTask, 60000, 60000);

        //Setting session end listener.
//        sessionListener = new SessionListener();
//        sessionTimer = new Timer();

        //Setting up heart rate timer.
//        heartRateTimerTask = new HeartRateTimerTask();
//        heartRateUpdateTimer = new Timer();
//        heartRateUpdateTimer.schedule(heartRateTimerTask, 60000, 60000);

        mMarkerPoints = new ArrayList<LatLng>();
        routePoints = new ArrayList<LatLng>();

        jsonArray = new JSONArray();

        layoutSingIn = (LinearLayout) findViewById(R.id.btn_layout_signin);
        layoutSignOut = (LinearLayout) findViewById(R.id.btn_layout_signout);
        layoutHealthStatus = (LinearLayout) findViewById(R.id.layout_health_status);
        textWelcomeMsg = (TextView) findViewById(R.id.label_welcome);
        layoutBtnJourney = (LinearLayout) findViewById(R.id.layout_btn_journey);
        imageHeartBeat = (ImageView) findViewById(R.id.image_heart_beat);
        textHeartRate = (TextView) findViewById(R.id.hearttext);

        btnSignIn = (Button) findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(clickListener);
        btnSignOut = (Button) findViewById(R.id.btn_signout);
        btnSignOut.setOnClickListener(clickListener);
        currentTime = (TextView) findViewById(R.id.hearttext1);
        btnStartJourney = (Button) findViewById(R.id.btn_start_journey);

        btnStartJourney.setOnClickListener(clickListener);
        btnStopJourney = (Button) findViewById(R.id.btn_stop_journey);
        btnStopJourney.setOnClickListener(clickListener);
        //Timer Initialization
        fitTimerTask = new FitInsertSessionsTimerTask();
        fitInsertTimer = new Timer();

        //start location track service.
        Intent intent = new Intent(getApplicationContext(), LocationTracker.class);
        startService(intent);

        buildFitnessClient();
        createLocationRequest();
        initMap(savedInstanceState);

        authorizeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        registerReceiver(locationReciever, new IntentFilter(LocationTracker.LOCATION_UPDATE));

        locationReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
//                    mMap.clear();
//
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    routePoints.add(latLng);
//
////                    routePoints = intent.getParcelableArrayListExtra("points");
//                    latitude = intent.getDoubleExtra("latitude", 0);
//                    longitude = intent.getDoubleExtra("longitude", 0);
//                    Log.d(TAG, "Location Service" + latitude + "," + longitude);
////                    Utils.showToast(getApplicationContext(), latitude + "," + longitude);
//
//                    altitude = intent.getDoubleExtra("altitude", 0);
//
////                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
////                    routePoints.add(latLng);
////
//                    PolylineOptions pOptions = new PolylineOptions()
//                            .width(10)
//                            .color(Color.BLUE)
//                            .geodesic(false);
//                    for (int z = 0; z < routePoints.size(); z++) {
//                        LatLng point = routePoints.get(z);
//                        pOptions.add(point);
//                    }
//                    line = mMap.addPolyline(pOptions);
//
//                    MarkerOptions mp = new MarkerOptions();
//
//                    mp.position(routePoints.get(routePoints.size() - 1));
////                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck));
//
//                    mp.title("my position");
//
//                    mMap.addMarker(mp);
//
//                    LatLng latLng1 = routePoints.get(routePoints.size() - 1);
//
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                            latLng1, 17));

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("latitude", latitude);
//                    jsonObject.put("longitude", longitude);
//                    jsonObject.put("altitude", altitude);
//                    jsonObject.put("timestamp", Utils.getTimeStamp());
//                    jsonArray.put(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        };

        registerReceiver(locationReciever, new IntentFilter(LocationTracker.LOCATION_UPDATE));

        loginToGoogle();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        checkJoyrneyStatus();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void loginToGoogle() {

        if (Utils.isNetworkConnected(getApplicationContext())) {
            mShouldResolve = true;
            dialog = new ProgressDialog(LandingScreenActivity.this);
            dialog.setMessage("Logging In....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mClient.connect();
        } else {
            Utils.showAlertDialog(LandingScreenActivity.this).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LandingScreenActivity.this);
        builder1.setMessage("Kindly make sure that your band is connected via sony app.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

   /* private void registerLocationUpdateTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("journeyid", "ds");
                    jsonObject.put("sessionid", "da");
                    jsonObject.put("locations", jsonArray);
                    ApiService.getInstance().doUpdateLocation(mContext, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "1MIn");
            }
        }, 0, UPDATE_INTERVAL);
    }*/

    private void buildFitnessClient() {
        mClient = buildGoogleApiClient();

//        mClient = ApiClient.getApiClient(LandingScreenActivity.this, this);
    }

    public void InsertSessionDataSource() {
        if (null == heartRateDs) {
            // 1. Create a data source
            heartRateDs = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_HEART_RATE_BPM)
                    .setName("HeartRateRecorder")
                    .setType(DataSource.TYPE_RAW)
                    .build();
        }


        // 2. Create a data set
        dataSet = DataSet.create(heartRateDs);


        if (null == locationDs) {
            // 1. Create a data source
            locationDs = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                    .setName("LocationHeartrateRecorder")
                    .setType(DataSource.TYPE_RAW)
                    .build();
        }

        dataSetLocation = DataSet.create(locationDs);

        if (null == locationDs2) {
            // 1. Create a data source
            locationDs2 = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                    .setName("LocationRecorder")
                    .setType(DataSource.TYPE_RAW)
                    .build();
        }

        // 2. Create a data set

        dataSetLoc = DataSet.create(locationDs2);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_signin:

//                    Log.i(TAG, "GET SLEEP CALLED :");

                    if (Utils.isNetworkConnected(getApplicationContext())) {
                        if (prefs.getBoolean("locked", locked)) {

                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_MONTH, -25);
                            cal.add(Calendar.HOUR_OF_DAY, 12);

                            long threeDaysAgo = cal.getTimeInMillis();
                            String starttime = String.valueOf(threeDaysAgo);

                            long endtime = System.currentTimeMillis();
                            String end = String.valueOf(endtime);
                            Utils.storeToPrefrences(getApplicationContext(), "starttime", starttime);
                            Utils.storeToPrefrences(getApplicationContext(), "endtime", end);

                            ApiService.getInstance().doGetSleepData(getApplicationContext(), Utils.USER_EMAIL, Utils.getFromPrefrences(getApplicationContext(), "starttime"),
                                    Utils.getFromPrefrences(getApplicationContext(), "endtime"), "BC:6E:64:FA:EF:E5");

//                            ApiService.getInstance().doGetSleepData(getApplicationContext(), Utils.USER_EMAIL, "1449988200000", "1454653800000", "BC:6E:64:FA:EF:E5");
                            prefs.edit().putBoolean("locked", false).commit();
                        } else {
                            ApiService.getInstance().doGetSleepData(getApplicationContext(), Utils.USER_EMAIL,
                                    Utils.getFromPrefrences(getApplicationContext(), "starttime"), Utils.getFromPrefrences(getApplicationContext(), "endtime"), "BC:6E:64:FA:EF:E5");
//                            ApiService.getInstance().doGetSleepData(getApplicationContext(), Utils.USER_EMAIL, "1449988200000", "1454653800000", "BC:6E:64:FA:EF:E5");
                        }
                        doAuthorizeUser();
                    } else {
                        Utils.showAlertDialog(LandingScreenActivity.this).show();
                    }

                    break;

                case R.id.btn_signout:

                    if (Utils.isNetworkConnected(getApplicationContext())) {
                        endUserSession(); //end user session

                        layoutSignOut.setVisibility(View.GONE);
                        layoutSingIn.setVisibility(View.VISIBLE);
                        layoutHealthStatus.setVisibility(View.GONE);
                        layoutBtnJourney.setVisibility(View.GONE);
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(LandingScreenActivity.this);
                        builder2.setMessage("Kindly make sure that your band is disconnected");
                        builder2.setCancelable(true);

                        builder2.setPositiveButton(
                                "Okay",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });


                        AlertDialog alert12 = builder2.create();
                        alert12.show();
                    } else {
                        Utils.showAlertDialog(getApplicationContext());
                    }
                    break;

                case R.id.btn_start_journey:
                    btnStartJourney.setVisibility(View.GONE);
                    btnStopJourney.setVisibility(View.VISIBLE);
                    InsertSessionDataSource();
                    startJouney();
                    break;

                case R.id.btn_stop_journey:
                    btnStopJourney.setVisibility(View.GONE);
                    btnStartJourney.setVisibility(View.VISIBLE);
                    stopJourney();
                    break;
            }
        }
    };

    private boolean isJourneyIdAvailable() {
        String journeyID = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        if (journeyID.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {

        try {
//            Utils.showToast(getApplicationContext(), "Location updates are started.");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mClient, mLocationRequest, (LocationListener) this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUsers() {
        users = new HashMap<String, String>();
        users.put("BC:6E:64:FA:EF:E5", "Albert Johan");

    }

    private String getUsers(String deviceId) {
        return users.get(deviceId);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]

        Log.i(TAG, "Cannot read values");
        heartRateListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    String Value1 = val.toString();
                    if (Double.valueOf(Value1) == 0.0) {
                        textHeartRate.setText("83 Heart rate sync error");
                    }
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);

                    HEART_RATE = dataPoint.getValue(field).toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//stuff that updates ui
                            textHeartRate.setText(HEART_RATE);
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                            Date currentLocalTime = cal.getTime();
                            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
// you can get seconds by adding  "...:ss" to it
                            date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                            String localTime = date.format(currentLocalTime);
                            currentTime.setText(localTime);
                        }
                    });


                    Log.i(TAG, "Heart rate" + HEART_RATE);

                    if (null != dataSet) {
                        Log.i(TAG, "Inside loopdata");
                        /* Insert DataPoint to DataSet */
                        // for each data point (startTime, endTime, stepDeltaValue):
                        DataPoint point = dataSet.createDataPoint()
                                .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                        //TODO mentioned in presentation
                        point.getValue(field).setFloat(val.asFloat());
                        // point.setIntValues(78);
                        /*     point.getValue(dateType.getFields().get(0)).setInt(sys);
                        point.getValue(dateType.getFields().get(1)).setInt(dia);
                        point.getValue(DataTypes.Fields.BPM).setFloat(bpm); */
                        dataSet.add(point);

                    } /*  if(null != dataSet) */
                }
            }
        };

        // 1. Subscribe to fitness data (see Recording Fitness Data)
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_HEART_RATE_BPM)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });


        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                        .setSamplingRate(5, TimeUnit.SECONDS)
                        .build(),
                heartRateListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Error" + status.getStatusCode());
                            Log.i(TAG, "Description" + status.getStatusMessage());
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }


    private void startJouney() {
        Log.i(TAG, "Success session started");
        sessionStartTime = System.currentTimeMillis();
        sessionLastTime = System.currentTimeMillis();


        fitInsertTimer.schedule(fitTimerTask, 120000, 120000);
    }


    private void stopJourney() {
        Log.i(TAG, "Session stopped");
        fitInsertTimer.cancel();

    }

    private void endUserSession() {
        Log.i(TAG, "END SESSION CALLED :");
        String journeyId = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        String sessionId = Utils.getFromPrefrences(getApplicationContext(), "sessionid");
        ApiService.getInstance().doEndSession(getApplicationContext(), journeyId, sessionId, Utils.getTimeStamp(), updateLocationTimer, locationTimerTask, heartRateUpdateTimer, heartRateTimerTask, textWelcomeMsg, this);

    }

    private void doAuthorizeUser() {

        String macID = Utils.getFromPrefrences(getApplicationContext(), "MACID");

        if (macID.isEmpty()) {
            macID = "BC:6E:64:FA:EF:E5";
        }

        String journeyId = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        if (journeyId.isEmpty()) {
            Log.i(TAG, "START JOURNEY :");
            initTimer();
            ApiService.getInstance().doStartJourney(getApplicationContext(), macID, updateLocationTimer, locationTimerTask, heartRateUpdateTimer, heartRateTimerTask, textWelcomeMsg, this);
        } else {
            //authorize service api
            Log.i(TAG, "AUTHORIZE  :");
            ApiService.getInstance().doAuthorize(getApplicationContext(), macID, updateLocationTimer, locationTimerTask, heartRateUpdateTimer, heartRateTimerTask, textWelcomeMsg, this);
        }

        layoutSingIn.setVisibility(View.GONE);
        layoutSignOut.setVisibility(View.VISIBLE);
        layoutHealthStatus.setVisibility(View.VISIBLE);
        layoutBtnJourney.setVisibility(View.VISIBLE);

//        updateLocationTimer.schedule(locationTimerTask, 60000, 60000);
//        heartRateUpdateTimer.schedule(heartRateTimerTask, 60000, 60000);

    }

    private void initTimer() {

        //Setting up location timer.
        locationTimerTask = new LocationTimerTask();
        updateLocationTimer = new Timer();

        //Setting up heart rate timer.
        heartRateTimerTask = new HeartRateTimerTask();
        heartRateUpdateTimer = new Timer();

    }

    private void endJourney() {
        ApiService.getInstance().doStopJourney(getApplicationContext());
    }

    private void onSignedOut() {
       /* updateUI(false);
        mStatus.setText(R.string.status_signed_out); */
        Log.i(TAG, "onSignedOut!!!");

        ApiService.getInstance().doStopJourney(getApplicationContext());

        if (mClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mClient);
            mClient.disconnect();
        }
        layoutSignOut.setVisibility(View.GONE);
        layoutSingIn.setVisibility(View.VISIBLE);
        layoutHealthStatus.setVisibility(View.GONE);
        layoutBtnJourney.setVisibility(View.GONE);
    }

    View.OnClickListener clickListenerSignOut = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            finish();
        }
    };

    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider disabled");
    }


    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider enabled");
    }


    public void stopTimer() {

        updateLocationTimer.cancel();
        updateLocationTimer.purge();
        heartRateUpdateTimer.cancel();
        heartRateUpdateTimer.purge();

    }

    public void startTimer() {

        locationTimerTask = new LocationTimerTask();
        updateLocationTimer = new Timer();

        heartRateTimerTask = new HeartRateTimerTask();
        heartRateUpdateTimer = new Timer();

//        heartRateCheckTimerTask = new HeartRateCheckTimerTask();
//        heartRateCheckTimer = new Timer();

//        heartRateCheckTimer.schedule(heartRateCheckTimerTask, 5000, 5000);
        updateLocationTimer.schedule(locationTimerTask, 60000, 60000);
        heartRateUpdateTimer.schedule(heartRateTimerTask, 60000, 60000);

    }

    //Inits the Google map
    private void initMap(Bundle savedInstanceState) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (savedInstanceState != null) {
            mapFragment = (SupportMapFragment) fragmentManager.findFragmentByTag(FRAGMENT_GOOGLE_MAP_TAG);
        }

        if (mapFragment == null) {

            mapFragment = new SupportMapFragment();
            fragmentTransaction.add(R.id.map_view, mapFragment, FRAGMENT_GOOGLE_MAP_TAG).commit();

            mMap = mapFragment.getMap();

            mHandler = new Handler();

            prepareMap();

        } else {
            fragmentTransaction.attach(mapFragment).commit();
        }

        Marker marker = null;
    }

    private void prepareMap() {
        //check every seconds, map is ready or not.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (IsMapReady()) {
                    if (mListener != null)
                        mListener.onMapReady();
                    return;
                }
                prepareMap();
            }
        }, 1000);
    }

    //Enables the basic functionality of Google map
    public boolean IsMapReady() {
        if (mMap == null && mapFragment != null) {
            mMap = mapFragment.getMap();
        }

        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
//            mMap.setBuildingsEnabled(true);
//            mMap.setIndoorEnabled(true);
            mMap.setOnMapClickListener(onMapClickListener);
            mMap.setOnMarkerClickListener(onMarkerClickListener);
            mMap.setMyLocationEnabled(true);
            mMap.setTrafficEnabled(true);
            mMap.setOnMyLocationChangeListener(locationChangeListener);
            return true;
        }
        return false;
    }

    LatLng prevLat;

    //Whenever the location changes its notified by listener
    GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {

            if (!isMapReady) {
                mMap.clear();

                prevLatitude = location.getLatitude();
                prevLongitude = location.getLongitude();
                prevLat = new LatLng(prevLatitude, prevLongitude);

                MarkerOptions mp = new MarkerOptions();

                mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck));

                mp.title("my position");

//                plotLocation(location, mMap);

                mMap.addMarker(mp);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15));

                isMapReady = true;
            }
        }
    };

    public void plotLocation(Location location, GoogleMap mMap) {

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck));

        mp.title("my position");

        prevLatitude = latitude;
        prevLongitude = longitude;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitude, longitude);

        PolylineOptions pOptions = new PolylineOptions()
                .width(10)
                .color(Color.GREEN)
                .geodesic(false);
        for (int z = 0; z < routePoints.size(); z++) {
            LatLng point = routePoints.get(z);
            pOptions.add(point);
        }
        line = mMap.addPolyline(pOptions);
        routePoints.add(latLng);

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 18));
    }

    private boolean isValidPosition(LatLng l) {
        return l == null || l.latitude == 0 || l.longitude == 0;
    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        Intent intent = new Intent(getApplicationContext(), LocationTracker.class);
        stopService(intent);

        String journeyID = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        String sessionID = Utils.getFromPrefrences(getApplicationContext(), "sessionid");

        if (!sessionID.isEmpty()) {
            //end user Session
            endUserSession();
        }

        if (!journeyID.isEmpty()) {

            //end user journey
            endJourney();
        }

//        stopTimer();

//        updateLocationTimer.purge();
//        updateLocationTimer.cancel();
//
//        heartRateUpdateTimer.purge();
//        heartRateUpdateTimer.cancel();

        unregisterReceiver(locationReciever);

        super.onDestroy();
    }

    private GoogleApiClient buildGoogleApiClient() {
        // Build a GoogleApiClient with access to basic profile information.  We also request
        // the Plus API so we have access to the Plus.AccountApi functions, but note that we are
        // not actually requesting any Plus Scopes so we will not ask for or get access to the
        // user's Google+ Profile.

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
//                .addApi(Wearable.API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.EMAIL))
                        //.addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                        //   .addScope(new Scope(Scopes.PROFILE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                                // [END auth_build_googleapiclient_beginning]
                                //  What to do? Find some data sources!

//                                layoutSingIn.setVisibility(View.GONE);
//                                layoutSignOut.setVisibility(View.VISIBLE);
//                                layoutHealthStatus.setVisibility(View.VISIBLE);
//                                layoutBtnJourney.setVisibility(View.VISIBLE);

                                Person user = Plus.PeopleApi.getCurrentPerson(mClient);
                                if (user != null) {
                                    Log.i(TAG, "Userinfo" + user.getDisplayName());
                                    UserName = user.getDisplayName();

                                    Log.i(TAG, "Userinfo" + user.getGender());
                                    emailid = Plus.AccountApi.getAccountName(mClient);
                                    Utils.storeToPrefrences(getApplicationContext(), "email", emailid);
                                }
//                                textWelcomeMsg.setText("Welcome " + UserName);

//                                authorizeUser(emailid);

//                                ApiService.getInstance().doStartJourney(getApplicationContext());

                                dialog.cancel();

//                                getConnectedWearablesID();
//
                                startLocationUpdates();
                                findFitnessDataSources();
                                //  findLocationDataSources();
                                checkHeartRate();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    Utils.showToast(LandingScreenActivity.this, "Network Lost");
                                    dialog.cancel();
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                    Utils.showToast(LandingScreenActivity.this, "Google Service Disconnected");
                                    dialog.cancel();
                                }

                                dialog.cancel();
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {


                                // Could not connect to Google Play Services.  The user needs to select an account,
                                // grant permissions or resolve an error in order to sign in.
                                // Refer to the javadoc for ConnectionResult to see possible error codes.
                                Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                                        + result.getErrorCode());

                                if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                                    // An API requested for GoogleApiClient is not available. The device's current
                                    // configuration might not be supported with the requested API or a required component
                                    // may not be installed, such as the Android Wear application. You may need to use a
                                    // second GoogleApiClient to manage the application's optional APIs.
                                    Log.w(TAG, "API Unavailable.");
                                } else if (!mIsResolving && mShouldResolve) {
                                    // The user already clicked the sign in button, we should resolve errors until
                                    // success or they click cancel.
                                    resolveSignInError(result);
                                } else {
                                    Log.w(TAG, "Already resolving.");
                                }

                                // Not connected, show signed-out UI
//                                onSignedOut();

                                dialog.cancel();


                               /* Log.i(TAG, "Connection failed. Cause: " + result.toString());
                               if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                } */
                            }
                        }
                );

//        mServerAuthHandler.checkServerAuthConfiguration(WEB_CLIENT_ID);
//        builder = builder.requestServerAuthCode(WEB_CLIENT_ID, mServerAuthHandler);

        return (builder.build());
    }

    private void checkHeartRate() {

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (Double.valueOf(textHeartRate.getText().toString()) == 0.0) {
                    //  Toast.makeText(getApplicationContext(), "Connect your band properly and relaunch app", Toast.LENGTH_LONG).show();
                    UpdateUi();
                    //showToast();
                } else {
                    textHeartRate.setVisibility(View.VISIBLE);
                    // textNotify.setVisibility(View.GONE);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    private void showToast() {
        Toast.makeText(getApplicationContext(), "Connect your band properly and relaunch app", Toast.LENGTH_LONG).show();
    }

    private void UpdateUi() {
        Log.i(TAG, "Heart value" + textHeartRate.getText().toString());
        //  textHeartRate.setVisibility(View.GONE);
        //  textNotify.setVisibility(View.VISIBLE);

        textHeartRate.setText("78.0 Heart rate sync error.Connect your band properly and relaunch the app");

        Utils.showToast(getApplicationContext(), "Connect your band properly and relaunch app");
        //   textHeartRate.setText("Heart rate sync error,relaunch app");

    }

    private void getConnectedWearablesID() {
        Wearable.NodeApi.getConnectedNodes(mClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    Log.i(TAG + "NODE", node.getId());
                }
            }
        });
    }

    private void doShowUserProfileScreen(String userName, final String emailid, final String userid) {

        LayoutInflater li = LayoutInflater.from(LandingScreenActivity.this);
        View promptsView = li.inflate(R.layout.user_profile, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LandingScreenActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        textUserName = (TextView) promptsView.findViewById(R.id.editTextDialogUserInput);
        textUserName.setText(userName);
        textemailId = (TextView) promptsView.findViewById(R.id.editTextDialogUserInput2);
        textemailId.setText(emailid);
        editTextDOB = (EditText) promptsView.findViewById(R.id.editTextDOB);
        editUserHeightInchs = (EditText) promptsView.findViewById(R.id.editTextHeightInches);
        editUserHeightft = (EditText) promptsView.findViewById(R.id.editTextHeightFeet);
        editUserWeight = (EditText) promptsView.findViewById(R.id.editTextWeight);

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate(editTextDOB);
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("SUBMIT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String userName = textUserName.getText().toString();
                                String email = "digitalpumpkin@gmail.com"; //textemailId.getText().toString();
                                String dob = editTextDOB.getText().toString();
                                String height = editUserHeightft.getText().toString() + "." + editUserHeightInchs.getText().toString();
                                String weight = editUserWeight.getText().toString();
                                String macID = "BC:6E:64:FA:EF:E5";//Utils.getFromPrefrences(getApplicationContext(), "MACID");
                                layoutSingIn.setVisibility(View.GONE);
                                layoutSignOut.setVisibility(View.VISIBLE);
                                layoutHealthStatus.setVisibility(View.VISIBLE);
                                layoutBtnJourney.setVisibility(View.VISIBLE);
//                                ApiService.getInstance().doAuthorize(getApplicationContext(), email, dob, height, weight, macID, userid);
//                                ApiService.getInstance().doGetSleepData(getApplicationContext(),"digitalpumpkin160915@gmail.com","1449901800000","1450350578041","BC:6E:64:FA:EF:E5");
//                                dialog.cancel();
                                Handler mHandler = new Handler();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        menus.setVisible(true);
                                        Utils.showToast(getApplicationContext(), "Sleep Data synced");
                                    }
                                }, 5000);

                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showDate(EditText editTextDOB) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog1 = new DatePickerDialog(LandingScreenActivity.this,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog1.show();
    }


    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            editTextDOB.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("/").append(mDay).append("/")
                    .append(mYear).append(" "));
//            System.out.println(v.getText().toString());


        }
    }

    /**
     * Starts an appropriate intent or dialog for user interaction to resolve the current error
     * preventing the user from being signed in.  This is normally the account picker dialog or the
     * consent screen where the user approves the scopes you requested,
     */
    private void resolveSignInError(ConnectionResult result) {
        if (result.hasResolution()) {
            // Google play services provided a resolution
            try {
                // Attempt to resolve the Google Play Services connection error
                result.startResolutionForResult(this, RC_SIGN_IN);
                mIsResolving = true;

                mIsResolving = false;
                mClient.connect();
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Sign in intent could not be sent.", e);

                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mIsResolving = false;
                mClient.connect();
            }
        } else {
            // Google play services did not provide a resolution, display error message
            displayError(result.getErrorCode());
        }
    }

    private void displayError(int errorCode) {
        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.w(TAG, "Google Play services resolution cancelled");
                            mShouldResolve = false;
                            Log.i(TAG, "Signed out");
                            //mStatus.setText(R.string.status_signed_out);
                        }
                    }).show();
        } else {
            // No default Google Play Services error, display a Toast
            String errorMsg = "Google Play services error could not be resolved: " + errorCode;
            Log.e(TAG, errorMsg);

            // Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            mShouldResolve = false;
            Log.i(TAG, "Signed out");
            //mStatus.setText(R.string.status_signed_out);
        }
    }

    /*Timer class */
    class FitInsertSessionsTimerTask extends TimerTask {

        public void run() {

            /* update fit session */
            // InsertSessionToFit();

        }
    }

    /*Timer class */
    public class LocationTimerTask extends TimerTask {

        public void run() {

            sendLocationUpdate();

        }
    }

    class SessionListener extends TimerTask {

        @Override
        public void run() {

        }
    }

    /*Heart rate time task*/
    public class HeartRateTimerTask extends TimerTask {

        @Override
        public void run() {
            sendHeartRateUpdate();
        }
    }

    /*Heart rate time task*/
    public class HeartRateCheckTimerTask extends TimerTask {

        @Override
        public void run() {
            String tempHeartRate = HEART_RATE.substring(0, HEART_RATE.length() - 2);

            if (tempHeartRate.equalsIgnoreCase("0")) {

                Utils.showToast(getApplicationContext(), "Band is not connected properly.");
            }
        }

    }


    private void sendHeartRateUpdate() {

        String journeyId = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        String sessionId = Utils.getFromPrefrences(getApplicationContext(), "sessionid");
        String tempHeartRate = HEART_RATE.substring(0, HEART_RATE.length() - 2);
        ApiService.getInstance().doUpdateHeartRate(getApplicationContext(), journeyId, sessionId, Integer.parseInt(tempHeartRate), Utils.getTimeStamp(), Utils.getTimeStamp());

    }

    private void sendLocationUpdate() {

        String journeyId = Utils.getFromPrefrences(getApplicationContext(), "journeyid");
        String sessionId = Utils.getFromPrefrences(getApplicationContext(), "sessionid");
        JSONObject jsonObject = new JSONObject();
        try {

            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            JSONObject jsonObject2 = jsonArray.getJSONObject(jsonArray.length() - 1);

            double lat1 = jsonObject1.getDouble("latitude");

            double distance = Utils.directDistance(jsonObject1.getDouble("latitude"), jsonObject1.getDouble("longitude"),
                    jsonObject2.getDouble("latitude"), jsonObject2.getDouble("longitude"));

            jsonObject.put("journeyid", journeyId);
            jsonObject.put("sessionid", sessionId);
            jsonObject.put("locations", jsonArray);
            jsonObject.put("distance", Math.round(distance));

//            Location location1 = new Location("Location A");
//            location1.setLatitude(jsonObject1.getDouble("latitude"));
//            location1.setLatitude(jsonObject1.getDouble("longitude"));
//
//            Location location2 = new Location("Location B");
//            location2.setLatitude(jsonObject2.getDouble("latitude"));
//            location2.setLongitude(jsonObject2.getDouble("longitude"));
//
//            double dist = location1.distanceTo(location2);

            ApiService.getInstance().doUpdateLocation(getApplicationContext(), jsonObject);

            jsonArray = new JSONArray();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void InsertSessionToFit() {

        // Create a session with metadata about the activity.
        Session inSertFitSession = new Session.Builder()
                .setName(SESSION_NAME)
                .setDescription(SESSION_DESCRIPTION)
                .setIdentifier(SESSION_IDENTIFIER + "1")
                .setActivity(FitnessActivities.RUNNING)
                .setStartTime(sessionLastTime, TimeUnit.MILLISECONDS)
                .setEndTime(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();


        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(inSertFitSession)
                .addDataSet(dataSet)
                .addDataSet(dataSetLocation)
                        //.addDataSet(dataSetLoc)
                        // .addDataSet(activitySegments)
                .build();


        PendingResult<Status> pendingResult =
                Fitness.SessionsApi.insertSession(mClient, insertRequest);

        //5. Check the result asynchronously
        // (The result is not immediately available)
        pendingResult.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Inserted session successfully");
                    sessionLastTime = System.currentTimeMillis();


                    dataSet = null;
                    dataSetLocation = null;
                    dataSetLoc = null;
                    dataSet = DataSet.create(heartRateDs);
                    dataSetLocation = DataSet.create(locationDs);
                    dataSetLoc = DataSet.create(locationDs2);


                } else {
                    //TODO handle failure
                    Log.i(TAG, "Failure to insert session");
                }

            }
        });
    }

    private void findFitnessDataSources() {
        // [START find_data_sources]
        Log.i(TAG, "Inside");
//        if (isAppInstalled("com.sonymobile.hostapp.everest")) {
////            doShowUserProfileScreen();
//        } else {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Sony App is not installed.")
//                    .setCancelable(false)
//                    .setPositiveButton("Install now", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
////                            Intent intent = new Intent(Intent.ACTION_MAIN);
////                            intent.addCategory(Intent.CATEGORY_HOME);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse("market://details?id=com.sonymobile.hostapp.everest"));
//                            try {
//                                startActivity(intent);
//                            } catch (Exception e) {
//                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sonymobile.hostapp.everest"));
//                            }
//                            if (mClient.isConnected()) {
//                                Plus.AccountApi.clearDefaultAccount(mClient);
//                                mClient.disconnect();
//                                onSignedOut();
//                            }
//                            // startActivity(intent);
//
//                        }
//                    });
//            AlertDialog alert = builder.create();
//            alert.show();
//
//        }

        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                // .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                        // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                            Log.i(TAG, "Device: " + dataSource.getDevice());

                            if (dataSource.getDevice() != null) {
                                Log.i(TAG, "Model" + dataSource.getDevice().getModel());
                                Log.i(TAG, "Type" + dataSource.getDevice().getType());
                                Log.i(TAG, "UID" + dataSource.getDevice().getUid());
                                Log.i(TAG, "Manufacturer" + dataSource.getDevice().getManufacturer());
                                Log.i(TAG, "Version" + dataSource.getDevice().getVersion());
                                try {
                                    File root = Environment.getExternalStorageDirectory();
                                    Log.i(TAG,"Root" +root);
                                    if (root.canWrite()) {
                                        File gpslogfile = new File(root, "smartDriver_log.txt");
                                        FileWriter gpswriter = new FileWriter(gpslogfile, true);
                                        BufferedWriter out = new BufferedWriter(gpswriter);
                                        out.append("***********************************************");
                                        out.append(LINE_SEPARATOR);
                                        out.append(dataSource.toString());
                                        out.append(LINE_SEPARATOR);
                                           out.append(dataSource.getDevice().toString());
                                          // out.append(dataSource.getDevice().toString());

                                        out.append(LINE_SEPARATOR);
                                        out.append("Manufacturer" +dataSource.getDevice().getManufacturer().toString());
                                        out.append(LINE_SEPARATOR);
                                        out.append("Model" +dataSource.getDevice().getModel().toString());
                                        out.append(LINE_SEPARATOR);
                                        out.append(dataSource.getDevice().getVersion().toString());
                                        out.append(LINE_SEPARATOR);
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    Log.d("Log: ", "Could not write file " + e.getMessage());
                                }
                            }

                            //Let's register a listener to receive Activity data!
                            //  if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)

                            if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                                    && heartRateListener == null) {

                                /*registerFitnessDataListener(dataSource,
                                        DataType.TYPE_LOCATION_SAMPLE); */

                                 /* Work accurately
                                        - For sampling rate
                                        - data souce sony mobile app
                                            - with google fit sharing enabled
                                             - Heart Activity Mode On
                                             - Works only in foreground */
                                    /*if((dataSource.getDevice() != null) &&
                                        (dataSource.getDevice().toString().equals("Device{samsung:SM-G925I:4304b702::1:2}")) )
                                   */
//
//                                if ((dataSource.getDevice() != null) &&
//                                        (dataSource.getDevice().toString().contains("Device{Sony Mobile Communications Inc.:SWR12:acf4263c::3:0}"))) {
                                if ((dataSource.getDevice() != null) &&
                                        (dataSource.getDevice().toString().contains("Device{Sony Mobile Communications Inc}"))){
                                    Log.i(TAG, "Data source for TYPE_HEART_RATE_BPM found!  Registering.");

                                    findDeviceID();
                                    registerFitnessDataListener(dataSource,
                                            DataType.TYPE_HEART_RATE_BPM);
                                }
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    private void findDeviceID() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices) {
            bt.getBondState();
            String name = bt.getBluetoothClass().toString();
            if (name.equalsIgnoreCase("SWR12")) {
                Utils.storeToPrefrences(getApplicationContext(), "MACID", bt.getAddress());
//                bt.createBond();
//                bt.setPairingConfirmation(true);
            }
            s.add("Name: " + bt.getName() + "MAC " + bt.getAddress());
        }
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }


    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */

    /**
     * Unregister the listener with the Sensors API.
     */
    private void unregisterFitnessDataListener() {
        if (heartRateListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mClient,
                heartRateListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
        heartRateListener = null;
    }


    private void unregisterLocationListener() {
        if (heartRateListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mClient,
                mListenerLocation)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
        mListenerLocation = null;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menus = (MenuItem) menu.findItem(R.id.menu_item_3);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_item_2):

                Log.i(TAG, "STOP JOURNEY CALLED:");
                ApiService.getInstance().doStopJourney(getApplicationContext()); //stop journey

                if (!Utils.getFromPrefrences(getApplicationContext(), "sessionid").isEmpty()) {
                    endUserSession();
                    layoutSignOut.setVisibility(View.GONE);
                    layoutSingIn.setVisibility(View.VISIBLE);
                    layoutHealthStatus.setVisibility(View.GONE);
                    layoutBtnJourney.setVisibility(View.GONE);
                }

//                sendHeartRateUpdate();
//                ApiService.getInstance().doStartJourney(getApplicationContext()); //start journey
//                ApiService.getInstance().doStopJourney(getApplicationContext()); // stop journey
//                ApiService.getInstance().doGetUserProfile(getApplicationContext()); // end journey
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void clearAccessToken() {
        Plus.AccountApi.clearDefaultAccount(mClient);
        Plus.AccountApi.revokeAccessAndDisconnect(mClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            onSignedOut();

//                            Intent intent = new Intent(LandingScreenActivity.this, RegistrationActivity.class);
//                            startActivity(intent);

                            Toast.makeText(getApplicationContext(), "Access tokens cleared successfully",
                                    Toast.LENGTH_LONG).show();
                        }
                        // After we revoke permissions for the user with a
                        // GoogleApiClient we must discard it and create a new one.
                        //  mClient = buildGoogleApiClient();
                        // mClient.connect();
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                // If the error resolution was successful we should continue
                // processing errors.  Otherwise, stop resolving.
                mShouldResolve = (resultCode == RESULT_OK);

                mIsResolving = false;
                if (!mClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mClient.connect();
                }
                break;

           /* case RC_USER_LOGIN:

                if (data != null) {

                    menuUpload.setVisible(true);

                    userId = data.getStringExtra("userid");
//                    String userName = databaseHelper.getUserData(getApplicationContext(), userId);
                    String userEmail = getUserEmail(userId);

                    layoutSingIn.setVisibility(View.GONE);
                    layoutSignOut.setVisibility(View.VISIBLE);
                    layoutHealthStatus.setVisibility(View.VISIBLE);
                    layoutBtnJourney.setVisibility(View.VISIBLE);

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            menuUpload.setVisible(false);
                            Utils.showToast(getApplicationContext(), "Sleep data synched.");

                        }
                    }, 10000);
                    textWelcomeMsg.setText("Welcome " + userName);


                    // authorizeUser(userName, userEmail, userId);
                    //   ApiService.getInstance().doGetSleepData(getApplicationContext(), "digitalpumpkin160915@gmail.com", "1449901800000", "1450350578041", "BC:6E:64:FA:EF:E5");
                }
                break;*/
        }
    }

    private boolean isSessionEnded;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Toast.makeText(getApplicationContext(), "Your session will end in next 5 seconds.", Toast.LENGTH_LONG).show();
//        sessionTimer.schedule(sessionListener, 0, 5000);
//        showAlertDialog(getApplicationContext());
    }

    public Dialog showAlertDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("End Session.");
        builder.setMessage("Leaving the application will end your session. Do you want to continue?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;

    }
}
