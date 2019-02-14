package jkumensa.parser.jku;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class ParsingSuccessfulTest {
    @Test
    public void test() throws IOException {
        Document doc = Jsoup.parse(ParsingSuccessfulTest.class.getClassLoader().getResourceAsStream("html/mensa_2019-02-14.html"), "UTF-8", "");
        JkuMensaParser p = new JkuMensaParser();
        p.parse(doc);
    }
    
    @Test
    public void testPrevious() throws IOException {
        Document doc = Jsoup.parse(ParsingSuccessfulTest.class.getClassLoader().getResourceAsStream("html/mensa_2018-11-22.html"), "UTF-8", "");
        JkuMensaParser p = new JkuMensaParser();
        p.parse(doc);
    }
}
