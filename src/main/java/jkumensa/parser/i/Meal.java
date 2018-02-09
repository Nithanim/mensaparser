package jkumensa.parser.i;

import java.util.Set;

public interface Meal extends Priced {
    String getTitle();

    Set<AllergyCode> getAllergyCodes();

    Set<FoodCharacteristic> getFoodCharacteristics();
}
