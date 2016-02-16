package drive.solution.com.drivecoach.transactions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executor;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.HeaderGroup;
import drive.solution.com.drivecoach.http.HttpResponse;
import drive.solution.com.drivecoach.http.RestMethod;

/**
 * Created by M1032185 on 11/26/2015.
 */
public abstract class Transaction {

    Context mContext;
    HeaderGroup mHeaders;
    RestMethod mRestMethod;

    HttpResponse mResponse;
    String mResponseString;
    URI mUri;
    boolean mExecuted = false;

    private static final String TAG = "DC1";

    public Transaction(Context mcontext) {
        mRestMethod = RestMethod.getInstance();
        mContext = mcontext;
        mHeaders = new HeaderGroup();
        mResponse = new HttpResponse();
    }

    public void initializeExecution() throws IllegalStateException, Exception {

        // Initialize uri and headers
        setupRequestUri();
        mRestMethod = RestMethod.getInstance();
        mHeaders = new HeaderGroup();
        mResponse = new HttpResponse();

    }

    /**
     * Returns the response body.
     *
     * @return the full response contents in form of a string.
     * @throws IllegalStateException if this method is called before this transaction has terminated,
     *                               or if it terminated with an error.
     */
    public String getResponseBody() throws IllegalStateException {

        // First try to see if the string is already cached
        if (mResponse != null && mResponse.getResponseBody() != null) {
            mResponseString = mResponse.getResponseBody();
            return mResponse.getResponseBody();
        }

        return null;
    }

    public boolean execute() {

        try {
            Log.d(TAG, "Init Execution");
            initializeExecution();
            mResponse = sendRequest();
            Log.d(TAG, "Got Response");

            mResponseString = mResponse.getResponseBody();
            if (BuildConfig.LOGS)
                Log.d(TAG, "RESPONSE IS : " + mResponseString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        final int statusCode = mResponse.getStatusCode();
        Log.d(TAG, "Status Code:" + statusCode);
        if (isSuccesfulStatusCode(statusCode)) {
            return true;
        } else {
            handleDefaultErrors(statusCode);
            return false;
        }
    }

    protected abstract HttpResponse sendRequest() throws IOException;

    protected boolean isSuccesfulStatusCode(final int statusCode) {
        return statusCode == 200;
    }

    void handleDefaultErrors(int statusCode) {
        switch (statusCode) {
            case 404:
                break;
            case 500:
                break;

        }
    }

    protected abstract void setupRequestUri();

    public Executor getExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }
}
