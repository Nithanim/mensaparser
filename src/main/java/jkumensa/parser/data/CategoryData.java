package jkumensa.parser.data;

import java.util.List;
import java.util.Set;
import jkumensa.parser.i.Category;
import jkumensa.parser.i.FoodCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryData implements Category {
    String title;

    List<MealData> meals;

    float priceStudentBonus;
    float priceStudent;
    float priceGuest;
    
    Set<FoodCharacteristic> attachments;
}
