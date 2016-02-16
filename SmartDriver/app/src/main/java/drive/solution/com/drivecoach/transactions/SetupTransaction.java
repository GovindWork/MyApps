package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.apache.http.message.BasicHeader;

import java.net.URI;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.HeaderGroup;

import drive.solution.com.drivecoach.http.RestMethod;

/**
 * Created by M1032185 on 11/26/2015.
 */
public abstract class SetupTransaction extends Transaction {

    URI mUri;

    HeaderGroup mHeaders;

    public SetupTransaction(Context mcontext) {

        super(mcontext);
    }

//    public SetupTransaction() {
//        mRestMethod = RestMethod.getInstance();
//        mHeaders = new HeaderGroup();
//    }

    protected void setupHeaders(HeaderGroup headers) {

    }

    @Override
    protected void setupRequestUri() {
        // Construct base URL
        mUri = URI.create(getUri() + getUrlPrefix());
    }

    protected String getUri() {
        return BuildConfig.BASE_URL;
    }

    protected String getUrlPrefix() {
        return "";
    }

    protected abstract String getPath();

    /**
     * Can be overridden by subclasses to provide an explicit String.
     */
    protected String getRequestBody() {
        return null;
    }

}
