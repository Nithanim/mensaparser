package jkumensa.parser.testmains;

import java.util.List;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import jkumensa.parser.MensaDayData;

class TesterHelper {
    static void printMensaDay(List<MensaDayData> ds) {
        for (MensaDayData day : ds) {
            System.out.println(day.getDate());
            for (MensaCategoryData cat : day.getCategories()) {
                System.out.println("\t" + cat.getTitle() + " [" + cat.getPriceGuest() + '/' + cat.getPriceStudent() + '/' + cat.getPriceStudentBonus() + "] ");

                for (MensaMealData m : cat.getMeals()) {
                    System.out.println("\t\t\t" + m.getTitle() + " [" + m.getPriceGuest() + '/' + m.getPriceStudent() + '/' + m.getPriceStudentBonus() + "] " + m.getAllergyCodes() + " " + m.getFoodCharacteristics());
                }
            }
        }
    }
}
