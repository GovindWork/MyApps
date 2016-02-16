package drive.solution.com.drivecoach.http;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import drive.solution.com.drivecoach.BuildConfig;
import drive.solution.com.drivecoach.http.interfaces.IRestMethod;
import drive.solution.com.drivecoach.utils.Utils;


/**
 * Created by M1032185 on 11/18/2015.
 */
public class RestMethod implements IRestMethod {

    private static final int TIMEOUT = 15 * 1000;

    private static RestMethod ourInstance = new RestMethod();

    public static final String TAG = "DC1";

    private RestMethod() {
    }

    private void addHeadersToRequest(HeaderGroup headers, HttpURLConnection get) {
        if (headers != null) {
            for (BasicHeader header : headers.getAllBasicHeaders()) {
                get.setRequestProperty(header.getName(), header.getValue());
            }
        }
    }

    public static RestMethod getInstance() {
        return ourInstance;
    }
   /* @Override
    public HttpResponse sendGetRequest(String uri) throws IOException {
        URL url = new URL(uri);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        HttpResponse response = new HttpResponse();
        response.setMethod("GET");
        response.setUri(uri);
        response.setStatusCode(conn.getResponseCode());
//        response.setResponseBody(Utils.streamlineHttpResponse(conn));
        response.setStatusLine(conn.getResponseMessage());
        response.setPostRequestBody("");

        conn.disconnect();

        return response;
    }*/

    @Override
    public HttpResponse sendPostRequest(URI uri, String body) throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("content-type", "application/json");

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(body.getBytes("UTF-8"));
        out.close();

        if (BuildConfig.LOGS) {
            Log.i(TAG, "URL : " + uri);
            Log.i(TAG, "PARAMS" + body);
        }

        HttpResponse response = new HttpResponse();
        response.setMethod("POST");
        response.setUri(uri);
        response.setStatusCode(conn.getResponseCode());
        response.setResponseBody(Utils.streamlineHttpResponse(conn));
        response.setStatusLine(conn.getResponseMessage());
        response.setPostRequestBody(body);

        conn.disconnect();

        return response;
    }

    private void addTimeout(HttpURLConnection get) {
        get.setConnectTimeout(TIMEOUT);
        get.setReadTimeout(TIMEOUT);
    }

    @Override
    public HttpResponse sendGetRequest(URI uri) throws IOException {

        URL url = new URL(uri.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setChunkedStreamingMode(0);
        conn.setRequestMethod("GET");
        conn.connect();

        if (BuildConfig.LOGS)
            Log.i(TAG, "URL : " + uri);

        HttpResponse response = new HttpResponse();
        response.setMethod("GET");
        response.setUri(uri);
        response.setStatusCode(conn.getResponseCode());
        response.setResponseBody(Utils.streamlineHttpResponse(conn));
        response.setStatusLine(conn.getResponseMessage());
        response.setPostRequestBody("");

        conn.disconnect();

        return response;

    }

   /* @Override
    public HttpResponse sendPostRequest(URI uri, HeaderGroup headers, String body) throws IOException {
        URL url = new URL(uri.toString());
        HttpsURLConnection conn = null;
        conn = (HttpsURLConnection) url.openConnection();

        addHeadersToRequest(headers, conn);
        addTimeout(conn);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(body.getBytes("UTF-8"));
        out.close();

        HttpResponse response = new HttpResponse();
        response.setMethod("POST");
        response.setUri(uri);
        response.setStatusCode(conn.getResponseCode());
        response.setResponseBody(Utils.streamlineHttpResponse(conn));
        response.setRequestHeaders(headers);
        response.setStatusLine(conn.getResponseMessage());
        response.setPostRequestBody(body);

        conn.disconnect();

        return response;
    }*/
}
