package jkumensa;

import java.io.IOException;
import java.util.List;
import jkumensa.parser.JkuMensaParser;
import jkumensa.parser.data.CategoryData;
import jkumensa.parser.data.MealData;
import jkumensa.parser.data.MensaDayData;
import jkumensa.parser.data.SubCategoryData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("http://menu.mensen.at/index/index/locid/1").get();
        List<MensaDayData> a = new JkuMensaParser().parse(doc);
        for(MensaDayData day : a) {
            System.out.println(day.getDate());
            for(CategoryData cat : day.getCategories()) {
                System.out.println("\t" + cat.getTitle());
                
                for(SubCategoryData sub : cat.getSubCategories()) {
                    System.out.println("\t\t" + sub.getTitle() + " [" + sub.getPriceGuest() + '/' + sub.getPriceStudent() + '/' + sub.getPriceStudentBonus() + "] " + sub.getAttachments());
                    
                    for(MealData m : sub.getMeals()) {
                        System.out.println("\t\t\t" + m.getTitle() + " [" + m.getPriceGuest() + '/' + m.getPriceStudent() + '/' + m.getPriceStudentBonus() + "] " + m.getAllergyCodes() + " " + m.getAttachments());
                    }
                }
            }
        }

        /*try (DbConnector dbc = new DbConnector()) {
            dbc.connect();

            MensaDayData day = a.get(0);
            String mensa = "JKU";

            //dbc.dsl().transaction(() -> { //Not working for single user sqlite
            MensaRecord mensaRecord = dbc.dsl().insertInto(MENSA)
                .columns(MENSA.NAME, MENSA.CAT)
                .values(mensa, day.getCategories().get(1).getTitle())
                .returning(MENSA.ID)
                .fetchOne();

            int id = mensaRecord.getId();
            //});
        }*/
    }
}
