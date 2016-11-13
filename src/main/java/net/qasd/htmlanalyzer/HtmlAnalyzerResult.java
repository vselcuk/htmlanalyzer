package net.qasd.htmlanalyzer;

import net.qasd.htmlanalyzer.analyzer.TagAnalyzers;
import net.qasd.htmlanalyzer.util.MutableInteger;

import java.util.Map;

public class HtmlAnalyzerResult {

    private boolean status;

    private String message;

    private String documentType;

    private String title;

    private Map<String, MutableInteger> headingLevelCounter;

    private TagAnalyzers.HyperMediaLinkSums hyperMediaLinkSums;

    private boolean hasLoginForm;

    /**
     * Gets the document type
     *
     * @return The document Type
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the document type
     *
     * @param documentType The document type
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the document title
     *
     * @return The document title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the document title
     *
     * @param title The document title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the heading level counts
     *
     * @return The heading level counts
     */
    public Map<String, MutableInteger> getHeadingLevelCounter() {
        return headingLevelCounter;
    }

    /**
     * Sets the heading level counts
     *
     * @param headingLevelCounter The heading level counts
     */
    public void setHeadingLevelCounter(Map<String, MutableInteger> headingLevelCounter) {
        this.headingLevelCounter = headingLevelCounter;
    }

    /**
     * Gets the hypermedia analyze result
     *
     * @return The hypermedia analyze result
     */
    public TagAnalyzers.HyperMediaLinkSums getHyperMediaLinkSums() {
        return hyperMediaLinkSums;
    }

    /**
     * Sets the hypermedia analyze result
     *
     * @param hyperMediaLinkSums The hypermedia analyze result
     */
    public void setHyperMediaLinkSums(TagAnalyzers.HyperMediaLinkSums hyperMediaLinkSums) {
        this.hyperMediaLinkSums = hyperMediaLinkSums;
    }

    /**
     * Gets the login form flag
     *
     * @return true if any login form exists
     */
    public boolean getHasLoginForm() {
        return hasLoginForm;
    }

    /**
     * Sets the flag for existing login form
     *
     * @param hasLoginForm the flag for existing login form
     */
    public void setHasLoginForm(boolean hasLoginForm) {
        this.hasLoginForm = hasLoginForm;
    }

    /**
     * Sets the analyze status to failed
     *
     * @param message Error message
     */
    public void failed(String message) {
        this.status = false;
        this.message = message;
    }

    /**
     * Sets the analyze status to succeed
     */
    public void succeed() {
        this.status = true;
    }

    /**
     * Returns the analyze status
     *
     * @return true if the analyze is succeed
     */
    public boolean isSucceed() {
        return status;
    }

    /**
     * Gets the status message
     *
     * @return Status message
     */
    public String getMessage() {
        return message;
    }
}
