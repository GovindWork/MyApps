package drive.solution.com.drivecoach.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by M1032185 on 11/26/2015.
 */
public class HeaderGroup {
    /** The list of headers for this group, in the order in which they were added */
    private final List<BasicHeader> headers;

    /**
     * Constructor for BasicHeaderGroup.
     */
    public HeaderGroup() {
        this.headers = new ArrayList<BasicHeader>(16);
    }

    /**
     * Removes any contained headers.
     */
    public void clear() {
        headers.clear();
    }

    /**
     * Adds the given header to the group.  The order in which this header was
     * added is preserved.
     *
     * @param header the header to add
     */
    public void addHeader(final BasicHeader header) {
        if (header == null) {
            return;
        }
        headers.add(header);
    }

    /**
     * Removes the given header.
     *
     * @param header the header to remove
     */
    public void removeBasicHeader(final BasicHeader header) {
        if (header == null) {
            return;
        }
        headers.remove(header);
    }

    /**
     * Replaces the first occurence of the header with the same name. If no header with
     * the same name is found the given header is added to the end of the list.
     *
     * @param header the new header that should replace the first header with the same
     * name if present in the list.
     */
    public void updateBasicHeader(final BasicHeader header) {
        if (header == null) {
            return;
        }
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        //     for (BasicHeader header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.headers.size(); i++) {
            final BasicHeader current = this.headers.get(i);
            if (current.getName().equalsIgnoreCase(header.getName())) {
                this.headers.set(i, header);
                return;
            }
        }
        this.headers.add(header);
    }

    /**
     * Sets all of the headers contained within this group overriding any
     * existing headers. The headers are added in the order in which they appear
     * in the array.
     *
     * @param headers the headers to set
     */
    public void setBasicHeaders(final BasicHeader[] headers) {
        clear();
        if (headers == null) {
            return;
        }
        Collections.addAll(this.headers, headers);
    }


    /**
     * Gets all of the headers with the given name.  The returned array
     * maintains the relative order in which the headers were added.
     *
     * <p>BasicHeader name comparison is case insensitive.
     *
     * @param name the name of the header(s) to get
     *
     * @return an array of length &ge; 0
     */
    public BasicHeader[] getBasicHeaders(final String name) {
        final List<BasicHeader> headersFound = new ArrayList<>();
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        //     for (BasicHeader header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.headers.size(); i++) {
            final BasicHeader header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }

        return headersFound.toArray(new BasicHeader[headersFound.size()]);
    }

    /**
     * Gets the first header with the given name.
     *
     * <p>BasicHeader name comparison is case insensitive.
     *
     * @param name the name of the header to get
     * @return the first header or {@code null}
     */
    public BasicHeader getFirstBasicHeader(final String name) {
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        //     for (BasicHeader header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.headers.size(); i++) {
            final BasicHeader header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    /**
     * Gets the last header with the given name.
     *
     * <p>BasicHeader name comparison is case insensitive.
     *
     * @param name the name of the header to get
     * @return the last header or {@code null}
     */
    public BasicHeader getLastBasicHeader(final String name) {
        // start at the end of the list and work backwards
        for (int i = headers.size() - 1; i >= 0; i--) {
            final BasicHeader header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }

        return null;
    }

    /**
     * Gets all of the headers contained within this group.
     *
     * @return an array of length &ge; 0
     */
    public BasicHeader[] getAllBasicHeaders() {
        return headers.toArray(new BasicHeader[headers.size()]);
    }

    /**
     * Tests if headers with the given name are contained within this group.
     *
     * <p>BasicHeader name comparison is case insensitive.
     *
     * @param name the header name to test for
     * @return {@code true} if at least one header with the name is
     * contained, {@code false} otherwise
     */
    public boolean containsBasicHeader(final String name) {
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        //     for (BasicHeader header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.headers.size(); i++) {
            final BasicHeader header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
