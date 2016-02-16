package drive.solution.com.drivecoach;

import android.location.Location;
//import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.content.Intent;
import android.content.IntentSender;
import android.widget.Button;
import android.view.View;
import android.view.View;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
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
import com.google.android.gms.fitness.result.SessionStopResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.plus.Plus;
import android.content.DialogInterface;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.FusedLocationProviderApi;


import java.lang.System.*;
import java.text.SimpleDateFormat;
import android.text.format.Time;
import android.widget.TextView;

//import drive.solution.com.drivecoach.views.ServerAuthHandler;


public class MainActivity extends Activity implements LocationListener{

    public static final String TAG = "DC1";
    public static final String SESSION_NAME = "JourneySession2";
    public static final String SESSION_IDENTIFIER = "JourneySesId2";
    public static final String SESSION_DESCRIPTION = "JourneySesDesc";
    public static String HEART_RATE;
    public static float ALTITUDE=50;

    // [START auth_variable_references]
    private static final int REQUEST_OAUTH = 1;

    private static final int RC_SIGN_IN = 0;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    // [END auth_variable_references]

    // [START mListener_variable_reference]
    // Need to hold a reference to this listener, as it's passed into the "unregister"
    // method in order to stop all sensors from sending data to this listener.
    private OnDataPointListener mListener = null;// heart rate listener

    private OnDataPointListener mListenerLocation = null; //Location listener
    // [END mListener_variable_reference]


    Button btnStop, btnStartJourney, btnEndJourney,  btnSignOut, btnRevokeAccess;
    SignInButton btnSignIn;
    Long sessionStartTime;
    Long sessionLastTime;

    Session session = null;

    DataSource heartRateDs, locationDs, locationDs2;
    DataSet dataSet = null;
    DataSet dataSetLocation = null;
    DataSet dataSetLoc = null;

    // Separate object to handle the logic for Server Auth Code exchange, which is optional
   // private ServerAuthHandler mServerAuthHandler;


    // Client ID for a web server that will receive the auth code and exchange it for a
    // refresh token if offline access is requested.
    //private static final String WEB_CLIENT_ID = "30325593377-s0qvn7r1r8ngog7gtmanhgf06p5m7qa5.apps.googleusercontent.com";
    //private static final String WEB_CLIENT_ID = "740258919107-qa60omats39vcp3b3ghs7pnber0m4keo.apps.googleusercontent.com";

    private static final String WEB_CLIENT_ID = "1012324710349-d2f7cpu7pq5dieh1v2ecth4ceskb22nd.apps.googleusercontent.com";


    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


    FitInsertSessionsTimerTask fitTimerTask;
    Timer fitInsertTimer;

    Session inSertFitSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView heartrate= (TextView) findViewById(R.id.textView);
        final TextView location = (TextView) findViewById(R.id.textView2);
        heartrate.setText(HEART_RATE);
        btnStop = (Button) findViewById(R.id.IdStopListener);
       // final TextView heartrate = (TextView) findViewById(R.id.textView);

       // mServerAuthHandler = new ServerAuthHandler(this);


        //Timer Initialization
        fitTimerTask = new FitInsertSessionsTimerTask();
        fitInsertTimer = new Timer();


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Unregistering");
                unregisterFitnessDataListener();
                unregisterLocationListener();
            }
        });

        // [START auth_oncreate_setup_ending]

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();
        //StartJourney - session
        btnStartJourney = (Button) findViewById(R.id.IdStartJourney);

        btnStartJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertSessionDataSource();
                //startJourneySession();
                startJouney();
            }
        });


        //EndJourney - session
        btnEndJourney = (Button)findViewById(R.id.IdEndJourney);

        btnEndJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // InsertSessionToFit();
                //stopJourneySession();
                stopJourney();
            }
        });


        btnSignIn = (SignInButton)findViewById(R.id.sign_in_button);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mShouldResolve = true;
                mClient.connect();

            }
        });


        btnSignOut = (Button) findViewById(R.id.sign_out_button);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear the default account so that Google Play services will not return an
                // onConnected callback without interaction.
                if (mClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mClient);
                    mClient.disconnect();
                }
                onSignedOut();

            }
        });

        btnRevokeAccess = (Button) findViewById(R.id.revoke_access_button);

        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear the default account and then revoke all permissions granted by the
                // user. If the user wants to sign in again, they will have to accept
                // the consent dialog.
                Plus.AccountApi.clearDefaultAccount(mClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    onSignedOut();
                                }
                                // After we revoke permissions for the user with a
                                // GoogleApiClient we must discard it and create a new one.
                                mClient = buildGoogleApiClient();
                                mClient.connect();
                            }
                        }
                );




            }
        });

        createLocationRequest();

    }

    //@Override
  /*  public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.IdStopListener:
                Log.i(TAG, "Unregistering");
                unregisterFitnessDataListener();
                unregisterLocationListener();
                break;
            case R.id.IdStartJourney:
                InsertSessionDataSource();
                //startJourneySession();
                startJouney();
                break;
            case

        }
    } */



    /*Timer class */
    class FitInsertSessionsTimerTask extends TimerTask{

        public void run() {

            /* update fit session */
            InsertSessionToFit();

        }
    }

    public void onProviderDisabled(String provider)
    {
        Log.i(TAG, "Provider disabled");
    }


    public void onProviderEnabled (String provider)
    {
        Log.i(TAG, "Provider enabled");
    }


    public void onStatusChanged (String provider, int status, Bundle extras)
    {

    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        Log.i(TAG, "LAT" + String.valueOf(mCurrentLocation.getLatitude()) + ' ' + String.valueOf(mCurrentLocation.getLongitude()) + ' '+Float.valueOf(ALTITUDE) +  ' '+String.valueOf(HEART_RATE));

        if(null != dataSetLocation)
        {
            Log.i(TAG,"Inside loop");
                        /* Insert DataPoint to DataSet */
            // for each data point (startTime, endTime, stepDeltaValue):
            DataPoint point = dataSetLocation.createDataPoint()
                    .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            //TODO mentioned in presentation
          //  point.setFloatValues((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude(), (float) mCurrentLocation.getAccuracy(), (float)mCurrentLocation.getAltitude());
            point.setFloatValues((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude(),  +Float.valueOf(ALTITUDE), +Float.valueOf(HEART_RATE));
            // point1.getValue(field).setFloat(val.asFloat());
            // point.setIntValues(78);
                        /*     point.getValue(dateType.getFields().get(0)).setInt(sys);
                        point.getValue(dateType.getFields().get(1)).setInt(dia);
                        point.getValue(DataTypes.Fields.BPM).setFloat(bpm); */

            dataSetLocation.add(point);

        } /*  if(null != dataSet) */
if(null!= dataSetLoc)
        {
            Log.i(TAG,"Inside dataset loop");
                        /* Insert DataPoint to DataSet */
            // for each data point (startTime, endTime, stepDeltaValue):
            DataPoint point = dataSetLocation.createDataPoint()
                    .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            //TODO mentioned in presentation
              point.setFloatValues((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude(), +Float.valueOf(ALTITUDE), (float) mCurrentLocation.getAccuracy());
           // point.setFloatValues((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude(),  +Float.valueOf(ALTITUDE), +Float.valueOf(HEART_RATE));
            // point1.getValue(field).setFloat(val.asFloat());
            // point.setIntValues(78);
                        /*     point.getValue(dateType.getFields().get(0)).setInt(sys);
                        point.getValue(dateType.getFields().get(1)).setInt(dia);
                        point.getValue(DataTypes.Fields.BPM).setFloat(bpm); */

            dataSetLoc.add(point);
        }



    }



    public void InsertSessionDataSource()
    {
        if(null == heartRateDs) {
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


        if(null == locationDs) {
            // 1. Create a data source
            locationDs = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                    .setName("LocationHeartrateRecorder")
                    .setType(DataSource.TYPE_RAW)
                    .build();
        }

        dataSetLocation = DataSet.create(locationDs);

        if(null == locationDs2) {
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


    public void InsertSessionToFit()
    {


        // Create a session with metadata about the activity.
        Session inSertFitSession = new Session.Builder()
                .setName(SESSION_NAME)
                .setDescription(SESSION_DESCRIPTION )
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
                    dataSetLoc= DataSet.create(locationDs2);


                } else {
                    //TODO handle failure
                    Log.i(TAG, "Failure to insert session");
                }

            }
        } );




        // Then, invoke the Sessions API to insert the session and await the result,
        // which is possible here because of the AsyncTask. Always include a timeout when
        // calling await() to avoid hanging that can occur from the service being shutdown
        // because of low memory or other conditions.
        // Log.i(TAG, "Inserting the session in the History API");
        // com.google.android.gms.common.api.Status insertStatus =
        //    Fitness.SessionsApi.insertSession(mClient, insertRequest)
        //       .await(1, TimeUnit.MINUTES);

        // Before querying the session, check to see if the insertion succeeded.
        //   if (!insertStatus.isSuccess())
        // {
          /*  Log.i(TAG, "There was a problem inserting the session: " +
                    insertStatus.getStatusMessage());
            */
        // }
        // else
        // {
        // At this point, the session has been inserted and can be read.
        //Log.i(TAG, "Session insert was successful!");
        // }

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


    private void onSignedOut() {
       /* updateUI(false);
        mStatus.setText(R.string.status_signed_out); */
        Log.i(TAG, "onSignedOut!!!");
    }

    private GoogleApiClient buildGoogleApiClient() {
        // Build a GoogleApiClient with access to basic profile information.  We also request
        // the Plus API so we have access to the Plus.AccountApi functions, but note that we are
        // not actually requesting any Plus Scopes so we will not ask for or get access to the
        // user's Google+ Profile.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
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
                                findFitnessDataSources();
                                //  findLocationDataSources();

                                startLocationUpdates();

                                // [START auth_build_googleapiclient_ending]
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
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
                                onSignedOut();



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

    //    mServerAuthHandler.checkServerAuthConfiguration(WEB_CLIENT_ID);
      //  builder = builder.requestServerAuthCode(WEB_CLIENT_ID, mServerAuthHandler);

        return(builder.build());

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    // [START auth_build_googleapiclient_beginning]
    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {

        mClient = buildGoogleApiClient();

    /*
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SESSIONS_API)
                //.addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                                // [END auth_build_googleapiclient_beginning]
                                //  What to do? Find some data sources!
                                findFitnessDataSources();

                                // [START auth_build_googleapiclient_ending]
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
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
                                }
                            }
                        }
                )
                .build(); */
    }
    // [END auth_build_googleapiclient_ending]

    // [START auth_connection_flow_in_activity_lifecycle_methods]
    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    //@Override
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    } */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }
    // [END auth_connection_flow_in_activity_lifecycle_methods]

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     *     {@link com.google.android.gms.fitness.SensorsApi
     *     #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */
    private void findLocationDataSources() {
        // [START find_data_sources]
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                // .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
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

                            //Let's register a listener to receive Activity data!
                            //  if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)

                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                    && mListenerLocation == null) {

                                /*registerFitnessDataListener(dataSource,
                                        DataType.TYPE_LOCATION_SAMPLE); */

                                 /* Work accurately
                                        - For sampling rate
                                        - data souce sony mobile app
                                            - with google fit sharing enabled
                                             - Heart Activity Mode On
                                             - Works only in foreground */
                                if((dataSource.getDevice() != null) &&
                                        (dataSource.getDevice().toString().equals("Device{samsung:SM-G925I:4304b702::1:2}")) )

                                {
                                    Log.i(TAG, "Data source for TYPE_LOCATION_SAMPLE FOUND  Registering.");
                                    registerLocationDataListener(dataSource,
                                            DataType.TYPE_LOCATION_SAMPLE);
                                }

                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerLocationDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        mListenerLocation = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);

                    if(null != dataSet)
                    {
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
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_LOCATION_SAMPLE)
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
                        .setSamplingRate(1, TimeUnit.MINUTES)
                        .build(),
                mListenerLocation)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Location Listener registered!");
                        } else {
                            Log.i(TAG, "Location Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     *     {@link com.google.android.gms.fitness.SensorsApi
     *     #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */
    private void findFitnessDataSources() {
        // [START find_data_sources]
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

                            //Let's register a listener to receive Activity data!
                            //  if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)

                            if (dataSource.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)
                                    && mListener == null) {

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

                                if((dataSource.getDevice() != null) &&
                                        (dataSource.getDevice().toString().equals("Device{Sony Mobile Communications Inc.:SWR12:acf4263c::3:0}")) )
                                {
                                    Log.i(TAG, "Data source for TYPE_HEART_RATE_BPM found!  Registering.");
                                    registerFitnessDataListener(dataSource,
                                            DataType.TYPE_HEART_RATE_BPM);
                                }
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }


    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]


        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                    HEART_RATE = dataPoint.getValue(field).toString();
                    Log.i(TAG, "Heart rate" + HEART_RATE);


                    if(null != dataSet)
                    {
                        Log.i(TAG,"Inside loopdata");
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
                        .setSamplingRate(10, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        // [END register_data_listener]
    }

    private  void startJouney()
    {
        Log.i(TAG,"Success session started");
        sessionStartTime = System.currentTimeMillis();
        sessionLastTime = System.currentTimeMillis();


        fitInsertTimer.schedule(fitTimerTask, 120000, 120000);


    }


    private void stopJourney()
    {
        Log.i(TAG,"Session stopped");
        fitInsertTimer.cancel();

    }

    private void startJourneySession()
    {

        // 2. Create a session object
        // (provide a name, identifier, description and start time)
        sessionStartTime = System.currentTimeMillis();
        session = new Session.Builder()
                .setName(SESSION_NAME)
                .setIdentifier(SESSION_IDENTIFIER+1000)
                .setDescription(SESSION_DESCRIPTION)
                .setStartTime(sessionStartTime, TimeUnit.MILLISECONDS)
                        // optional - if your app knows what activity:
                .setActivity(FitnessActivities.RUNNING)
                .build();

        // 3. Invoke the Sessions API with:
        // - The Google API client object
        // - The request object
        PendingResult<Status> pendingResult =
                Fitness.SessionsApi.startSession(mClient, session);

        // 5. Check the result asynchronously
        // (The result is not immediately available)
        pendingResult.setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Success Session Started");
                        } else {
                            //TODO handle failure
                            Log.i(TAG, "Failure Session Started");
                        }
                    }
                }
        );




    }


    private void stopJourneySession()
    {
        // 1. Invoke the Sessions API with:
        // - The Google API client object
        // - The name of the session
        // - The session indentifier
        PendingResult<SessionStopResult> pendingResult =
                Fitness.SessionsApi.stopSession(mClient, session.getIdentifier());


        // 2. Check the result (see other examples)
        pendingResult.setResultCallback(
                new ResultCallback<SessionStopResult>() {
                    @Override
                    public void onResult(SessionStopResult sessionStopResult) {
                        if (sessionStopResult.getStatus().isSuccess()) {
                            Log.i(TAG, "Success Session Stopped");
                            session = null;
                        } else {
                            //TODO handle failure
                            Log.i(TAG, "Failure Session Stopped");
                        }
                    }

                }
        );

        // 3. Unsubscribe from fitness data (see Recording Fitness Data)
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_HEART_RATE_BPM).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Success: ReccordingApi Unsubscribed");
                } else {
                    Log.i(TAG, "Failed: ReccordingApi Unsubscribed");
                }
            }
        });



    }

    /**
     * Unregister the listener with the Sensors API.
     */
    private void unregisterFitnessDataListener() {
        if (mListener == null) {
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
                mListener)
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
        mListener = null;
    }


    private void unregisterLocationListener() {
        if (mListenerLocation == null) {
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



}
