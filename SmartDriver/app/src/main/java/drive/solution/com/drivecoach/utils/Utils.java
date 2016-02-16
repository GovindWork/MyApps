package drive.solution.com.drivecoach.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Math.cos;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.sin;

/**
 * Created by M1032185 on 11/19/2015.
 */
public class Utils {

    public static final String URL_LOCATION_UPDATE = "http://10.0.2.2:8080/mbtp/web/rest/locationDetailsService/saveLocationDetails";

    public static final String URL_VIN_REGISTRATION = "";

    public static final String BASE_URL = "http://digitalpumpkin.cloudapp.net/VolvoWearablesApp/setdata/";

    public static final String AUTH_PENDING = "auth_state_pending";

    public static final String DB_NAME = "drivercoach";
    public static final String DB_TABLE_USER_PROFILE = "userprofile";
    public static final String DB_TABLE_USER_DETAIL = "userdetails";

    public static final String USER_EMAIL = "smartband2sleep@gmail.com";

    public static String KEY_ID = "id";
    public static String MAC_ID = "macid";
    public static String END_TIME = "endtime";
    public static String JOURNEY_ID = "journeyid";
    public static String SESSION_ID = "sessionid";
    public static String USER_NAME = "username";
    public static String EMAIL_ID = "emailid";
    public static String HEIGHT = "height";
    public static String WEIGHT = "weight";
    public static String ACCESS_TOKEN = "acceesstoken";

    private static final String PREFRENCES_NAME = "app_prefrences";
    private static final String PREFRENCES_LOGIN = "app_login";


    DataSource heartRateDs, locationDs, locationDs2;

    DataSet dataSet = null;
    DataSet dataSetLocation = null;
    DataSet dataSetLoc = null;
    private Context mContext;

    public Utils(Context context) {
        mContext = context;
    }


    //Checking for network connectivity
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //Parsing web service response and creating it in json format.
    public static String streamlineHttpResponse(HttpURLConnection con) throws IOException {
        if (con == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int responseCode = con.getResponseCode();
        InputStream content = responseCode >= 200 && responseCode <= 299 ? con.getInputStream() : con.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        content.close();
        reader.close();
        return builder.toString();
    }

    //Gets Device IMEI Number
    public static String getIMEINumber(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    //Gets Timestamp
    public static long getTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    //Stores user login status
    public static void storeLoginStatus(Context context,boolean status){
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCES_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstlogin", status);
        editor.commit();
    }

    public static boolean getLoginStatus(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCES_LOGIN, Context.MODE_PRIVATE);
        return preferences.getBoolean("firstlogin", true);
    }

    //Storing VIN to shared preferences for future usage
    public static void storeToPrefrences(Context context, String type, String vid) {

        SharedPreferences preferences = context.getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(type, vid);
        editor.commit();
    }

    //Gets stored data from prefrences
    public static String getFromPrefrences(Context context, String type) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(type, "");
    }

    //Creating alert dialog for generic usage
    public static Dialog showAlertDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No network connection");
        builder.setMessage("Can't connect to the network.Check your data connection and try again.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;

    }

    public static ProgressDialog getProgressDialog(Context context, String message) {

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        return dialog;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getCurrentDate() {

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());

    }

    private static final double LOCAL_PI = 3.1415926535897932385;

    private static double ToRadians(double degrees) {
        double radians = degrees * LOCAL_PI / 180;
        return radians;
    }

    public static double directDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = ToRadians(lat2 - lat1);
        double dLng = ToRadians(lng2 - lng1);
        double a = sin(dLat / 2) * sin(dLat / 2) +
                cos(ToRadians(lat1)) * cos(ToRadians(lat2)) *
                        sin(dLng / 2) * sin(dLng / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;
        return dist * meterConversion;
    }
}
