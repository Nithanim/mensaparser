package jkumensa.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.parser.data.CategoryData;
import jkumensa.parser.data.MensaDayData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JkuMensaParser {
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

        Elements rawCategories = day.select(".day-content > .category");

        categories.add(
            new CategoryData(
                "Classic",
                rawCategories.stream()
                    .filter(e -> e.select(".category-title").first().text().contains("Classic"))
                    .map(e -> new JkuClassicSubparser().parse(e))
                    .collect(Collectors.toList())
            )
        );
        categories.add(
            new CategoryData(
                "Choice",
                new JkuChoiceSubparser().parse(
                    rawCategories.stream().filter(e -> e.select(".category-title").first().text().contains("Choice")).findAny().get()
                )
            )
        );

        return new MensaDayData(date, categories);
    }
}
