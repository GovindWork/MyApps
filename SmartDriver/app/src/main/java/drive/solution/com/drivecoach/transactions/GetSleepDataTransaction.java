package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.json.JSONObject;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.BasicHeader;
import drive.solution.com.drivecoach.http.HeaderGroup;

/**
 * Created by M1032185 on 12/18/2015.
 */
public class GetSleepDataTransaction extends GetTransaction {

    private String regNum;

    JSONObject jsonObject;

    private String userEmailid;

    private String sleepStartTime;

    private String sleepEndTime;

    private String userDeviceId;

    private static final String SLEEP_URL = "http://digitalpumpkin.cloudapp.net/VolvoServices/jersey/sony/sleep";

    public GetSleepDataTransaction(Context context, String emailId, String startTime, String endTime, String deviceId) {
        super(context);
        userEmailid = emailId;
        sleepStartTime = startTime;
        sleepEndTime = endTime;
        userDeviceId = deviceId;
    }

    @Override
    protected JSONObject setupRequestBody() {
        return jsonObject;
    }

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    protected String getUrlPrefix() {
        return "";
    }

    @Override
    protected String getUri() {
        return BuildConfig.SLEEP_DATA_URL + getUrlEncoded();
    }

    protected String getUrlEncoded() {
        return "?emailId=" + userEmailid + "&startTime=" + sleepStartTime + "&endTime=" + sleepEndTime + "&deviceId=" + userDeviceId;
    }

    @Override
    protected void setupHeaders(HeaderGroup headers) {
        super.setupHeaders(headers);

        headers.updateBasicHeader(new BasicHeader("", ""));
    }
}


