package jkumensa.parser.jku;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.api.AllergyCodeSet;
import jkumensa.api.MensaFoodCharacteristic;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class JkuChoiceSubparser {
    /**
     * Either yields one or two prices.
     *
     * There is
     * <pre>
     * Wiener Schnitzel vom Schwein mit Reis 5,10 Euro
     * </pre> and
     * <pre>
     * Pasta Spaghetti Bolognese/Carbonara 3,00/3,90 Euro
     * </pre>
     */
    private static final Pattern PRICE_PATTERN = Pattern.compile("(?:(\\d+,\\d\\d)\\/)?(\\d+,\\d\\d) Euro");

    public List<MensaCategoryData> parse(Element categoryElement) {
        String title = categoryElement.select(".category-title").first().text();
        Element catcontent = categoryElement.select(".category-content").first();
        /* Choice-page changed on 21.11.2018 - seems that choice is printed each day and no longer has a <p>-tag for date */
//        Elements elems = catcontent.select("> p");
//        if (!elems.isEmpty()) {
//            elems.first().remove(); //first p contains date -> so remove
//        }

        List<Node> flattened = Cleaner.flattenPTags(catcontent.children());
        Cleaner.replaceNbsp(flattened);
        Cleaner.filterMultiBrAndTrim(flattened);
        //TODO: combine direct following strong tags. Prevents <strong>Pi</strong><strong>zza</strong>
        //at least it was needed with the old website
        List<List<Node>> cleaned = Cleaner.groupByKeepingTag("strong", flattened);
        cleaned.forEach(Cleaner::removeLeadingAndTrailingBrs);

        return cleaned.stream()
            .map(this::parseSubCategory)
            .collect(Collectors.toList());
    }

    private MensaCategoryData parseSubCategory(List<? extends Node> raw) {
        ListIterator<? extends Node> it = raw.listIterator();

        String title = ((Element) it.next()).text();
        it.remove();

        List<List<Node>> groups = Cleaner.groupByDiscardingTag("br", raw);

        List<MensaMealData> meals = groups.stream()
            .map(this::parseMeal)
            .collect(Collectors.toList());

        return new MensaCategoryData(title, meals, -1, -1, -1, Collections.emptySet());
    }

    private MensaMealData parseMeal(List<Node> raw) {

        Element e = new Element("dummy");
        e.insertChildren(0, raw);

        Set<MensaFoodCharacteristic> foodCharacteristics = Extractor.foodCharacteristicFromImg(e.select("img"));

        String fulltext = e.text();
        AllergyCodeSet allergyCodes = new AllergyCodeSet();
        fulltext = Extractor.extractAndRemoveAllergyCodes(fulltext, allergyCodes);

        //build generic method to extract info by regex and stripping it off the string
        float price = -1;
        StringBuilder textWithoutPrices = new StringBuilder();
        int pos = 0;
        Matcher m = PRICE_PATTERN.matcher(fulltext);
        if (m.find()) {
            if (pos != m.start()) {
                textWithoutPrices.append(fulltext.substring(pos, m.start()).trim());
                textWithoutPrices.append(' ');
                pos = m.end();
            }

            if (m.groupCount() == 1) {
                price = Float.parseFloat(m.group(1).replace(",", "."));
            } else if (m.groupCount() == 2) {
                price = Float.parseFloat(m.group(2).replace(",", "."));
            }

        }
        if (pos < fulltext.length()) {
            textWithoutPrices.append(fulltext.substring(pos, fulltext.length()).trim());
        }

        String text = textWithoutPrices.toString().trim();

        return new MensaMealData(text, -1, -1, price, allergyCodes, foodCharacteristics);
    }
}
