package jkumensa.parser.khg;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jkumensa.parser.data.CategoryData;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.MensaDayData;
import jkumensa.parser.data.SubCategoryData;
import jkumensa.parser.ex.MensaDateParsingException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class KhgMensaParser {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d\\d)\\.[^\\d]*(\\d\\d)\\.(?:(\\d\\d)\\.|[^A-Z]*([A-Za-z]*))[^\\d]*(\\d\\d\\d\\d)");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);

    public List<MensaDayData> parse(Document doc) {
        Element container = doc.select(".modTeaser .swslang").first();

        Element prev = null;
        Element curr = null;
        Iterator<Element> dateSearchIt = container.children().iterator();
        //skip everything until table; element before holds the date
        while (dateSearchIt.hasNext()) {
            curr = dateSearchIt.next();
            if (curr.tagName().equals("table")) {
                break;
            }
            prev = curr;
        }

        Element dateContainer = prev;
        Element dataTable = curr;

        Matcher m = DATE_PATTERN.matcher(((TextNode) dateContainer.childNodes().get(2)).text());
        m.find();
        int day1 = Integer.parseInt(m.group(1));
        int day2 = Integer.parseInt(m.group(2));
        String monthInt = m.group(3);
        String monthString = m.group(4);
        int year = Integer.parseInt(m.group(5));

        YearMonth ym;
        if (monthString != null) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM", Locale.GERMAN);
            ym = YearMonth.of(year, f.parse(monthString).get(ChronoField.MONTH_OF_YEAR));

        } else if (monthInt != null) {
            ym = YearMonth.of(year, Integer.valueOf(monthInt));
        } else {
            throw new MensaDateParsingException("Unable to parse month! No regex match!");
        }
        LocalDate mondayDate = LocalDate.of(year, ym.getMonth(), day1);

        ArrayList<MensaDayData> mensaDays = new ArrayList<>();
        Iterator<Element> trIt = dataTable.select(">tbody>tr").iterator();
        while (trIt.hasNext()) {
            Element baseTr = trIt.next();
            Elements tds = baseTr.select(">td");

            Element weekdayTd = tds.get(0);
            int rowspan = Integer.parseInt(weekdayTd.attr("rowspan"));

            String weekday = weekdayTd.text();
            int dayOffset = Weekday.valueOf(weekday).ordinal();
            LocalDate date = mondayDate.plusDays(dayOffset);

            List<MealData> meals = new ArrayList<>(3);

            tds.remove(0);
            if (tds.get(0).text().length() > 5 /*easy empty check because of nbsp and other funny things*/) {
                meals.add(parseMeal(tds));
            }
            for (int i = 0; i < rowspan - 1 && trIt.hasNext() /*fix for rowspan without having enough elements*/; i++) {
                tds = trIt.next().select(">td");
                if (tds.get(0).text().length() > 5) {
                    meals.add(parseMeal(tds));
                }
            }

            List<SubCategoryData> subCategories = new ArrayList<>(1);
            subCategories.add(new SubCategoryData(null, meals, -1, -1, -1, Collections.emptySet()));
            List<CategoryData> categories = new ArrayList<>(1);
            categories.add(new CategoryData(null, subCategories));
            mensaDays.add(new MensaDayData(date, categories));

        }

        return mensaDays;
    }

    private MealData parseMeal(List<Element> tds) {
        String title = cleanString(tds.get(0).text());
        Float bonusPrice = parseFloat(tds.get(1).text());
        Float normalPrice = parseFloat(tds.get(2).text());
        return new MealData(title, bonusPrice, -1, normalPrice, Collections.emptySet(), Collections.emptySet());
    }

    private float parseFloat(String s) {
        String t = s.replace(",", ".");
        if (t.length() > 2 /*Poor-man's empty check*/) {
            try {
                return Float.parseFloat(t);
            } catch (Exception ex) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private String cleanString(String s) {
        return s.replace("\u00a0", " ").trim();
    }

    private enum Weekday {
        MO, DI, MI, DO, FR, SA, SO;
    }
}
