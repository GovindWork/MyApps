package drive.solution.com.drivecoach.views;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import drive.solution.com.drivecoach.R;
import drive.solution.com.drivecoach.http.TransactionProcessor;
import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.transactions.StopJourneyTransaction;
import drive.solution.com.drivecoach.transactions.HeartRateTransaction;
import drive.solution.com.drivecoach.transactions.JSONFactory;
import drive.solution.com.drivecoach.transactions.LocationTransaction;
import drive.solution.com.drivecoach.utils.Utils;

public class SplashScreenActivity extends AppCompatActivity {

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startMainScreen();

            }
        }, 1000);
    }

    private void startMainScreen() {
        finish();
        startActivity(new Intent(getApplicationContext(), LandingScreenActivity.class));
    }

    public void doStart(View view) {

//        JSONFactory jsonFactory = new JSONFactory();
//        StartJourneyTransaction startJourneyTransaction = new StartJourneyTransaction(getApplicationContext(), jsonFactory.getStartJourneyParams("2345"));
//        TransactionProcessor transactionProcessor = new TransactionProcessor(getApplicationContext(), new WebRequest() {
//            @Override
//            public void onDataArrived(String data) {
//
//                Toast.makeText(getApplicationContext(), "" + data, Toast.LENGTH_LONG).show();
//
//            }
//        });
//        transactionProcessor.execute(startJourneyTransaction);
    }

//    public void doAuthorize(View view) {
//
//        JSONFactory jsonFactory = new JSONFactory();
//        JSONObject jsonObject = jsonFactory.getDriverParams("1234", "rer", "qweqw", "eqwe");
//        UserProfile userProfile = new UserProfile(getApplicationContext(), jsonObject);
//        TransactionProcessor transactionProcessor = new TransactionProcessor(getApplicationContext(), new WebRequest() {
//            @Override
//            public void onDataArrived(String data) {
//
//                Toast.makeText(getApplicationContext(), "" + data, Toast.LENGTH_LONG).show();
//
//            }
//        });
//        transactionProcessor.execute(userProfile);
//    }

    public void doHeartRate(View view) {
//        JSONFactory jsonFactory = new JSONFactory();
//        JSONObject jsonObject = jsonFactory.getHeartRateParams("test", "test", 50, 43, 423);
//        HeartRateTransaction userProfile = new HeartRateTransaction(getApplicationContext(), jsonObject);
//        TransactionProcessor transactionProcessor = new TransactionProcessor(getApplicationContext(), new WebRequest() {
//            @Override
//            public void onDataArrived(String data) {
//
//                Toast.makeText(getApplicationContext(), "" + data, Toast.LENGTH_LONG).show();
//
//            }
//        });
//        transactionProcessor.execute(userProfile);
    }

    public void doUpdateLocation(View view) {
        JSONFactory jsonFactory = new JSONFactory();
        String[] locations = {"12.32312", "23.525235", "3523"};
        JSONObject jsonObject = jsonFactory.getLocationParams("test", "test", locations, "test", "test");
        LocationTransaction userProfile = new LocationTransaction(getApplicationContext(), jsonObject);
        TransactionProcessor transactionProcessor = new TransactionProcessor(getApplicationContext(), new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                Toast.makeText(getApplicationContext(), "" + data, Toast.LENGTH_LONG).show();

            }
        });
        transactionProcessor.execute(userProfile);
    }

}
