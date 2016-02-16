package drive.solution.com.drivecoach.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.lang.ref.WeakReference;

/**
 * Created by M1032185 on 12/12/2015.
 */
public class ApiClient {

    public static final String TAG = "DC1";

    private static Context mContext;

    private static Activity mActivity;

    private static final int RC_SIGN_IN = 0;

    private static boolean authInProgress = false;

    /* Is there a ConnectionResult resolution in progress? */
    private static boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private static boolean mShouldResolve = false;

    public static GoogleApiClient getApiClient(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        return mClient;
    }

    private static GoogleApiClient mClient = buildApiClient(mContext, mActivity);

    private static GoogleApiClient buildApiClient(Context context, Activity mActivity) {
        // Build a GoogleApiClient with access to basic profile information.  We also request
        // the Plus API so we have access to the Plus.AccountApi functions, but note that we are
        // not actually requesting any Plus Scopes so we will not ask for or get access to the
        // user's Google+ Profile.

//        WeakReference activityWeakReference = new WeakReference<>(mActivity);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
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
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener);

        return (builder.build());
    }

    private static GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Log.i(TAG, "Connected!!!");
            // Now you can make calls to the Fitness APIs.

//            layoutSingIn.setVisibility(View.GONE);
//            layoutSignOut.setVisibility(View.VISIBLE);
//            layoutHealthStatus.setVisibility(View.VISIBLE);
//            layoutBtnJourney.setVisibility(View.VISIBLE);

//            Person user = Plus.PeopleApi.getCurrentPerson(mClient);
//            if (user != null) {
//                Log.i(TAG, "Userinfo" + user.getDisplayName());
//                UserName = user.getDisplayName();
//
//                Log.i(TAG, "Userinfo" + user.getGender());
//                emailid = Plus.AccountApi.getAccountName(mClient);
//            }

//            textWelcomeMsg.setText("Welcome " + UserName);

//            authorizeUser(emailid);

//            ApiService.getInstance().doStartJourney(getApplicationContext());

//            dialog.cancel();

//            getConnectedWearablesID();

//            findFitnessDataSources();
//            findLocationDataSources();

//            startLocationUpdates();

        }

        @Override
        public void onConnectionSuspended(int connectionStatus) {
            // If your connection to the sensor gets lost at some point,
            // you'll be able to determine the reason and react to it here.
            if (connectionStatus == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                Log.i(TAG, "Connection lost.  Cause: Network Lost.");
            } else if (connectionStatus == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
            }
        }
    };

    private static GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

            // Could not connect to Google Play Services.  The user needs to select an account,
            // grant permissions or resolve an error in order to sign in.
            // Refer to the javadoc for ConnectionResult to see possible error codes.
            Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                    + connectionResult.getErrorCode());

            if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                // An API requested for GoogleApiClient is not available. The device's current
                // configuration might not be supported with the requested API or a required component
                // may not be installed, such as the Android Wear application. You may need to use a
                // second GoogleApiClient to manage the application's optional APIs.
                Log.w(TAG, "API Unavailable.");
            } else if (!mIsResolving && mShouldResolve) {
                // The user already clicked the sign in button, we should resolve errors until
                // success or they click cancel.
                resolveSignInError(connectionResult);
            } else {
                Log.w(TAG, "Already resolving.");
            }
        }
    };

    /**
     * Starts an appropriate intent or dialog for user interaction to resolve the current error
     * preventing the user from being signed in.  This is normally the account picker dialog or the
     * consent screen where the user approves the scopes you requested,
     */
    private static void resolveSignInError(ConnectionResult result) {
        if (result.hasResolution()) {
            // Google play services provided a resolution
            try {
                // Attempt to resolve the Google Play Services connection error
                result.startResolutionForResult(mActivity, RC_SIGN_IN);
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

    private static void displayError(int errorCode) {
        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, mActivity, RC_SIGN_IN,
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
}
