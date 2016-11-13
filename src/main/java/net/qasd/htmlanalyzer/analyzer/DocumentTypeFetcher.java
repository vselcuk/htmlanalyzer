package net.qasd.htmlanalyzer.analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @link https://en.wikipedia.org/wiki/Document_type_declaration
 */
public class DocumentTypeFetcher {

    // regex patterns for the most common document types
    public enum DocumentType {
        HTML5("(?i)<!DOCTYPE html>"),
        HTML("(?i)<!DOCTYPE html PUBLIC \".+ HTML (.+?)\\/\\/.+\".*>"),
        XHTML("(?i)<!DOCTYPE html PUBLIC \".+ XHTML (.+?)\\/\\/.+\".*>"),
        UNKNOWN("");

        private final String pattern;

        DocumentType(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Initiates a new instance of DocumentTypeFetcher
     *
     * @return A new instance of DocumentTypeFetcher
     */
    public static DocumentTypeFetcher newInstance() {
        return new DocumentTypeFetcher();
    }

    /**
     * Fetches the document type
     *
     * @param text Document type declaration text
     * @return Document type with version
     */
    public String fetchDocumentType(String text) {
        // normalize the document type text for better regular expression match
        text = normalizeDeclaration(text);

        // HTML5 check
        if (text.matches(DocumentType.HTML5.getPattern())) {
            return DocumentType.HTML5.toString();
        } else {
            // HTML check
            String version = matchVersion(text, DocumentType.HTML.getPattern());
            if (version != null) {
                return DocumentType.HTML.toString() + " " + version;
            }

            // XHTML check
            version = matchVersion(text, DocumentType.XHTML.getPattern());
            if (version != null) {
                return DocumentType.XHTML.toString() + " " + version;
            }
        }

        // not supported types
        return DocumentType.UNKNOWN.toString();
    }

    /**
     * Matches the document type version
     *
     * @param text          Document type text to be matched
     * @param patternString Regular expression pattern
     * @return Matched version
     */
    protected String matchVersion(String text, String patternString) {
        String version = null;

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            version = matcher.group(1);
        }

        return version;
    }

    /**
     * Normalize the document type declaration text by replacing new lines and multiple occurrence of spaces
     *
     * @param declaration Document type declaration
     * @return Html document type
     */
    protected String normalizeDeclaration(String declaration) {
        // replace new line with space
        declaration = declaration.replace("\n", " ");

        // multiple spaces with single space
        declaration = declaration.replaceAll("\\s{2,}", " ").trim();

        return declaration;
    }
}