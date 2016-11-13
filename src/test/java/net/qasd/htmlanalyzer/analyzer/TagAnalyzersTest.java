package net.qasd.htmlanalyzer.analyzer;

import net.qasd.htmlanalyzer.util.MutableInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TagAnalyzersTest {

    private static String INTERNAL_DOMAIN = "www.abc.com";
    private static String EXTERNAL_DOMAIN = "www.xyz.com";

    private Document document;

    @Before
    public void setUp() {
        document = Jsoup.parse("<html><body>" +
            // headings
            "<H1>test-h1</H1><h1>test-h1</h1>" +
            "<h2>test-h2</h2><h2>test-h2</h2>" +
            "<h3>test-h3</h3>" +
            "<H6>test-h6</H6><h6>test-h6</h6>" +

            //internal hypermedia links
            "<a href=\"./index.html\">link</a>" +
            "<a href=\"../index.html\">link</a>" +
            "<link href=\"/css/index.css\"/>" +
            "<a href=\"index.html\">link</a>" +
            "<a href=\"http://" + INTERNAL_DOMAIN + "\">link</a>" +
            "<a href=\"http://" + INTERNAL_DOMAIN + "/\">link</a>" +
            "<a href=\"http://" + INTERNAL_DOMAIN + "/index.html\">link</a>" +
            "<a href=\"https://" + INTERNAL_DOMAIN + "\">link</a>" +
            "<link href=\"https://" + INTERNAL_DOMAIN + "/css/index.css\"/>" +
            "<a href=\"https://" + INTERNAL_DOMAIN + "/index.html\">link</a>" +
            "<a href=\"//" + INTERNAL_DOMAIN + "\">link</a>" +
            "<a href=\"//" + INTERNAL_DOMAIN + "/\">link</a>" +
            "<link href=\"//" + INTERNAL_DOMAIN + "/css/index.css\"/>" +
            "<script src=\"/js/index.js\"/>" +

            //external hypermedia links
            "<a href=\"http://" + EXTERNAL_DOMAIN + "\">link</a>" +
            "<a href=\"http://" + EXTERNAL_DOMAIN + "/\">link</a>" +
            "<a href=\"http://" + EXTERNAL_DOMAIN + "/index.html\">link</a>" +
            "<a href=\"https://" + EXTERNAL_DOMAIN + "\">link</a>" +
            "<a href=\"https://" + EXTERNAL_DOMAIN + "/\">link</a>" +
            "<a href=\"https://" + EXTERNAL_DOMAIN + "/index.html\">link</a>" +
            "<a href=\"//" + EXTERNAL_DOMAIN + "\">link</a>" +
            "<a href=\"//" + EXTERNAL_DOMAIN + "/\">link</a>" +
            "<a href=\"//" + EXTERNAL_DOMAIN + "/index.html\">link</a>" +
            "<script src=\"//" + EXTERNAL_DOMAIN + "/js/index.js\"/>" +
            "<link href=\"https://" + EXTERNAL_DOMAIN + "/css/index.css\"/>" +

            "</body</html1>");

    }

    @Test
    public void testAnalyzeHeadingLevels() {
        Map<String, MutableInteger> result = TagAnalyzers.sumUpHeadingLevels(document);

        assertEquals(2, result.get("h1").getValue());
        assertEquals(2, result.get("h2").getValue());
        assertEquals(1, result.get("h3").getValue());
        assertFalse(result.containsKey("h4"));
        assertFalse(result.containsKey("h5"));
        assertEquals(2, result.get("h6").getValue());
    }

    @Test
    public void testAnalyzeHypermediaLinks() {
        TagAnalyzers.HyperMediaLinkSums result = TagAnalyzers.sumUpHypermediaLinks(document, INTERNAL_DOMAIN);

        assertEquals(14, result.getInternal().getValue());
        assertEquals(11, result.getExternal().getValue());
    }
}