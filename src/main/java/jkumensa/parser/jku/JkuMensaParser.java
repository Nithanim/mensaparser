package jkumensa.parser.jku;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.parser.MensaDayData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JkuMensaParser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

    public Map<MensaSubType, List<MensaDayData>> parse(Document doc) {
        Element days = doc.select("#speiseplan.desktop #week #days").first();

        Map<MensaSubType, List<MensaDayData>> data = new EnumMap<>(MensaSubType.class);
        Arrays.stream(MensaSubType.values()).forEach(t -> data.put(t, new ArrayList<>(6)));
        
        for (Element day : days.select(">.day")) {
            Map<MensaSubType, MensaDayData> mdm = parseDay(day);
            mdm.entrySet().forEach((e) -> {
                data.get(e.getKey()).add(e.getValue());
            });
        }
        return data;
    }

    private Map<MensaSubType, MensaDayData> parseDay(Element day) {
        String dateString = day.select(".date").text().trim();
        LocalDate date = DATE_FORMATTER.parse(dateString, LocalDate::from);

        Elements rawCategories = day.select(".day-content > .category");

        Map<MensaSubType, MensaDayData> data = new EnumMap<>(MensaSubType.class);
        List<MensaCategoryData> classics = rawCategories.stream()
            .filter(e -> e.select(".category-title").first().text().contains("Classic"))
            .map(e -> new JkuClassicSubparser().parse(e))
            .collect(Collectors.toList());
        data.put(MensaSubType.CLASSIC, new MensaDayData(date, classics));

        List<MensaCategoryData> choices = new JkuChoiceSubparser().parse(
            rawCategories.stream().filter(e -> e.select(".category-title").first().text().contains("Choice")).findAny().get()
        );
        data.put(MensaSubType.CHOICE, new MensaDayData(date, choices));

        return data;
    }

    public enum MensaSubType {
        CLASSIC, CHOICE;
    }
}
