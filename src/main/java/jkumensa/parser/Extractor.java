package jkumensa.parser;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.parser.ex.MensaAllergyCodeParsingException;
import jkumensa.parser.ex.MensaFoodCharacteristicParsingException;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import org.jsoup.nodes.Element;

public class Extractor {
    public static final Pattern ALLERGY_PATTERN = Pattern.compile("(\\(\\s*[A-Z](?:[\\s;,]+[A-Z])*\\s*\\))");

    public static EnumSet<AllergyCode> parseAllergyCodes(String allergyString) {
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

    public static Set<FoodCharacteristic> foodCharacteristicFromImg(List<Element> imgs) {
        return imgs.stream()
            .map(img -> foodCharacteristicFromImg(img))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(FoodCharacteristic.class)));
    }

    public static FoodCharacteristic foodCharacteristicFromImg(Element img) {
        if (!img.tagName().equals("img")) {
            throw new IllegalArgumentException("Give element is not an img tag " + img);
        }

        String alt = img.attr("alt");
        if (alt.contains("Vegetarisch")) {
            return FoodCharacteristic.VEGETARIAN;
        } else if (alt.contains("Vegan")) {
            return FoodCharacteristic.VEGAN;
        } else if (alt.contains("Fisch")) {
            return FoodCharacteristic.FISH;
        } else if (alt.contains("Nachhaltige Fischerei")) {
            return FoodCharacteristic.MSC;
        } else if (alt.contains("Brainfood")) {
            return FoodCharacteristic.BRAINFOOD;
        }
        throw new MensaFoodCharacteristicParsingException("No known type was found for " + img);
    }
}
