package jkumensa.parser.jku;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jkumensa.parser.data.CategoryData;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.MensaDayData;
import jkumensa.parser.khg.KhgMensaParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {
    public static void main(String[] args) throws IOException {
        khg();
    }

    private static void jku() throws IOException {
        Document doc = Jsoup.connect("http://menu.mensen.at/index/index/locid/1").get();
        Map<JkuMensaParser.MensaSubType, List<MensaDayData>> p = new JkuMensaParser().parse(doc);

        printMensaDay(p.get(JkuMensaParser.MensaSubType.CHOICE));
        //printMensaDay(a.get(JkuMensaParser.MensaSubType.CLASSIC));
    }

    private static void khg() throws IOException {
        Document doc = Jsoup.connect("https://www.dioezese-linz.at/institution/8075/essen/menueplan").get();
        KhgMensaParser p = new KhgMensaParser();
        printMensaDay(p.parse(doc));
    }

    private static void printMensaDay(List<MensaDayData> ds) {
        for (MensaDayData day : ds) {
            System.out.println(day.getDate());
            for (CategoryData cat : day.getCategories()) {
                System.out.println("\t" + cat.getTitle() + " [" + cat.getPriceGuest() + '/' + cat.getPriceStudent() + '/' + cat.getPriceStudentBonus() + "] ");

                for (MealData m : cat.getMeals()) {
                    System.out.println("\t\t\t" + m.getTitle() + " [" + m.getPriceGuest() + '/' + m.getPriceStudent() + '/' + m.getPriceStudentBonus() + "] " + m.getAllergyCodes() + " " + m.getFoodCharacteristics());
                }
            }
        }
    }
}
