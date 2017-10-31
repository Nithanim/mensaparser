package jkumensa.parser.data;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import jkumensa.parser.i.Meal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealData implements Meal {
    String title;

    float priceGuest;
    float priceStudent;
    float priceStudentBonus;

    Set<AllergyCode> allergyCodes;
    Set<FoodCharacteristic> attachments;
}
