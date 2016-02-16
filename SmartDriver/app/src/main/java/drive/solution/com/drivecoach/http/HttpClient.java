package drive.solution.com.drivecoach.http;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import drive.solution.com.drivecoach.http.interfaces.WebRequest;
import drive.solution.com.drivecoach.utils.Utils;

/**
 * Created by M1032185 on 11/18/2015.
 */
public class HttpClient extends AsyncTask<String, Void, String> {

    JSONObject json;
    RestMethod mRestMethod;
    WebRequest iWebRequest;

    public HttpClient(JSONObject jsonObj) {
        json = jsonObj;
        mRestMethod = RestMethod.getInstance();
//        iWebRequest = webRequest;
    }

    @Override
    protected String doInBackground(String... params) {

        URL url = null;
        HttpURLConnection conn = null;
        HttpResponse response = null;
        try {
            url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(json.toString().getBytes("UTF-8"));
            out.close();

            response = new HttpResponse();
            response.setMethod("POST");
//            response.setUri(params[0]);
            response.setStatusCode(conn.getResponseCode());
            response.setResponseBody(Utils.streamlineHttpResponse(conn));
            response.setStatusLine(conn.getResponseMessage());
            response.setPostRequestBody(json.toString());

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        HttpResponse response = new HttpResponse();
//        try {
//            response = mRestMethod.sendPostRequest(params[0], json.toString());
//
//            Log.d("HttpClient", response.getStatusLine() + "" + response);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return response.getResponseBody();
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
//        iWebRequest.onDataArrived(data);
    }
}
