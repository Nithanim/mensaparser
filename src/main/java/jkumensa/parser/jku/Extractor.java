package jkumensa.parser.jku;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jkumensa.api.AllergyCodeSet;
import jkumensa.api.MensaFoodCharacteristic;
import jkumensa.parser.ex.MensaAllergyCodeParsingException;
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
            throw new IllegalArgumentException("Given element is not an img tag " + img);
        }

        String alt = img.attr("alt").toLowerCase();
        if (alt.contains("vegetarisch")) {
            return MensaFoodCharacteristic.VEGETARIAN;
        } else if (alt.equals("asc")) {
            return MensaFoodCharacteristic.ASC;
        } else if (alt.equals("msc")) {
            return MensaFoodCharacteristic.MSC;
        } else if (alt.contains("vegan")) {
            return MensaFoodCharacteristic.VEGAN;
        } else if (alt.contains("fisch")) {
            return MensaFoodCharacteristic.FISH;
        } else if (alt.contains("nachhaltige fischerei")) {
            return MensaFoodCharacteristic.MSC;
        } else if (alt.contains("brainfood")) {
            return MensaFoodCharacteristic.BRAINFOOD;
        } else if (alt.contains("umweltzeichen")) {
            return MensaFoodCharacteristic.AUSTRIAN_ENVIRONMENT;
        } else {
            return MensaFoodCharacteristic.UNKNOWN;
        }
    }

    public static String extractAndRemoveAllergyCodes(String fulltext, AllergyCodeSet allergyCodes) {
        StringBuilder textWithoutAllergyCodes = new StringBuilder();
        int pos = 0;
        Matcher m = ALLERGY_PATTERN.matcher(fulltext);
        while (m.find()) {
            //Build the text of the meal without the allergy symbols
            String allergyString = m.group(1);
            if (pos != m.start()) {
                textWithoutAllergyCodes.append(fulltext.substring(pos, m.start()).trim());
                textWithoutAllergyCodes.append(' ');
                pos = m.end();
            }

            allergyCodes.addAll(Extractor.parseAllergyCodes(allergyString));
        }
        if (pos < fulltext.length()) {
            textWithoutAllergyCodes.append(fulltext.substring(pos, fulltext.length()).trim());
        }
        String title = textWithoutAllergyCodes.toString().trim();
        return title;
    }
}
