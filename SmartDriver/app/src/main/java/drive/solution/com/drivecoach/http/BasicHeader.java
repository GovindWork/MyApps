package drive.solution.com.drivecoach.http;

/**
 * Created by M1032185 on 11/26/2015.
 */
public class BasicHeader {
    private final String value;
    private final String name;

    /**
     * Constructor with name and value
     *
     * @param name the header name
     * @param value the header value
     */
    public BasicHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        // no need for non-default formatting in toString()
        return (name + ":" + value).toString();
    }
}

