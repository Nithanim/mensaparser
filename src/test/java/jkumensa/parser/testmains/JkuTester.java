package jkumensa.parser.testmains;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jkumensa.parser.MensaDayData;
import jkumensa.parser.jku.*;
import static jkumensa.parser.testmains.TesterHelper.printMensaDay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JkuTester {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("http://menu.mensen.at/index/index/locid/1").get();
        Map<JkuMensaParser.MensaSubType, List<MensaDayData>> p = new JkuMensaParser().parse(doc);

        printMensaDay(p.get(JkuMensaParser.MensaSubType.CLASSIC));
        printMensaDay(p.get(JkuMensaParser.MensaSubType.CHOICE));
    }
}
