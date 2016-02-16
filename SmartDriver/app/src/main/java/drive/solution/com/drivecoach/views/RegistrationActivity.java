package drive.solution.com.drivecoach.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import drive.solution.com.drivecoach.R;
import drive.solution.com.drivecoach.http.TransactionProcessor;
import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.transactions.ApiService;
import drive.solution.com.drivecoach.transactions.JSONFactory;
import drive.solution.com.drivecoach.transactions.RegisterTransaction;
import drive.solution.com.drivecoach.utils.Utils;


public class RegistrationActivity extends AppCompatActivity implements WebRequest, MainPresenter {

    EditText editTextVin;

    Button btnOK;

    Button btnCancel;

    public static final String TAG_VID = "vid";

    private static final String TAG = "DC1";

    Handler mHandler;

    Timer fitInsertTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editTextVin = (EditText) findViewById(R.id.edit_vin);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(clickEventListenr);
        btnCancel.setOnClickListener(clickEventListenr);

        mHandler = new Handler();

        if (isRegistered()) {
            startLandingPage();
        } else {
            return;
        }
    }

    View.OnClickListener clickEventListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_ok:

                    if (Utils.isNetworkConnected(getApplicationContext()) || editTextVin.getText().toString().equalsIgnoreCase("1234")) {
                        if (editTextVin.getText().toString().isEmpty()) {
                            editTextVin.setError("Please enter VIN to register");
                            return;
                        }

                        doRegisterVIN(editTextVin.getText().toString());

                    } else {
                        Utils.showAlertDialog(RegistrationActivity.this).show();
                    }
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
            }
        }
    };

    //Registers vin with cloud
    private void doRegisterVIN(String vin) {

        final ProgressDialog dialog = Utils.getProgressDialog(RegistrationActivity.this, "Registering VIN.");
        dialog.show();

        String IMEINumber = Utils.getIMEINumber(RegistrationActivity.this);
        String timeStamp = Utils.getCurrentDate();

        //Stores vin to prefrences
        Utils.storeToPrefrences(getApplicationContext(), "vin", vin);

//        startLandingPage();

        //perform registration
        doRegisterVehicle(getApplicationContext(), vin, IMEINumber, timeStamp, new RegistrationActivity(), dialog);

    }

    private void startLandingPage() {
        finish();
        Intent intent = new Intent(this, LandingScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataArrived(String data) {

    }

    public boolean isRegistered() {
        if (Utils.getFromPrefrences(getApplicationContext(), "regNo").toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void startLoadingPage() {
        finish();
        Intent intent = new Intent(RegistrationActivity.this, LandingScreenActivity.class);
        startActivity(intent);

    }

    //Registers vehicle identification number
    public void doRegisterVehicle(final Context context, String vin, String IMEINumber, String timeStamp, final RegistrationActivity registrationActivity, final ProgressDialog dialog) {

        Log.i("DC1", "Vehicle registration begins.");
        JSONFactory jsonFactory = new JSONFactory();
        final JSONObject jsonObject = jsonFactory.getRegisterParams(vin, IMEINumber, timeStamp);

        RegisterTransaction registerTransaction = new RegisterTransaction(context, jsonObject);
        TransactionProcessor processor = new TransactionProcessor(context, new WebRequest() {
            @Override
            public void onDataArrived(String data) {

                String regNum = "";
                String journeyId = "";

                try {
                    JSONObject jsonData = new JSONObject(data);

                    String id = jsonData.getString("id");
                    if (id.equalsIgnoreCase("null")) {
                        dialog.cancel();
                        Log.i(TAG, "Invalid VIN." + data);
                        Utils.showToast(context, "Enter valid VIN to register.");
                        return;
                    } else {
                        regNum = jsonData.getString("regno");
                        journeyId = jsonData.getString("journeyid");
                        Utils.storeToPrefrences(getApplicationContext(), "journeyid", journeyId);
                        dialog.cancel();
                        Log.i(TAG, "Vehicle Registered Successfully.");
                        Utils.showToast(context, "Vehicle Registered Successfully.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Store register number and journey id
                Utils.storeToPrefrences(context, "regNo", regNum);
                Utils.storeToPrefrences(context, "journeyId", journeyId);

                Log.i(TAG, "Details saved in prefrences.");

                //closes progress dialog
                dialog.cancel();

                //start landing screen activity
                startLoadingPage();

                //start journey
//                doStartJourney(context);
            }
        });
        processor.execute(registerTransaction);
    }


}
