package net.qasd.htmlanalyzer.analyzer;

import net.qasd.htmlanalyzer.util.DictionaryUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class LoginDetector {

    private static final String FORM_METHOD_POST = "POST";

    /**
     * Initiates a new instance of LoginDetector
     *
     * @return A new instance of LoginDetector
     */
    public static LoginDetector newInstance() {
        return new LoginDetector();
    }

    /**
     * Checks whether the html document has a login form
     *
     * @param document Parsed html document
     * @return true if the document has a login form
     * @throws IOException If any io action fails for the dictionary loading
     */
    public boolean hasLoginForm(Document document) throws IOException {
        // collect all form elements
        Elements formElements = document.select("form");

        for (Element element : formElements) {
            if (existsPasswordField(element)) {
                return true;
            } else if (isTwoStepLogin(element)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the form element has ONLY one password field as child
     *
     * @param formElement Form Element
     * @return true If the form has ONLY one password field
     */
    protected boolean existsPasswordField(Element formElement) {
        // if there is ONLY one password field then assume it as login form
        Elements passwordElements = formElement.select("input[type=password]");
        if (passwordElements.size() == 1) {
            return true;
        }
        //else might be a registration form

        return false;
    }

    /**
     * Checks whether
     * the form method is POST and
     * the form contains at least one text input type with its name similar to username or email and
     * the action contains something similar to login or auth
     * for assuming the element as two step login form
     * <p>
     * Two step login: The login action is divided to two steps; in the first step entering the username and
     * in the second step entering th password
     *
     * @param formElement Form Element
     * @return true If the form is a two step login form
     * @throws IOException If any io action fails for the dictionary loading
     */
    protected boolean isTwoStepLogin(Element formElement) throws IOException {
        String formMethod = formElement.attr("method");
        String formAction = formElement.attr("action");

        if (!formMethod.trim().isEmpty()
            && !formAction.trim().isEmpty()) {
            // the method is POST
            if (formMethod.equalsIgnoreCase(FORM_METHOD_POST)) {
                // if action is allowed
                if (existsInDictionary(formAction, DictionaryUtil.getDictionaryFromResourceFile(DictionaryUtil.FILE_NAME_LOGIN_ACTION))) {
                    // contains ONLY one text field
                    Elements textElements = formElement.select("input[type=text]");
                    if (textElements.size() == 1) {
                        // get the name of the field
                        String textElementName = textElements.get(0).attr("name");
                        if (textElementName.trim().isEmpty()) {
                            textElementName = textElements.get(0).id();
                        }
                        if (!textElementName.trim().isEmpty()) {
                            // if field name is allowed
                            if (existsInDictionary(textElementName, DictionaryUtil.getDictionaryFromResourceFile(DictionaryUtil.FILE_NAME_USERNAME))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks whether the given text exists in the dictionary
     *
     * @param text       Text to be looked up
     * @param dictionary List of dictionary items
     * @return true If the given text is matched in the dictionary
     */
    protected boolean existsInDictionary(String text, List<String> dictionary) {
        // normalize it for the dictionary look up
        text = text.replaceAll("[^A-Za-z ]", "").toLowerCase();

        for (String item : dictionary) {
            if (text.contains(item.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
