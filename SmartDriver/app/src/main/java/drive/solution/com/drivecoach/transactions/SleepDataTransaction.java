package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.json.JSONObject;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.BasicHeader;
import drive.solution.com.drivecoach.http.HeaderGroup;

/**
 * Created by M1032185 on 12/17/2015.
 */
public class SleepDataTransaction extends PostTransaction {

    private String regNum;

    JSONObject jsonObject;

    public SleepDataTransaction(Context context, JSONObject jsonObject) {
        super(context);
        this.jsonObject = jsonObject;
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
        return "setsleepdata";
    }

    @Override
    protected String getUri() {
        return BuildConfig.BASE_URL;
    }

    @Override
    protected void setupHeaders(HeaderGroup headers) {
        super.setupHeaders(headers);

        headers.updateBasicHeader(new BasicHeader("", ""));
    }
}

