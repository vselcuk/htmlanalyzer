package net.qasd.htmlanalyzer.web.controller;

import net.qasd.htmlanalyzer.HtmlAnalyzer;
import net.qasd.htmlanalyzer.HtmlAnalyzerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IndexController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    public IndexController() {
    }

    /**
     * Serves the url input form
     *
     * @param req Request
     * @param res Response
     * @return View and view model variables
     */
    public static ModelAndView serveHomePage(Request req, Response res) {
        logger.info("Received a home page request from {}", req.ip() == null ? "n/a" : req.ip());

        return new ModelAndView(new HashMap<String, Object>(), "templates/home.vm");
    }

    /**
     * Serves the html analyzer process, called by ajax
     *
     * @param req Request
     * @param res Response
     * @return View and view model variables containing the result and error message
     */
    public static ModelAndView executeHtmlAnalyzer(Request req, Response res) {
        logger.info("Received a html analyzer request from {}", req.ip() == null ? "n/a" : req.ip());

        Map<String, Object> model = new HashMap<>();
        HtmlAnalyzerResult result;

        // get the url from request parameters
        String url = req.queryParams("url");
        if (url == null || url.trim().isEmpty()) {
            logger.warn("The url parameter is not set");
            model.put("errorMessage", Messages.URL_PARAMETER_NOT_FOUND);
        } else {
            try {
                logger.info("The url parameter is \"{}\"", url);

                result = HtmlAnalyzer.execute(new URL(url));
                if (result.isSucceed()) {
                    logger.info("Analyzing of the page with the url \"{}\" is succeed", url);
                    model.put("analyzeResult", result);
                } else {
                    logger.info("Analyzing of the page with the url \"{}\" is failed", url);
                    model.put("errorMessage", result.getMessage());
                }
            } catch (MalformedURLException urlException) {
                model.put("errorMessage", Messages.URL_NOT_VALID);
                logger.info("The url \"{}\" is not valid", url, urlException);
            }
        }

        return new ModelAndView(model, "templates/result.vm");
    }

    /**
     * Messages
     */
    public static class Messages {
        public static final String URL_PARAMETER_NOT_FOUND = "Please enter the url";
        public static final String URL_NOT_VALID = "The entered url is not valid. Please try again";
    }
}
