package jkumensa.parser.jku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jkumensa.api.AllergyCodeSet;
import jkumensa.api.MensaFoodCharacteristic;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import jkumensa.parser.ex.MensaMealParsingException;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class JkuClassicSubparser {
    private static final Pattern CLASSIC_PRICE_PATTERN = Pattern.compile("[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*");

    public MensaCategoryData parse(Element categoryElement) {
        String title = categoryElement.select(".category-title").first().text();
        Element catcontent = categoryElement.select(".category-content").first();
        List<List<Node>> cleaned = Cleaner.splitFuckedUpFreeformUserInput(catcontent.children());
        List<MensaMealData> meals = parseMeals(cleaned);

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
        Set<MensaFoodCharacteristic> foodCharacteristics = Extractor.foodCharacteristicFromImg(icons);

        MensaCategoryData s = new MensaCategoryData(
            title,
            meals,
            priceGuest,
            priceStudent,
            priceStudentBonus,
            foodCharacteristics
        );
        return s;
    }

    private List<MensaMealData> parseMeals(List<List<Node>> raw) {
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        if (raw.get(0).stream().anyMatch(n -> n.toString().contains("eschlossen"))) {
            return Collections.emptyList();
        }

        List<MensaMealData> meals = new ArrayList<>();

        for (List<Node> l : raw) {
            Element e = new Element("dummy");
            e.insertChildren(0, l);

            String fulltext = e.text();
            try {
                fulltext = e.text().trim();

                AllergyCodeSet allergyCodes = new AllergyCodeSet();
                Set<MensaFoodCharacteristic> foodCharacteristics = Extractor.foodCharacteristicFromImg(e.select("img"));

                String title = Extractor.extractAndRemoveAllergyCodes(fulltext, allergyCodes);

                meals.add(new MensaMealData(title, -1, -1, -1, allergyCodes, foodCharacteristics));
            } catch (Exception ex) {
                throw new MensaMealParsingException("Unable to parse meal from >>>" + fulltext + "<<<", ex);
            }
        }

        return meals;
    }
}
