package net.qasd.htmlanalyzer.analyzer;

import net.qasd.htmlanalyzer.util.MutableInteger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class TagAnalyzers {

    // possible external hypermedia links:
    // protocol://different_host/path?query
    // //different_host/path?query
    private static final String EXTERNAL_LINKS_REGEX_TEMPLATE = "(?:.+:\\/\\/|\\/\\/)((?!%s)[^/]+)(?:\\/.*)?";
    private static final List<String> headingTags = new ArrayList<>(6);

    // prepare the heading tags (h1 to h6)
    static {
        IntStream.rangeClosed(1, 6).forEach(i -> headingTags.add("h" + i));
    }

    /**
     * Sums up the occurrence of the headings by their level
     *
     * @param document Parsed html document
     * @return Sums of the heading levels
     */
    public static Map<String, MutableInteger> sumUpHeadingLevels(Document document) {
        // find the all headings
        Elements headings = document.select(String.join(", ", headingTags));

        // loop through the found headings to sum up the occurrences by heading level
        Map<String, MutableInteger> headingLevelCounter = new HashMap<>();
        headings.forEach(heading -> {
            String headingLevel = heading.tagName();
            MutableInteger count = headingLevelCounter.get(headingLevel);
            // initial entry
            if (count == null) {
                headingLevelCounter.put(headingLevel, new MutableInteger(1));
            } else {
                // update the existing value
                count.setValue(count.getValue() + 1);
            }
        });

        return headingLevelCounter;
    }

    /**
     * Sums up the internal and external links
     *
     * @param document Parsed html document
     * @return the sumps of the internal/external links
     */
    public static HyperMediaLinkSums sumUpHypermediaLinks(Document document, String host) {
        HyperMediaLinkSums result = new HyperMediaLinkSums();

        // collect all elements with href and src (a[href], link[href], area[href], img[src], script[src])
        Elements hyperMediaElements = document.select("[href], [src]");

        // compile to regex patter to us it for all found elements
        Pattern externalLinksPattern = Pattern.compile(
            String.format(EXTERNAL_LINKS_REGEX_TEMPLATE, Pattern.quote(host))
        );

        hyperMediaElements.forEach(element -> {
            // get the link from either href or src attribute
            String link;
            if (element.hasAttr("href")) {
                link = element.attr("href");
            } else {
                link = element.attr("src");
            }

            // if external regex matches increase the count
            if (externalLinksPattern.matcher(link).matches()) {
                result.external.setValue(result.external.getValue() + 1);
            } else {//any empty link counts as internal
                result.internal.setValue(result.internal.getValue() + 1);
            }
        });

        return result;
    }

    /**
     * Simple class to store hypermedia link sums
     */
    public static class HyperMediaLinkSums {
        private MutableInteger internal = new MutableInteger(0);
        private MutableInteger external = new MutableInteger(0);

        public MutableInteger getInternal() {
            return internal;
        }

        public MutableInteger getExternal() {
            return external;
        }

        public String toString() {
            return String.format("{internal=%d, external=%d}", internal.getValue(), external.getValue());
        }
    }
}
