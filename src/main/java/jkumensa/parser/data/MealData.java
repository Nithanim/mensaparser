package jkumensa.parser.data;

import java.util.Set;
import jkumensa.parser.i.AllergyCode;
import jkumensa.parser.i.FoodCharacteristic;
import jkumensa.parser.i.Meal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealData implements Meal {
    String title;

    float priceStudentBonus;
    float priceStudent;
    float priceGuest;

    Set<AllergyCode> allergyCodes;
    Set<FoodCharacteristic> foodCharacteristics;
}
