package jkumensa.parser.jku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static jkumensa.parser.jku.Extractor.ALLERGY_PATTERN;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.SubCategoryData;
import jkumensa.parser.ex.MensaMealParsingException;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import lombok.Value;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class JkuClassicSubparser {
    private static final Pattern CLASSIC_PRICE_PATTERN = Pattern.compile("[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*");

    public SubCategoryData parse(Element categoryElement) {
        String title = categoryElement.select(".category-title").first().text();
        Element catcontent = categoryElement.select(".category-content").first();
        List<List<Node>> cleaned = Cleaner.splitFuckedUpFreeformUserInput(catcontent.children());
        List<MealData> meals = parseMeals(cleaned);

        float priceGuest = -1;
        float priceStudent = -1;
        float priceStudentBonus = -1;

        String pricesString = categoryElement.select(">.category-price").text();
        Matcher m = CLASSIC_PRICE_PATTERN.matcher(pricesString);
        if (m.matches()) {
            priceStudentBonus = Float.parseFloat(m.group(1).replace(",", "."));
            priceStudent = Float.parseFloat(m.group(2).replace(",", "."));
            priceGuest = Float.parseFloat(m.group(3).replace(",", "."));
        }

        Elements icons = categoryElement.select("> .category-icons img");
        Set<FoodCharacteristic> foodCharacteristics = Extractor.foodCharacteristicFromImg(icons);

        SubCategoryData s = new SubCategoryData(
            title,
            meals,
            priceGuest,
            priceStudent,
            priceStudentBonus,
            foodCharacteristics
        );
        return s;
    }

    private List<MealData> parseMeals(List<List<Node>> raw) {
        if (raw.get(0).stream().anyMatch(n -> n.toString().contains("eschlossen"))) {
            return Collections.emptyList();
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

                StringBuilder textWithoutAllergyCodes = new StringBuilder();
                int pos = 0;
                while (m.find()) {
                    //Build the text of the meal without the allergy symbols
                    String allergyString = m.group(1);
                    if (pos != m.start()) {
                        textWithoutAllergyCodes.append(fulltext.substring(pos, m.start()).trim());
                        textWithoutAllergyCodes.append(' ');
                        pos = m.end();
                    }

                    allergyCodes.addAll(Extractor.parseAllergyCodes(allergyString));
                }
                if (pos < fulltext.length()) {
                    textWithoutAllergyCodes.append(fulltext.substring(pos, fulltext.length()).trim());
                }

                String title = textWithoutAllergyCodes.toString().trim();

                meals.add(new MealData(title, -1, -1, -1, allergyCodes, EnumSet.noneOf(FoodCharacteristic.class)));
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
