package jkumensa.parser.data;

import java.util.List;
import jkumensa.parser.i.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryData implements Category {
    String title;
    
    List<SubCategoryData> subCategories;
}
