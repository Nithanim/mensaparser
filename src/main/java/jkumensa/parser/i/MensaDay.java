package jkumensa.parser.i;

import java.time.LocalDate;
import java.util.List;

public interface MensaDay {
    LocalDate getDate();

    List<? extends Category> getCategories();
}
