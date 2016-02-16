package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.json.JSONObject;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.BasicHeader;
import drive.solution.com.drivecoach.http.HeaderGroup;

/**
 * Created by M1032185 on 12/2/2015.
 */
public class GetUserProfile extends PostTransaction {

    JSONObject jsonObject;

    private static final String USER_PROFILE_INFO = "https://www.googleapis.com/oauth2/v1/userinfo?";

    public GetUserProfile(Context context, JSONObject jsonObject) {
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
        return "alt=json";
    }

    @Override
    protected String getUri() {
        return USER_PROFILE_INFO;
    }

    @Override
    protected void setupHeaders(HeaderGroup headers) {
        super.setupHeaders(headers);

        headers.updateBasicHeader(new BasicHeader("", ""));
    }
}

