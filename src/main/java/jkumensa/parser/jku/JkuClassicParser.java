package jkumensa.parser.jku;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.api.MensaFoodCharacteristic;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import jkumensa.api.AllergyCodeSet;
import jkumensa.parser.MensaDayData;
import jkumensa.parser.ex.MensaAllergyCodeParsingException;
import jkumensa.parser.ex.MensaMealParsingException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class JkuClassicParser {
    private static final Pattern ALLERGY_PATTERN = Pattern.compile("(\\(\\s*[A-Z](?:[^A-Z]*[A-Z])*\\s*\\))");
    private static final Pattern CLASSIC_PRICE_PATTERN = Pattern.compile("[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*(\\d*,\\d*)[^\\d]*");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

    public List<MensaDayData> parse(Document doc) {
        Element days = doc.select("#speiseplan.desktop #week #days").first();

        List<MensaDayData> parsedDays = new ArrayList<>();
        for (Element day : days.select(">.day")) {
            MensaDayData md = parseDay(day);
            parsedDays.add(md);
        }
        return parsedDays;
    }

    private MensaDayData parseDay(Element day) {
        String dateString = day.select(".date").text().trim();
        LocalDate date = DATE_FORMATTER.parse(dateString, LocalDate::from);

        List<MensaCategoryData> classic = parseClassic(
            day.select(".day-content > .category").stream()
                .filter(e -> e.select(".category-title").first().text().contains("lassic"))
                .collect(Collectors.toList())
        );

        return new MensaDayData(date, classic);
    }

    private List<MensaCategoryData> parseClassic(List<Element> categories) {
        List<MensaCategoryData> cats = new ArrayList<>();

        for (Element categoryElement : categories) {
            String title = categoryElement.select(".category-title").first().text();

            if (title.contains("Classic")) {
                Element catcontent = categoryElement.select(".category-content").first();
                List<List<Node>> cleaned = Cleaner.splitFuckedUpFreeformUserInput(catcontent.children());
                List<MensaMealData> meals = parseClassicMeals(cleaned);

                float priceStudentBonus = -1;
                float priceStudent = -1;
                float priceGuest = -1;

                String pricesString = categoryElement.select(">.category-price").text();
                Matcher m = CLASSIC_PRICE_PATTERN.matcher(pricesString);
                if (m.matches()) {
                    priceStudentBonus = Float.parseFloat(m.group(1).replace(",", "."));
                    priceStudent = Float.parseFloat(m.group(2).replace(",", "."));
                    priceGuest = Float.parseFloat(m.group(3).replace(",", "."));
                }

                MensaCategoryData c = new MensaCategoryData("Classic", meals, priceStudentBonus, priceStudent, priceGuest, Collections.emptySet());
                cats.add(c);
            }
        }

        return cats;
    }

    private List<MensaMealData> parseClassicMeals(List<List<Node>> raw) {
        if (raw.get(0).stream().anyMatch(n -> n.toString().contains("geschlossen"))) {
            return null;
        }

        List<MensaMealData> meals = new ArrayList<>();

        for (List<Node> l : raw) {
            Element e = new Element("dummy");
            e.insertChildren(0, l);

            String fulltext = e.text();
            try {
                fulltext = e.text().trim();

                Matcher m = ALLERGY_PATTERN.matcher(fulltext);

                AllergyCodeSet allergyCodes = new AllergyCodeSet();
                EnumSet<MensaFoodCharacteristic> attachments = EnumSet.noneOf(MensaFoodCharacteristic.class);

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

                    attachments.addAll(parseFoodCharacteristics(null));
                    allergyCodes.addAll(parseAllergyCodes(allergyString));
                }
                if (pos < fulltext.length()) {
                    textWithoutAlleryCodes.append(fulltext.substring(pos, fulltext.length()).trim());
                }

                String title = textWithoutAlleryCodes.toString().trim();

                meals.add(new MensaMealData(title, -1, -1, -1, allergyCodes, attachments));
            } catch (Exception ex) {
                throw new MensaMealParsingException("Unable to parse meal from >>>" + fulltext + "<<<", ex);
            }
        }

        return meals;
    }

    private EnumSet<MensaFoodCharacteristic> parseFoodCharacteristics(String s) {
        return EnumSet.noneOf(MensaFoodCharacteristic.class); //TODO
    }

    private AllergyCodeSet parseAllergyCodes(String allergyString) {
        AllergyCodeSet s = new AllergyCodeSet();
        for (int i = 0; i < allergyString.length(); i++) {
            char c = allergyString.charAt(i);
            if ('A' <= c && c <= 'Z') {
                try {
                    s.add(c);
                } catch (IllegalArgumentException ex) {
                    throw new MensaAllergyCodeParsingException(ex);
                }
            }
        }
        return s;
    }
}
