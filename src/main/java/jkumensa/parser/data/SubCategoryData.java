package jkumensa.parser.data;

import java.util.List;
import java.util.Set;
import jkumensa.parser.i.FoodCharacteristic;
import jkumensa.parser.i.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryData implements SubCategory {
    String title;

    List<MealData> meals;

    float priceGuest;
    float priceStudent;
    float priceStudentBonus;
    
    Set<FoodCharacteristic> attachments;
}
