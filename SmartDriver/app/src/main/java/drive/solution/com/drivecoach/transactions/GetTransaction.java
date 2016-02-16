package drive.solution.com.drivecoach.transactions;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import drive.solution.com.drivecoach.http.HttpResponse;

/**
 * Created by M1032185 on 12/18/2015.
 */
public abstract class GetTransaction extends SetupTransaction {

    protected JSONObject mRequestBody;

    public GetTransaction(Context mcontext) {
        super(mcontext);
    }

    @Override
    protected String getPath() {
        return null;
    }

    @Override
    public void initializeExecution() throws Exception {
        super.initializeExecution();

        mRequestBody = setupRequestBody();
    }

    protected abstract JSONObject setupRequestBody();

    @Override
    protected HttpResponse sendRequest() throws IOException {
        return mRestMethod.sendGetRequest(mUri);
    }

    @Override
    protected String getRequestBody() {
        if (mRequestBody != null) {
            return mRequestBody.toString();
        }

        return null;
    }
}

