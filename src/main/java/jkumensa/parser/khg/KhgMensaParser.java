package jkumensa.parser.khg;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jkumensa.api.AllergyCodeSet;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import jkumensa.parser.MensaDayData;
import jkumensa.parser.ex.MensaDateParsingException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class KhgMensaParser {
    /**
     * 1: int: start day <br>
     * 2: int: start month (optional) <br>
     * 3: int: end day <br>
     * 4: int: end month (or 5) <br>
     * 5: str: end month (or 4) <br>
     * 6: int: end year
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d?\\d)\\.(?:[^-\\d]*(\\d?\\d))?[^\\d]*(\\d?\\d)\\.\\s*(?:(\\d?\\d)\\.|([A-Za-zäöü]*))\\s*(\\d?\\d?\\d\\d)");

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

            int menuNumber = 1;
            List<MensaCategoryData> categories = new ArrayList<>(2 + 1);
            tds.remove(0);
            if (tds.get(0).text().length() > 5 /*easy empty check because of nbsp and other funny things*/) {
                categories.add(parseCat(tds, menuNumber++));
            }
            for (int i = 0; i < rowspan - 1 && trIt.hasNext() /*fix for rowspan without having enough elements*/; i++) {
                tds = trIt.next().select(">td");
                if (tds.get(0).text().length() > 5) {
                    categories.add(parseCat(tds, menuNumber++));
                }
            }

            mensaDays.add(new MensaDayData(date, categories));
        }

        return mensaDays;
    }

    /**
     * 1: int: start day
     *
     * 2: int: start month (optional)
     *
     * 3: int: end day 4: int: end month (or 5) 5: str: end month (or 4) 6: int:
     * end year
     */
    private LocalDate parseDate(String text) throws MensaDateParsingException, NumberFormatException {
        Matcher m = DATE_PATTERN.matcher(text);
        m.find();
        int startDay = Integer.parseInt(m.group(1));
        int startMonth = m.group(2) != null ? Integer.parseInt(m.group(2)) : -1;
        int endDay = Integer.parseInt(m.group(3));
        int endMonth;

        if (m.group(4) != null) {
            endMonth = Integer.parseInt(m.group(4));
        } else if (m.group(5) != null) {
            endMonth = monthStrToNum(m.group(5));
        } else {
            throw new MensaDateParsingException("Unable to parse date \"" + text + "\"");
        }

        int endYear = Integer.parseInt(m.group(6));
        if (endYear < 2000) {
            endYear += 2000;
        }

        LocalDate endDate = LocalDate.of(endYear, endMonth, endDay);
        LocalDate startDate = endDate.minusDays(4);

        if (startDate.getDayOfMonth() != startDay) {
            throw new MensaDateParsingException("Calculated start day does not match parsed one \"" + text + "\"");
        }
        return startDate;
    }

    private MensaCategoryData parseCat(Elements tds, int menuNumber) {
        String title = cleanString(tds.get(0).text());
        Float bonusPrice = parseFloat(tds.get(1).text());
        Float normalPrice = parseFloat(tds.get(2).text());
        MensaMealData meal = new MensaMealData(title, -1, -1, -1, new AllergyCodeSet(), Collections.emptySet());
        ArrayList<MensaMealData> meals = new ArrayList<>(3);
        meals.add(meal);

        return new MensaCategoryData("Menü " + menuNumber, meals, bonusPrice, -1, normalPrice, Collections.emptySet());
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

    private int monthStrToNum(String month) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM", Locale.GERMAN);
        return f.parse(month).get(ChronoField.MONTH_OF_YEAR);
    }

    private enum Weekday {
        MO, DI, MI, DO, FR, SA, SO;
    }
}
