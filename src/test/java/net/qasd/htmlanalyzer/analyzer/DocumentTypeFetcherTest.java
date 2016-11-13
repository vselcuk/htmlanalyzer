package net.qasd.htmlanalyzer.analyzer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentTypeFetcherTest {

    private static DocumentTypeFetcher typeFetcher;

    @BeforeClass
    public static void setUp() {
        typeFetcher = DocumentTypeFetcher.newInstance();
    }

    @Test
    public void testFetchDocumentType() {
        assertEquals("HTML5", typeFetcher.fetchDocumentType("<!DOCTYPE html>"));
        assertEquals("XHTML Basic 1.0", typeFetcher.fetchDocumentType("<!DOCTYPE html PUBLIC\n" +
            "  \"-//W3C//DTD XHTML Basic 1.0//EN\"\n" +
            "  \"http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd\">"));
        assertEquals("XHTML 1.0 Strict", typeFetcher.fetchDocumentType("<!DOCTYPE html\n" +
            "     PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
            "     \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"));
        assertEquals("HTML 4.01", typeFetcher.fetchDocumentType("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n" +
            "   \"http://www.w3.org/TR/html4/strict.dtd\">"));
        assertEquals(DocumentTypeFetcher.DocumentType.UNKNOWN.toString(), typeFetcher.fetchDocumentType("<!DOCTYPE HTML PUBLIC>"));
    }

    @Test
    public void testMatchVersion() {
        assertEquals("Basic 1.0",
            typeFetcher.matchVersion(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.0//EN\">",
                DocumentTypeFetcher.DocumentType.XHTML.getPattern()
            )
        );
        assertEquals("4.01",
            typeFetcher.matchVersion(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN url\">",
                DocumentTypeFetcher.DocumentType.HTML.getPattern()
            )
        );
    }

    @Test
    public void testNormalizeDeclaration() {
        assertEquals("test test", typeFetcher.normalizeDeclaration("test \ntest"));
        assertEquals("test test test", typeFetcher.normalizeDeclaration("test   \ntest\n \n test "));
    }
}