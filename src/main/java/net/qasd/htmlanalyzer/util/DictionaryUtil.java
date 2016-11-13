package net.qasd.htmlanalyzer.util;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class DictionaryUtil {

    public static final String FILE_NAME_LOGIN_ACTION = "dictionaries/loginaction.txt";
    public static final String FILE_NAME_USERNAME = "dictionaries/username.txt";

    /**
     * Reads the dictionary file from the resource folder and makes a list of its content
     *
     * @param filename File name
     * @return Dictionary list
     * @throws IOException If the file io action is failed
     */
    public static List<String> getDictionaryFromResourceFile(String filename) throws IOException {
        InputStream inputStream = DictionaryUtil.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream != null) {
            return IOUtils.readLines(inputStream, Charset.forName("UTF-8"));
        }

        throw new FileNotFoundException();
    }
}
