package jkumensa.parser;

import java.time.LocalDate;
import java.util.List;
import jkumensa.api.data.MensaCategoryData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensaDayData implements MensaDay {
    LocalDate date;
    
    List<MensaCategoryData> categories;
}
