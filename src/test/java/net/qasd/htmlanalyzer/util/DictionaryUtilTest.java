package net.qasd.htmlanalyzer.util;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DictionaryUtilTest {

    private static final String FILE_DATA = "test1\n" +
        "test2\n" +
        "test3";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFetDictionaryFromResourceFile() throws IOException {
        File testFile = new File(ClassLoader.getSystemClassLoader().getResource("").getPath() + "/test.txt");
        FileUtils.write(testFile, FILE_DATA);

        assertEquals(3, DictionaryUtil.getDictionaryFromResourceFile("test.txt").size());

        FileUtils.deleteQuietly(testFile);
    }

    @Test
    public void testGetDictionaryFromNonExistingResourceFile() throws IOException {
        thrown.expect(FileNotFoundException.class);

        DictionaryUtil.getDictionaryFromResourceFile("tmp.txt");
    }
}