package net.qasd.htmlanalyzer.util;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlReaderTest {

    private static final String SAMPLE_RESPONSE = "test test";
    private HttpURLConnection mockHttpConnection;
    private URLStreamHandler urlStreamHandler;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        mockHttpConnection = mock(HttpURLConnection.class);
        when(mockHttpConnection.getInputStream()).thenReturn(IOUtils.toInputStream(SAMPLE_RESPONSE, Charset.forName("UTF-8")));

        urlStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                return mockHttpConnection;
            }
        };
    }

    @Test
    public void testGetContent() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        String actual = UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler));
        assertEquals(SAMPLE_RESPONSE, actual);
    }

    @Test
    public void testGetContentNotFound() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        thrown.expect(UrlReader.UrlNotFoundException.class);

        UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler));
    }

    @Test
    public void testGetContentNotHtml() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("image/jpg");

        thrown.expect(UrlReader.NotHtmlDocumentException.class);

        UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler));
    }

    @Test
    public void testGetContentNoContentType() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn(null);

        thrown.expect(UrlReader.NotHtmlDocumentException.class);

        UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler));
    }

    @Test
    public void testGetContentNotReadable() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        thrown.expect(UrlReader.UrlNotReadableException.class);

        UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler));
    }

    @Test
    public void testGetContentFollowRedirect301() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_MOVED_PERM);
        when(mockHttpConnection.getHeaderField("Location")).thenReturn("http://loripsum.net");
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        assertTrue(UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler)).contains("Lorem Ipsum"));
    }

    @Test
    public void testGetContentFollowRedirect302() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_MOVED_TEMP);
        when(mockHttpConnection.getHeaderField("Location")).thenReturn("http://loripsum.net");
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        assertTrue(UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler)).contains("Lorem Ipsum"));
    }

    @Test
    public void testGetContentFollowRedirect303() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_SEE_OTHER);
        when(mockHttpConnection.getHeaderField("Location")).thenReturn("http://loripsum.net");
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        assertTrue(UrlReader.newInstance().getContent(new URL(null, "http://www.google.com", urlStreamHandler)).contains("Lorem Ipsum"));
    }

    @Test
    public void testGetContentMaxFollowRedirect() throws IOException, UrlReader.UrlNotFoundException, UrlReader.NotHtmlDocumentException, UrlReader.UrlNotReadableException {
        when(mockHttpConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_MOVED_PERM);
        when(mockHttpConnection.getHeaderField("Location")).thenReturn("http://loripsum.net");
        when(mockHttpConnection.getHeaderField("Content-Type")).thenReturn("text/html");

        UrlReader urlReader = UrlReader.newInstance();
        urlReader.setUrlStreamHandler(urlStreamHandler);

        thrown.expect(UrlReader.UrlNotReadableException.class);

        urlReader.getContent(new URL(null, "http://www.google.com", urlStreamHandler));
    }
}
