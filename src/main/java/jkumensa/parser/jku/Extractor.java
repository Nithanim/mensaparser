package jkumensa.parser.jku;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.api.MensaFoodCharacteristic;
import jkumensa.api.AllergyCodeSet;
import jkumensa.parser.ex.MensaAllergyCodeParsingException;
import jkumensa.parser.ex.MensaFoodCharacteristicParsingException;
import org.jsoup.nodes.Element;

public class Extractor {
    public static final Pattern ALLERGY_PATTERN = Pattern.compile("(\\(\\s*[A-Z](?:[\\s;,]+[A-Z])*\\s*\\))");

    public static AllergyCodeSet parseAllergyCodes(String allergyString) {
        AllergyCodeSet s = new AllergyCodeSet();
        for (int i = 0; i < allergyString.length(); i++) {
            char c = allergyString.charAt(i);
            if ('A' <= c && c <= 'Z') {
                try {
                    s.add(c);
                } catch (IllegalArgumentException ex) {
                    throw new MensaAllergyCodeParsingException(ex);
                }
            }
        }
        return s;
    }

    public static Set<MensaFoodCharacteristic> foodCharacteristicFromImg(List<Element> imgs) {
        return imgs.stream()
            .map(img -> foodCharacteristicFromImg(img))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(MensaFoodCharacteristic.class)));
    }

    public static MensaFoodCharacteristic foodCharacteristicFromImg(Element img) {
        if (!img.tagName().equals("img")) {
            throw new IllegalArgumentException("Give element is not an img tag " + img);
        }

        String alt = img.attr("alt");
        if (alt.contains("Vegetarisch")) {
            return MensaFoodCharacteristic.VEGETARIAN;
        } else if (alt.contains("Vegan")) {
            return MensaFoodCharacteristic.VEGAN;
        } else if (alt.contains("Fisch")) {
            return MensaFoodCharacteristic.FISH;
        } else if (alt.contains("Nachhaltige Fischerei")) {
            return MensaFoodCharacteristic.MSC;
        } else if (alt.contains("Brainfood")) {
            return MensaFoodCharacteristic.BRAINFOOD;
        }
        throw new MensaFoodCharacteristicParsingException("No known type was found for " + img);
    }
}
