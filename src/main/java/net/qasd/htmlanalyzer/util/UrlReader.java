package net.qasd.htmlanalyzer.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;

public class UrlReader {

    private static final int MAX_FOLLOW = 10;

    // used for unit testing
    private URLStreamHandler urlStreamHandler = null;

    /**
     * Initiates a new instance of UrlReader
     *
     * @return A new instance of UrlReader
     */
    public static UrlReader newInstance() {
        return new UrlReader();
    }

    /**
     * Gets the content of the given url
     * <p>
     * The method is converted from static for easy unit testing
     *
     * @param url The url
     * @return Html content
     * @throws IOException              If any io action fails
     * @throws UrlNotFoundException     If the http response code is NOT_FOUND
     * @throws NotHtmlDocumentException If the content type is different from text/html
     * @throws UrlNotReadableException  If the maximum follow limit exceeds or the http response status is different from OK and NOT_FOUND
     */
    public String getContent(URL url) throws IOException, UrlNotFoundException, NotHtmlDocumentException, UrlNotReadableException {
        return getContent(url, 0);
    }

    /**
     * Gets the content of the given url
     * <p>
     * The method is converted from static for easy unit testing
     *
     * @param url   The url
     * @param index The follow redirect index
     * @return Html content
     * @throws IOException              If any io action fails
     * @throws UrlNotFoundException     If the http response code is NOT_FOUND
     * @throws NotHtmlDocumentException If the content type is different from text/html
     * @throws UrlNotReadableException  If the maximum follow limit exceeds or the http response status is different from OK and NOT_FOUND
     */
    public String getContent(URL url, int index) throws IOException, UrlNotFoundException, NotHtmlDocumentException, UrlNotReadableException {
        // only allow MAX_FOLLOW redirects
        if (index > MAX_FOLLOW) {
            throw new UrlNotReadableException();
        }

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        // set http connection settings
        httpConnection.setRequestMethod("GET");
        httpConnection.setInstanceFollowRedirects(true);
        httpConnection.setReadTimeout(10 * 1000);
        httpConnection.setRequestProperty("User-Agent", "Mozilla");

        int responseCode = httpConnection.getResponseCode();

        // check redirect
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
            responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
            responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
            String location = httpConnection.getHeaderField("Location");

            // increase the follow redirect index
            index++;

            // build the follow url
            URL followUrl;
            if (urlStreamHandler == null) {
                followUrl = new URL(location);
            } else {
                followUrl = new URL(null, location, urlStreamHandler);
            }

            return getContent(followUrl, index);
        }

        // check whether the http status is OK
        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // check the content type
            String contentType = httpConnection.getHeaderField("Content-Type");
            if (contentType != null && contentType.toLowerCase().contains("text/html")) {
                // read the content
                return IOUtils.toString(httpConnection.getInputStream(), Charset.forName("UTF-8"));
            } else {
                throw new NotHtmlDocumentException();
            }
        } else if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new UrlNotFoundException();
        } else {
            throw new UrlNotReadableException();
        }
    }

    /**
     * Sets the url stream handler
     * <p>
     * Used for unit testing in order to mock the URL
     *
     * @param urlStreamHandler The url stream handler
     */
    public void setUrlStreamHandler(URLStreamHandler urlStreamHandler) {
        this.urlStreamHandler = urlStreamHandler;
    }

    /**
     * Exceptions
     */
    public static class UrlNotFoundException extends Exception {
    }

    public static class NotHtmlDocumentException extends Exception {
    }

    public static class UrlNotReadableException extends Exception {
    }
}
