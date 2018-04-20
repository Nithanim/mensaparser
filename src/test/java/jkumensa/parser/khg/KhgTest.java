package jkumensa.parser.khg;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jkumensa.api.AllergyCodeSet;
import jkumensa.api.data.MensaCategoryData;
import jkumensa.api.data.MensaMealData;
import jkumensa.parser.data.MensaDayData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class KhgTest {

    @Test
    public void D2018_04_11() throws IOException {
        Document doc = Jsoup.parse(KhgTest.class.getClassLoader().getResourceAsStream("html/khg_2018-04-11.html"), "UTF-8", "");
        KhgMensaParser p = new KhgMensaParser();
        List<MensaDayData> daysActual = p.parse(doc);
    }

    @Test
    public void D2017_12_11() throws IOException {
        Document doc = Jsoup.parse(KhgTest.class.getClassLoader().getResourceAsStream("html/khg_2017-12-11.html"), "UTF-8", "");
        KhgMensaParser p = new KhgMensaParser();
        List<MensaDayData> daysActual = p.parse(doc);
        compare(
            Arrays.asList(
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 11),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Apfel-Curry Suppe, pikanter Hirseauflauf mit Tomatensauce und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Apfel-Curry Suppe, Putenfilet in Kürbiskernpanade mit Petersilerdäpfel und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 12),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Erdäpfelsuppe, Thunfisch oder Gemüselasagne mit Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Erdäpfelsuppe. Züricher Geschnetzeltes vom Schwein mit Reis und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 13),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Hafer-Lauchsuppe, Spaghetti mit Linsen-Gemüse.Bolognese und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Haer-Lauchsuppe, Montafoner Hendlfilet mit Spinatspätzle und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 14),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Buchweizen-Frittatensuppe, Topfennockerl mit Apfelmus", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Buchweizen-Frittatensuppe, gekochtes Rindfleisch mit Semmelkren und Erdäpfelschmarren", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 15),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Dinkel-Grießschöberlsuppe, Krautfleckerl mit Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Dinkel-Grießschöberlsuppe. Chilli con Carne mit Gebäck", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                )
            ),
            daysActual
        );
    }

    @Test
    public void D2017_12_18() throws IOException {
        Document doc = Jsoup.parse(KhgTest.class.getClassLoader().getResourceAsStream("html/khg_2017-12-18.html"), "UTF-8", "");
        KhgMensaParser p = new KhgMensaParser();
        List<MensaDayData> daysActual = p.parse(doc);
        compare(
            Arrays.asList(
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 18),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Kohlsuppe, Gemüse-Mozzarellstrudel mit Tomatenragout und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Kohlsuppe, Wiener Schnitzel vom österr. Schwein mit Petersilerdäpfel und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 3",
                            Arrays.asList(
                                new MensaMealData("Mascarpone-Orangencreme", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            -1, -1, 1.3f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 19),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Kürbiskernknödelsuppe, Spinat-Nudelauflauf mit Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Kürbiskernknödelsuppe, österr. Brathendl mit Reis und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 3",
                            Arrays.asList(
                                new MensaMealData("Topfen-Pfirsichstrudel", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            -1, -1, 1.3f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 20),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Karotten-Ingwersuppe, Dinkelreislaibchen mit Kohlrabiragout und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Karotten-Ingwersuppe, österr. Puten-Wachauer-Rieslingschnitzel mit Vollkornhörnchen und Salat", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 21),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData("Ab heute ist unsere Mensa geschlossen!", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            4f, -1, 5.25f,
                            Collections.emptySet()
                        ),
                        new MensaCategoryData(
                            "Menü 2",
                            Arrays.asList(
                                new MensaMealData("Am 8. Jänner 2018 kochen wir wieder gerne für Sie!", -1, -1, -1, new AllergyCodeSet(), Collections.emptySet())
                            ),
                            5f, -1, 6.25f,
                            Collections.emptySet()
                        )
                    )
                ),
                new MensaDayData(
                    LocalDate.of(2017, Month.DECEMBER, 22),
                    Arrays.asList(
                        new MensaCategoryData(
                            "Menü 1",
                            Arrays.asList(
                                new MensaMealData(
                                    "Wir wünschen allen unseren Gästen ein frohes Weihnachtsfest und alles Gute für das Jahr 2018",
                                    -1, -1, -1,
                                    new AllergyCodeSet(),
                                    Collections.emptySet()
                                )
                            ),
                            -1, -1, -1,
                            Collections.emptySet()
                        )
                    )
                )
            ),
            daysActual
        );
    }

    private void compare(List<MensaDayData> daysExpected, List<MensaDayData> daysActual) {
        for (int i = 0; i < daysExpected.size(); i++) {
            MensaDayData dayExpected = daysExpected.get(i);
            MensaDayData dayActual = daysActual.get(i);

            Assert.assertEquals(dayExpected.getDate(), dayActual.getDate());

            int j = 0;
            try {
                for (; j < dayExpected.getCategories().size(); j++) {
                    MensaCategoryData catExpected = dayExpected.getCategories().get(j);
                    MensaCategoryData catActual = dayActual.getCategories().get(j);

                    assertCat(catExpected, catActual);
                }
            } catch (Throwable err) {
                throw new AssertionError("Error asserting Cat" + i, err);
            }
        }
    }

    private void assertCat(MensaCategoryData subExpected, MensaCategoryData subActual) throws AssertionError {
        Assert.assertEquals(subExpected.getTitle(), subActual.getTitle());
        Assert.assertEquals(subExpected.getPriceGuest(), subActual.getPriceGuest(), 0.1);
        Assert.assertEquals(subExpected.getPriceStudent(), subActual.getPriceStudent(), 0.1);
        Assert.assertEquals(subExpected.getPriceStudentBonus(), subActual.getPriceStudentBonus(), 0.1);
        Assert.assertEquals(subExpected.getFoodCharacteristics(), subActual.getFoodCharacteristics());

        int i = 0;
        try {
            for (; i < subExpected.getMeals().size(); i++) {
                MensaMealData mealExpected = subExpected.getMeals().get(i);
                MensaMealData mealActual = subActual.getMeals().get(i);

                assertMeal(mealExpected, mealActual);
            }
        } catch (Throwable err) {
            throw new AssertionError("Error asserting Meal" + i, err);
        }
    }

    private void assertMeal(MensaMealData mealExpected, MensaMealData mealActual) {
        Assert.assertEquals(mealExpected.getTitle(), mealActual.getTitle());
        Assert.assertEquals(mealExpected.getPriceStudentBonus(), mealActual.getPriceStudentBonus(), 0.1);
        Assert.assertEquals(mealExpected.getPriceStudent(), mealActual.getPriceStudent(), 0.1);
        Assert.assertEquals(mealExpected.getPriceGuest(), mealActual.getPriceGuest(), 0.1);
        Assert.assertEquals(mealExpected.getFoodCharacteristics(), mealActual.getFoodCharacteristics());
        Assert.assertEquals(mealExpected.getAllergyCodes(), mealActual.getAllergyCodes());
    }
}
