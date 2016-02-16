package drive.solution.com.drivecoach.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.transactions.Transaction;

/**
 * Created by M1032185 on 11/27/2015.
 */
public class TransactionProcessor extends AsyncTask<Transaction, Void, Boolean> {

    private final String TAG = TransactionProcessor.class.getSimpleName();
    Context mContext;
    private Transaction mT;

    WebRequest iWebRequest;

    public TransactionProcessor(Context context, WebRequest webRequest) {
        mContext = context;
        iWebRequest = webRequest;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Transaction... params) {

        mT = params[0];
        boolean success = false;

        // Execute transaction
        try {
            Log.d("Transaction", "Transaction begins");
            success = mT.execute();
        } catch (Exception e) {
            final String tName = mT.getClass().getSimpleName();
            Log.d(TAG, "Error while executing transaction %s for op %s id %d");
            success = false;
        }

        // If successful, parse results still in BG thread
        if (success) {
            try {
                transactionSuccessful();
                Log.d("Transaction", "Transaction Successfull");
            } catch (Exception e) {
                //Some transaction parsers does not handle invalid data
                success = false;
            }
        }

        return success;
    }

    private void transactionSuccessful() {
        Log.d("Transaction", "Transaction data received" + mT.getResponseBody());
//        iWebRequest.onDataArrived(mT.getResponseBody());
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        if (status) {
            iWebRequest.onDataArrived(mT.getResponseBody());
        }
    }
}
