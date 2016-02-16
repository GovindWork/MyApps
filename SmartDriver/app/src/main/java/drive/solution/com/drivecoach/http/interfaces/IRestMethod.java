package drive.solution.com.drivecoach.http.interfaces;

import java.io.IOException;
import java.net.URI;

import drive.solution.com.drivecoach.http.HeaderGroup;
import drive.solution.com.drivecoach.http.HttpResponse;

/**
 * Created by M1032185 on 11/18/2015.
 */
public interface IRestMethod {

    HttpResponse sendPostRequest(URI uri, String body) throws IOException;

    HttpResponse sendGetRequest(URI uri) throws IOException;

}

