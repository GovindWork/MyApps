package drive.solution.com.drivecoach.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import org.json.JSONObject;

import drive.solution.com.drivecoach.R;
import drive.solution.com.drivecoach.http.TransactionProcessor;
import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.transactions.EndSessionTransaction;
import drive.solution.com.drivecoach.transactions.JSONFactory;
import drive.solution.com.drivecoach.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    SeekBar seekHeartInterval;

    SeekBar seekLocationInterval;

    TextView heartIntervalText;

    TextView locationIntervalText;

    private static final int MIN_INTERVAL = 2;

    private static int HEART_RATE_INTERVAL;

    private static int LOCATION_UPDATE_INTERVAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekHeartInterval = (SeekBar) findViewById(R.id.heart_rate_interval);
        seekHeartInterval.setOnSeekBarChangeListener(heartIntervalListener);
        heartIntervalText = (TextView) findViewById(R.id.text_heart_interval);
        locationIntervalText = (TextView) findViewById(R.id.text_location_interval);
        seekLocationInterval = (SeekBar) findViewById(R.id.location_update_interval);
        seekLocationInterval.setOnSeekBarChangeListener(locationIntervalListener);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setTitle("Settings");
    }

    SeekBar.OnSeekBarChangeListener heartIntervalListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int interval, boolean b) {

            if (interval < MIN_INTERVAL) {
                heartIntervalText.setText(MIN_INTERVAL + " mins");
                HEART_RATE_INTERVAL = MIN_INTERVAL;
                seekBar.setProgress(MIN_INTERVAL);
            } else {
                heartIntervalText.setText(String.valueOf(interval) + "mins");
                HEART_RATE_INTERVAL = interval;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener locationIntervalListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int interval, boolean b) {

            if (interval < MIN_INTERVAL) {
                locationIntervalText.setText(MIN_INTERVAL + " mins");
                LOCATION_UPDATE_INTERVAL = MIN_INTERVAL;
                seekBar.setProgress(MIN_INTERVAL);
            } else {
                locationIntervalText.setText(String.valueOf(interval) + "mins");
                LOCATION_UPDATE_INTERVAL = interval;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

   /* public void doEndJourney(View view) {
        JSONFactory jsonFactory = new JSONFactory();
//        JSONObject jsonObject = jsonFactory.getStopJourneyParams("");
        EndSessionTransaction endSessionTransaction = new EndSessionTransaction(getApplicationContext(), jsonObject);
        TransactionProcessor processor = new TransactionProcessor(getApplicationContext(), new WebRequest() {
            @Override
            public void onDataArrived(String data) {

            }
        });

        processor.execute(endSessionTransaction);
    }
*/
   /* public void doRevokeAccess(View view) {

        Plus.AccountApi.clearDefaultAccount(mClient);
        Plus.AccountApi.revokeAccessAndDisconnect(mClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            onSignedOut();


                            Intent intent = new Intent(LandingScreenActivity.this, RegistrationActivity.class);
                            startActivity(intent);


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
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Utils.storeToPrefrences(getApplicationContext(), "heart_rate_interval", String.valueOf(HEART_RATE_INTERVAL));
        Utils.storeToPrefrences(getApplicationContext(), "location_update_interval", String.valueOf(LOCATION_UPDATE_INTERVAL));

    }
}
