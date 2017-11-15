package jkumensa.parser;

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
import jkumensa.parser.data.CategoryData;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.MensaDayData;
import jkumensa.parser.data.SubCategoryData;
import jkumensa.parser.ex.MensaAllergyCodeParsingException;
import jkumensa.parser.ex.MensaMealParsingException;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import lombok.Value;
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

        List<CategoryData> categories = new ArrayList<>();

        List<SubCategoryData> classic = parseClassic(
            day.select(".day-content > .category").stream()
                .filter(e -> e.select(".category-title").first().text().contains("lassic"))
                .collect(Collectors.toList())
        );
        categories.add(new CategoryData("Classic", classic));

        return new MensaDayData(date, categories);
    }

    private List<SubCategoryData> parseClassic(List<Element> categories) {
        List<SubCategoryData> subcats = new ArrayList<>();

        for (Element categoryElement : categories) {
            String title = categoryElement.select(".category-title").first().text();

            if (title.contains("Classic")) {
                Element catcontent = categoryElement.select(".category-content").first();
                List<List<Node>> cleaned = Cleaner.splitFuckedUpFreeformUserInput(catcontent.children());
                List<MealData> meals = parseClassicMeals(cleaned);

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

                SubCategoryData s = new SubCategoryData("Classic", meals, priceStudentBonus, priceStudent, priceGuest, Collections.emptySet());
                subcats.add(s);
            }
        }

        return subcats;
    }

    private List<MealData> parseClassicMeals(List<List<Node>> raw) {
        if (raw.get(0).stream().anyMatch(n -> n.toString().contains("geschlossen"))) {
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
                    allergyCodes.addAll(parseAllergyCodes(allergyString));
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

    private EnumSet<AllergyCode> parseAllergyCodes(String allergyString) {
        EnumSet<AllergyCode> s = EnumSet.noneOf(AllergyCode.class);
        for (int i = 0; i < allergyString.length(); i++) {
            char c = allergyString.charAt(i);
            if ('A' <= c && c <= 'Z') {
                try {
                    s.add(AllergyCode.valueOf(String.valueOf(c)));
                } catch (IllegalArgumentException ex) {
                    throw new MensaAllergyCodeParsingException(ex);
                }
            }
        }
        return s;
    }

    @Value
    private class Dual<A, B> {
        A a;
        B b;
    }
}
