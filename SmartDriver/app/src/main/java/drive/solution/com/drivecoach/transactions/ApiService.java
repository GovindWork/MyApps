package drive.solution.com.drivecoach.transactions;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import drive.solution.com.drivecoach.http.TransactionProcessor;
import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.utils.Utils;
import drive.solution.com.drivecoach.views.LandingScreenActivity;

/**
 * Created by M1032185 on 11/28/2015.
 */
public class ApiService {

    private static final String TAG = "Smart_Driver";

    private static ApiService instance = new ApiService();

    private ApiService() {

    }

    public static ApiService getInstance() {
        return instance;
    }

    //Starts journey
    public void doStartJourney(final Context context, final String macID, final Timer updateLocationTimer, final LandingScreenActivity.LocationTimerTask locationTimerTask,
                               final Timer heartRateUpdateTimer, final LandingScreenActivity.HeartRateTimerTask heartRateTimerTask, final TextView textWelcomeMsg, final LandingScreenActivity landingScreenActivity) {

        JSONFactory jsonFactory = new JSONFactory();
        String vin = Utils.getFromPrefrences(context, "vin");
        JSONObject jsonObject = jsonFactory.getStartJourneyParams(vin, Utils.getTimeStamp());
        StartJourneyTransaction startJourneyTransaction = new StartJourneyTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                try {
                    JSONObject jsonObject1 = new JSONObject(data);
                    String journeyId = jsonObject1.getString("journeyid");
                    Utils.storeToPrefrences(context, "journeyid", journeyId);

                    doAuthorize(context, macID, updateLocationTimer, locationTimerTask, heartRateUpdateTimer, heartRateTimerTask, textWelcomeMsg, landingScreenActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, "Journey Started.", Toast.LENGTH_LONG).show();


            }
        });
        transactionProcessor.execute(startJourneyTransaction);
    }

    //Stops journey
    public void doStopJourney(final Context context) {

        JSONFactory jsonFactory = new JSONFactory();
        String vin = Utils.getFromPrefrences(context, "journeyid");
        JSONObject jsonObject = jsonFactory.getStopJourneyParams(vin, Utils.getTimeStamp());
        StopJourneyTransaction stopJourneyTransaction = new StopJourneyTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                Toast.makeText(context, "Journey Ended.", Toast.LENGTH_LONG).show();
                //reset journey id
                Utils.storeToPrefrences(context, "journeyid", "");

            }
        });
        transactionProcessor.execute(stopJourneyTransaction);
    }

    //Update location details to cloud
    public void doUpdateLocation(Context context, JSONObject jsonObject) {
        LocationTransaction locationTransaction = new LocationTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {


            }
        });
        transactionProcessor.execute(locationTransaction);
    }

    //Updates heart rate to cloud
    public void doUpdateHeartRate(final Context context, String journeyId, String sessionId, int heartRate, long startTime, long endTime) {
        JSONFactory jsonFactory = new JSONFactory();
        JSONObject jsonObject = jsonFactory.getHeartRateParams(journeyId, sessionId, heartRate, startTime, endTime);
        HeartRateTransaction heartRateTransaction = new HeartRateTransaction(context, jsonObject);
        final int r;
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

            }
        });
        transactionProcessor.execute(heartRateTransaction);
    }

    //Ends driver session
    public void doEndSession(final Context context, String journeyId, String sessionId, long endTime, final Timer updateLocationTimer,
                             final LandingScreenActivity.LocationTimerTask locationTimerTask, final Timer heartRateUpdateTimer,
                             final LandingScreenActivity.HeartRateTimerTask heartRateTimerTask, TextView textWelcomeMsg, final LandingScreenActivity landingScreenActivity) {

        JSONFactory jsonFactory = new JSONFactory();
        JSONObject jsonObject = jsonFactory.getEndSessionParams(journeyId, sessionId, endTime);
        EndSessionTransaction endSessionTransactionen = new EndSessionTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                Utils.storeToPrefrences(context, "sessionid", "");


                landingScreenActivity.stopTimer();

//                Log.d(TAG, "Timer cancelled");

//                updateLocationTimer.schedule(locationTimerTask, 60000, 60000);
//                heartRateUpdateTimer.schedule(heartRateTimerTask, 60000, 60000);

//                Log.d(TAG, "Timer started");

            }
        });
        transactionProcessor.execute(endSessionTransactionen);
    }

    //Authorizes user submits user related information to volvo cloud.
    public void doAuthorize(final Context context, String deviceid, final Timer updateLocationTimer, final LandingScreenActivity.LocationTimerTask
            locationTimerTask, final Timer heartRateUpdateTimer, final LandingScreenActivity.HeartRateTimerTask heartRateTimerTask, final TextView textWelcomeMsg, final LandingScreenActivity landingScreenActivity) {

        String userEmail = "digitalpumpkin@gmail.com";
        String dob = "25-11-1989";
        String userHeight = "5.2";
        String userWeight = "65";

        JSONFactory jsonFactory = new JSONFactory();
        final String journeyId = Utils.getFromPrefrences(context, "journeyid");
        JSONObject jsonObject = jsonFactory.getDriverParams(userEmail, userHeight, userWeight, journeyId, deviceid);
        AuthorizeDriverTransaction authorizeDriverTransaction = new AuthorizeDriverTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);

                    String sessionId = jsonObject.getString("sessionid");
                    String userName = jsonObject.getString("firstname");

                    Utils.showToast(context, "User authorized.");
                    Utils.storeToPrefrences(context, "sessionid", sessionId);
                    Utils.storeToPrefrences(context, "firstname", userName);
                    Utils.storeToPrefrences(context, "authorized", "auth");

                    landingScreenActivity.startLocationUpdates();

                    landingScreenActivity.startTimer();

//                    updateLocationTimer.schedule(locationTimerTask, 60000, 60000);
//                    heartRateUpdateTimer.schedule(heartRateTimerTask, 60000, 60000);

                    textWelcomeMsg.setText("Welcome " + userName);

//                    Log.i("DC1", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        transactionProcessor.execute(authorizeDriverTransaction);
    }

    public void doSetSleepData(Context context, String deviceId, String sleepData) {

        JSONFactory jsonFactory = new JSONFactory();
        final String emailId = Utils.getFromPrefrences(context, "email");
        JSONObject jsonObject = jsonFactory.getSleepParams(deviceId, emailId, sleepData);
        SleepDataTransaction sleepDataTransaction = new SleepDataTransaction(context, jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

            }
        });
        transactionProcessor.execute(sleepDataTransaction);
    }

    public void doGetSleepData(final Context context, String emailId, String startTime, final String endTime, String id) {

        final String macId = Utils.getFromPrefrences(context, "MACID");
        GetSleepDataTransaction getSleepDataTransaction = new GetSleepDataTransaction(context, emailId, startTime, endTime, id);
        TransactionProcessor processor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {
                if (data != "") {

                    doSetSleepData(context, macId, data);
                    Utils.storeToPrefrences(context, "starttime", endTime);
                    Utils.storeToPrefrences(context, "endtime", String.valueOf(Utils.getTimeStamp()));
                } else {
                    Utils.storeToPrefrences(context, "endtime", String.valueOf(Utils.getTimeStamp()));
                }
            }
        });

        processor.execute(getSleepDataTransaction);
    }

    public void initTimer() {


//        locationTimerTask = new LocationTimerTask();
//        updateLocationTimer = new Timer();
    }

}
