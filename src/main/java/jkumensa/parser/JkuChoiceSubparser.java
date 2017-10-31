package jkumensa.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static jkumensa.parser.Extractor.ALLERGY_PATTERN;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.SubCategoryData;
import jkumensa.parser.ex.MensaMealParsingException;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import lombok.Value;
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

    public List<SubCategoryData> parse(Element categoryElement) {
        String title = categoryElement.select(".category-title").first().text();
        Element catcontent = categoryElement.select(".category-content").first();
        catcontent.select("> p").first().remove(); //first p contains date -> so remove TODO: make more robust

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

    private SubCategoryData parseSubCategory(List<? extends Node> raw) {
        ListIterator<? extends Node> it = raw.listIterator();

        String title = ((Element) it.next()).text();
        it.remove();

        List<List<Node>> groups = Cleaner.groupByDiscardingTag("br", raw);

        List<MealData> meals = groups.stream()
            .map(this::parseMeal)
            .collect(Collectors.toList());
        
        return new SubCategoryData(title, meals, -1, -1, -1, Collections.emptySet());
    }

    private MealData parseMeal(List<Node> raw) {
        EnumSet<FoodCharacteristic> foodCharacteristics = extractFoodCharacteristics(raw);

        Element e = new Element("dummy");
        e.insertChildren(0, raw);

        String fulltext = e.text();

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
        
        return new MealData(text, -1, -1, price, Collections.emptySet(), foodCharacteristics);
    }

    private EnumSet<FoodCharacteristic> extractFoodCharacteristics(List<? extends Node> nodes) {
        return nodes.stream()
            .filter(Element.class::isInstance)
            .map(Element.class::cast)
            .filter(e -> e.tagName().equals("img"))
            .map(Extractor::foodCharacteristicFromImg)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(FoodCharacteristic.class)));
    }

    private List<MealData> parseMeals(List<List<Node>> raw) {
        if (raw.get(0).stream().anyMatch(n -> n.toString().contains("eschlossen"))) {
            return null;
        }

        List<MealData> meals = new ArrayList<>();

        for (List<Node> l : raw) {
            Element e = new Element("dummy");
            e.insertChildren(0, l);

            String fulltext = e.text();
            try {
                fulltext = e.text().trim();

                Matcher m = ALLERGY_PATTERN.matcher(fulltext);

                EnumSet<AllergyCode> allergyCodes = EnumSet.noneOf(AllergyCode.class);
                EnumSet<FoodCharacteristic> attachments = EnumSet.noneOf(FoodCharacteristic.class);

                StringBuilder textWithoutAlleryCodes = new StringBuilder();
                int pos = 0;
                while (m.find()) {
                    //Build the text of the meal without the allergy symbols
                    String allergyString = m.group(1);
                    if (pos != m.start()) {
                        textWithoutAlleryCodes.append(fulltext.substring(pos, m.start()).trim());
                        textWithoutAlleryCodes.append(' ');
                        pos = m.end();
                    }

                    attachments.addAll(parseAttachments(null));
                    allergyCodes.addAll(Extractor.parseAllergyCodes(allergyString));
                }
                if (pos < fulltext.length()) {
                    textWithoutAlleryCodes.append(fulltext.substring(pos, fulltext.length()).trim());
                }

                String title = textWithoutAlleryCodes.toString().trim();

                meals.add(new MealData(title, -1, -1, -1, allergyCodes, attachments));
            } catch (Exception ex) {
                throw new MensaMealParsingException("Unable to parse meal from >>>" + fulltext + "<<<", ex);
            }
        }

        return meals;
    }

    private Dual<String, EnumSet<FoodCharacteristic>> extractAttachments(String s) {
        Matcher m = ALLERGY_PATTERN.matcher(s);

        StringBuilder text = new StringBuilder();
        EnumSet<FoodCharacteristic> attachments = EnumSet.noneOf(FoodCharacteristic.class);

        int pos = 0;
        boolean matchedSomething = false; //can only call end() when something matched...
        while (m.find()) {
            //Build the text of the meal without the allergy symbols
            String allergyString = m.group(1);
            if (pos != m.start()) {
                text.append(s.substring(pos, m.start()));
                pos = m.end();
            }

            attachments.addAll(parseAttachments(allergyString));
            matchedSomething = true;
        }
        if (!matchedSomething || m.end() < text.length()) {
            text.append(text.substring(pos, text.length()));
        }

        String title = text.toString();
        return new Dual<>(title, attachments);
    }

    private EnumSet<FoodCharacteristic> parseAttachments(String attachments) {
        return EnumSet.noneOf(FoodCharacteristic.class); //TODO
    }

    @Value
    private class Dual<A, B> {
        A a;
        B b;
    }
}
