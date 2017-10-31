package jkumensa.parser.i;

import java.util.List;
import java.util.Set;

public interface SubCategory extends Priced {
    String getTitle();

    List<? extends Meal> getMeals();
    
    Set<? extends FoodCharacteristic> getAttachments();
}
