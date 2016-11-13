package net.qasd.htmlanalyzer.web.controller;

import net.qasd.htmlanalyzer.HtmlAnalyzer;
import org.junit.Test;
import spark.Request;
import spark.Response;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndexControllerTest {

    @Test
    public void testServeHomePage() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        Map<String, Object> model = (Map<String, Object>) IndexController.serveHomePage(request, response).getModel();
        assertTrue(model.isEmpty());
    }

    @Test
    public void testExecuteHtmlAnalyzerEmptyUrl() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        when(request.queryParams("url")).thenReturn("");
        Map<String, Object> model = (Map<String, Object>) IndexController.executeHtmlAnalyzer(request, response).getModel();
        assertTrue(model.containsKey("errorMessage"));
        assertEquals(IndexController.Messages.URL_PARAMETER_NOT_FOUND, model.get("errorMessage"));

        when(request.queryParams("url")).thenReturn(null);
        model = (Map<String, Object>) IndexController.executeHtmlAnalyzer(request, response).getModel();
        assertTrue(model.containsKey("errorMessage"));
        assertEquals(IndexController.Messages.URL_PARAMETER_NOT_FOUND, model.get("errorMessage"));
    }

    @Test
    public void testExecuteHtmlAnalyzerNotValidUrl() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        when(request.queryParams("url")).thenReturn("notvalidurl");

        Map<String, Object> model = (Map<String, Object>) IndexController.executeHtmlAnalyzer(request, response).getModel();

        assertTrue(model.containsKey("errorMessage"));
        assertEquals(IndexController.Messages.URL_NOT_VALID, model.get("errorMessage"));
    }

    @Test
    public void testExecuteHtmlAnalyzerValidUrl() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        when(request.queryParams("url")).thenReturn("https://www.google.com");

        Map<String, Object> model = (Map<String, Object>) IndexController.executeHtmlAnalyzer(request, response).getModel();

        assertTrue(model.containsKey("analyzeResult"));
    }

    @Test
    public void testExecuteHtmlAnalyzerNotFoundUrl() {
        Request request = mock(Request.class);
        Response response = mock(Response.class);

        when(request.queryParams("url")).thenReturn("https://www.google.com/asdfg");

        Map<String, Object> model = (Map<String, Object>) IndexController.executeHtmlAnalyzer(request, response).getModel();

        assertTrue(model.containsKey("errorMessage"));
        assertEquals(HtmlAnalyzer.Messages.URL_NOT_FOUND, model.get("errorMessage"));
    }
}