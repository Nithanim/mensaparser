package jkumensa.parser.i;

import java.time.LocalDate;
import java.util.List;
import jkumensa.api.MensaCategory;

public interface MensaDay {
    LocalDate getDate();

    List<? extends MensaCategory> getCategories();
}
