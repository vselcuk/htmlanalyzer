package net.qasd.htmlanalyzer;

import net.qasd.htmlanalyzer.analyzer.DocumentTypeFetcher;
import net.qasd.htmlanalyzer.analyzer.LoginDetector;
import net.qasd.htmlanalyzer.util.UrlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static net.qasd.htmlanalyzer.analyzer.TagAnalyzers.sumUpHeadingLevels;
import static net.qasd.htmlanalyzer.analyzer.TagAnalyzers.sumUpHypermediaLinks;

public class HtmlAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(HtmlAnalyzer.class);

    /**
     * Executes the html analyzer
     *
     * @param url The url
     * @return Html analyzer result
     */
    public static HtmlAnalyzerResult execute(URL url) {
        HtmlAnalyzerResult result = new HtmlAnalyzerResult();

        String urlStr = url.toString();

        try {
            logger.info("Html analyzing starts for the url \"{}\"", urlStr);

            String html = UrlReader.newInstance().getContent(url);

            try {
                Document htmlDocument = Jsoup.parse(html);
                logger.debug("Html has been parsed for the url \"{}\"", urlStr);

                // find the document type
                logger.debug("Getting the document type for the url \"{}\"", urlStr);
                for (Node node : htmlDocument.childNodes()) {
                    if (node instanceof DocumentType) {
                        result.setDocumentType(DocumentTypeFetcher.newInstance().fetchDocumentType(node.toString()));

                        logger.info("The document type is \"{}\" for the url \"{}\"", result.getDocumentType(), urlStr);
                        break;
                    }
                }

                // set title
                logger.debug("Getting the page title for the url \"{}\"", urlStr);
                result.setTitle(htmlDocument.title());
                logger.info("The page title is \"{}\" for the url \"{}\"", result.getTitle(), urlStr);

                // analyze heading levels
                logger.debug("Running heading level analyzing for the url \"{}\"", urlStr);
                result.setHeadingLevelCounter(sumUpHeadingLevels(htmlDocument));
                logger.info("The heading level counts: \"{}\" for the url \"{}\"", result.getHeadingLevelCounter().toString(), urlStr);

                // analyze hypermedia links
                logger.debug("Running hypermedia analyzing for the url \"{}\"", urlStr);
                result.setHyperMediaLinkSums(sumUpHypermediaLinks(htmlDocument, url.getHost()));
                logger.info("The hypermedia counts: \"{}\" for the url \"{}\"", result.getHyperMediaLinkSums().toString(), urlStr);

                // find any login form
                logger.debug("Running login detection for the url \"{}\"", urlStr);
                result.setHasLoginForm(LoginDetector.newInstance().hasLoginForm(htmlDocument));
                logger.info("Login form is {}found for the url \"{}\"", (!result.getHasLoginForm() ? "not " : ""), urlStr);

                result.succeed();
                logger.info("Html analyzing is completed successfully for the url \"{}\"", urlStr);
            } catch (IOException loginDetectorException) {
                result.failed(Messages.LOGIN_DETECTION_FAILED);
                logger.warn("Login detection failed for the url \"{}", urlStr, loginDetectorException);
            } catch (Exception jsoupException) {
                result.failed(Messages.HTML_NOT_VALID);
                logger.warn("Html cannot be parsed for the url \"{}\"", urlStr, jsoupException);
            }
        } catch (UrlReader.UrlNotFoundException htmlNotValid) {
            result.failed(Messages.URL_NOT_FOUND);
            logger.warn("The url \"{}\" is not found", urlStr, htmlNotValid);
        } catch (UrlReader.NotHtmlDocumentException htmlNotValid) {
            result.failed(Messages.HTML_NOT_VALID);
            logger.warn("The url \"{}\" is not a html document", urlStr, htmlNotValid);
        } catch (UrlReader.UrlNotReadableException | IOException urlNotFound) {
            result.failed(Messages.URL_NOT_VALID);
            logger.warn("The url \"{}\" is not readable", urlStr, urlNotFound);
        }

        return result;
    }

    /**
     * Messages
     */
    public static class Messages {
        public static final String URL_NOT_FOUND = "The given url is not found";
        public static final String HTML_NOT_VALID = "The given url is not a valid html document";
        public static final String URL_NOT_VALID = "The given url is not valid";
        public static final String LOGIN_DETECTION_FAILED = "The login form detection is failed";
    }
}
