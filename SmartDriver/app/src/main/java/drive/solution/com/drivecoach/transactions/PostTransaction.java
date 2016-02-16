package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import drive.solution.com.drivecoach.http.HttpResponse;

/**
 * Created by M1032185 on 11/26/2015.
 */
public abstract class PostTransaction extends SetupTransaction {

    protected JSONObject mRequestBody;

    public PostTransaction(Context mcontext) {
        super(mcontext);
        mRequestBody = new JSONObject();
    }

    @Override
    public void initializeExecution() throws Exception {
        super.initializeExecution();

        mRequestBody = setupRequestBody();
    }

    protected abstract JSONObject setupRequestBody();

    @Override
    protected HttpResponse sendRequest() throws IOException {
        return mRestMethod.sendPostRequest(mUri, getRequestBody());
    }

    @Override
    protected String getRequestBody() {
        if (mRequestBody != null) {
            return mRequestBody.toString();
        }

        return null;
    }
}
