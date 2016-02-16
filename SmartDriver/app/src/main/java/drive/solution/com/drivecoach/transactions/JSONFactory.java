package drive.solution.com.drivecoach.transactions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import drive.solution.com.drivecoach.utils.Utils;

/**
 * Created by M1032185 on 11/27/2015.
 */
public class JSONFactory {

    JSONObject jsonObject;

    public JSONFactory() {
        jsonObject = new JSONObject();
    }

    public JSONObject getStartJourneyParams(String regNum, long starttime) {

        try {
            jsonObject.put("vin", regNum);
            jsonObject.put("starttime", starttime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public JSONObject getStopJourneyParams(String journeyId, long endTime) {

        try {
            jsonObject.put("journeyid", journeyId);
            jsonObject.put("endtime", endTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getDriverParams(String userEmail, String userHeight, String userWeight, String journeyId, String deviceid) {

        try {
            jsonObject.put("journeyid", journeyId);
            jsonObject.put("emailid", userEmail);
            jsonObject.put("height", userHeight);
            jsonObject.put("weight", userWeight);
            jsonObject.put("deviceid", deviceid);
//            jsonObject.put("dob", userDOB);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getSleepParams(String deviceId, String email, String sleepData) {
        JSONObject jsonObject1 = null;
        try {

            jsonObject1 = new JSONObject(sleepData);
//            JSONArray jsonArray = jsonObject1.getJSONArray("sleepData");
//            jsonObject.put("deviceId", deviceId);
//            jsonObject.put("emailId", email);
//            jsonObject.put("day", Utils.getTimeStamp());
//            jsonObject.put("sleepData", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    public JSONObject getHeartRateParams(String journeyId, String sessionId, int heartRate, long startTime, long endTime) {

        try {
            jsonObject.put("journeyid", journeyId);
            jsonObject.put("sessionid", sessionId);
            jsonObject.put("heartrate", heartRate);
            jsonObject.put("starttime", startTime);
            jsonObject.put("endtime", endTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getLocationParams(String journeyId, String sessionId, String locations[], String startTime, String endTime) {

        try {
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("latitude", locations[0]);
            jsonObject.put("longitude", locations[1]);
            jsonObject.put("altitude", locations[2]);
            jsonArray.put(jsonObject);
            jsonObject.put("journeyId", journeyId);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("heartRate", startTime);
            jsonObject.put("locations", jsonArray);
            jsonObject.put("endTime", endTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getEndSessionParams(String journeyId, String sessionId, long endTime) {

        try {
            jsonObject.put("journeyid", journeyId);
            jsonObject.put("sessionid", sessionId);
            jsonObject.put("endtime", endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public JSONObject getRegisterParams(String vin, String imei, String regTime) {
        try {
            jsonObject.put("vin", vin);
            jsonObject.put("imei", imei);
            jsonObject.put("registeredtime", regTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
