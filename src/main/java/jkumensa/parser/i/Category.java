package jkumensa.parser.i;

import java.util.List;

public interface Category {
    String getTitle();
    
    List<? extends SubCategory> getSubCategories();
}
