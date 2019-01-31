package jkumensa.parser.testmains;

import java.io.IOException;
import jkumensa.parser.khg.KhgMensaParser;
import static jkumensa.parser.testmains.TesterHelper.printMensaDay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class KhgTester {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.dioezese-linz.at/institution/8075/essen/menueplan").get();
        KhgMensaParser p = new KhgMensaParser();
        printMensaDay(p.parse(doc));
    }
}
