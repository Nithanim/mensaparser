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
import jkumensa.parser.ex.MensaDateParsingException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class KhgMensaParser {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d\\d)\\.[^\\d]*(\\d\\d)\\.(?:(\\d\\d)\\.|[^A-Z]*([A-Za-z]*))[^\\d]*(\\d\\d\\d\\d)");

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

        LocalDate mondayDate = parseDate(((TextNode) dateContainer.childNodes().get(2)).text());

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

            List<CategoryData> categories = new ArrayList<>(2 + 1);
            tds.remove(0);
            if (tds.get(0).text().length() > 5 /*easy empty check because of nbsp and other funny things*/) {
                categories.add(parseCat(tds));
            }
            for (int i = 0; i < rowspan - 1 && trIt.hasNext() /*fix for rowspan without having enough elements*/; i++) {
                tds = trIt.next().select(">td");
                if (tds.get(0).text().length() > 5) {
                    categories.add(parseCat(tds));
                }
            }

            mensaDays.add(new MensaDayData(date, categories));
        }

        return mensaDays;
    }

    private LocalDate parseDate(String text) throws MensaDateParsingException, NumberFormatException {
        Matcher m = DATE_PATTERN.matcher(text);
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
        return mondayDate;
    }

    private CategoryData parseCat(List<Element> tds) {
        String title = cleanString(tds.get(0).text());
        Float bonusPrice = parseFloat(tds.get(1).text());
        Float normalPrice = parseFloat(tds.get(2).text());
        MealData meal = new MealData(title, -1, -1, -1, Collections.emptySet(), Collections.emptySet());
        ArrayList<MealData> meals = new ArrayList<>(3);
        meals.add(meal);

        return new CategoryData(null, meals, bonusPrice, -1, normalPrice, Collections.emptySet());
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
