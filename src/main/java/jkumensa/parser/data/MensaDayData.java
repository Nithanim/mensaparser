package jkumensa.parser.data;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jkumensa.parser.i.MensaDay;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensaDayData implements MensaDay {
    LocalDate date;
    
    List<CategoryData> categories;
}
