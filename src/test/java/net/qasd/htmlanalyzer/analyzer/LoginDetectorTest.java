package net.qasd.htmlanalyzer.analyzer;

import net.qasd.htmlanalyzer.util.DictionaryUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DictionaryUtil.class)
public class LoginDetectorTest {

    private static LoginDetector loginDetector;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        loginDetector = LoginDetector.newInstance();
    }

    @Test
    public void testOnePasswordField() {
        String html = "<form>" +
            "<input type=\"text\" name=\"user\">" +
            "<input type=\"password\" name=\"pass\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertTrue(loginDetector.existsPasswordField(document.select("form").first()));
    }

    @Test
    public void testMultiplePasswordField() {
        String html = "<form>" +
            "<input type=\"text\" name=\"user\">" +
            "<input type=\"password\" name=\"pass\">" +
            "<input type=\"password\" name=\"re-pass\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertFalse(loginDetector.existsPasswordField(document.select("form").first()));
    }

    @Test
    public void testTwoStepLogin() throws IOException {
        String html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertTrue(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginMultipleTextField() throws IOException {
        String html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" name=\"user\">" +
            "<input type=\"text\" name=\"email\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginWrongMethod() throws IOException {
        String html = "<form method=\"get\" action=\"signin_test.php\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginWrongAction() throws IOException {
        String html = "<form method=\"post\" action=\"/save\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginNoActionAndNoMethod() throws IOException {
        String html = "<form method=\"\" action=\"/\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form action=\"signin\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form>" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginWithNameAttribute() throws IOException {
        String html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);
        assertTrue(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" name=\"\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" name=\"fname\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginWithIdAttribute() throws IOException {
        String html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" id=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);
        assertTrue(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" id=\"\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));

        html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" id=\"fname\">" +
            "</form>";
        document = Jsoup.parse(html);
        assertFalse(loginDetector.isTwoStepLogin(document.select("form").first()));
    }

    @Test
    public void testTwoStepLoginNotExistingDictionaries() throws IOException {
        String html = "<form method=\"post\" action=\"/signin\">" +
            "<input type=\"text\" id=\"user\">" +
            "</form>";
        Document document = Jsoup.parse(html);

        mockStatic(DictionaryUtil.class);

        when(DictionaryUtil.getDictionaryFromResourceFile(DictionaryUtil.FILE_NAME_LOGIN_ACTION)).thenThrow(IOException.class);
        thrown.expect(IOException.class);
        loginDetector.isTwoStepLogin(document.select("form").first());

        when(DictionaryUtil.getDictionaryFromResourceFile(DictionaryUtil.FILE_NAME_LOGIN_ACTION)).thenReturn(Arrays.asList("signin"));
        when(DictionaryUtil.getDictionaryFromResourceFile(DictionaryUtil.FILE_NAME_USERNAME)).thenThrow(IOException.class);
        thrown.expect(IOException.class);
        loginDetector.isTwoStepLogin(document.select("form").first());
    }

    @Test
    public void testHasLoginForm() throws IOException {
        String html = "<form>" +
            "<input type=\"text\" name=\"user\">" +
            "<input type=\"password\" name=\"pass\">" +
            "</form>";
        assertTrue(loginDetector.hasLoginForm(Jsoup.parse(html)));

        html = "<form method=post action=login>" +
            "<input type=\"text\" name=\"user\">" +
            "</form>";
        assertTrue(loginDetector.hasLoginForm(Jsoup.parse(html)));
    }

    @Test
    public void testExistsInDictionary() {
        assertTrue(loginDetector.existsInDictionary("test1", Arrays.asList("test", "abc")));
        assertFalse(loginDetector.existsInDictionary("test1", Arrays.asList("abc", "xyz")));
    }
}
