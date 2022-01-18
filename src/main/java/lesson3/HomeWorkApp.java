package lesson3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class HomeWorkApp {
    public static void main(String[] args) {

        List<String> wordList = new ArrayList<>(Arrays.asList(
                "Стол",
                "Стул",
                "Дом",
                "Печь",
                "Крыша",
                "Окно",
                "Кресло",
                "Кровать",
                "Стул",
                "Стекло",
                "Лестница",
                "Печь",
                "Лошадь",
                "Кресло",
                "Стул",
                "Дом")
        );

        HashSet<String> uniqWords = new HashSet<>(wordList);
        System.out.println(uniqWords);

        for(String val : uniqWords) {
            System.out.printf("%s: %d\n", val, wordList.stream().filter(s -> s.equals(val)).count());
        }

        PhoneCatalog catalog = new PhoneCatalog();

        catalog.add("Иванов", "880012345600");
        catalog.add("Иванов", "880012345601");
        catalog.add("Иванов", "880012345601");
        catalog.add("Петров", "880012345602");
        catalog.add("Петров", "880012345605");
        catalog.add("Петров", "880012345606");
        catalog.add("Сидоров", "880012345603");
        catalog.add("Васин", "880012345604");

        System.out.println(catalog.get("Иванов"));
        System.out.println(catalog.get("Васин"));
        System.out.println(catalog.get("Петров"));
        System.out.println(catalog.get("Жижин"));
    }
}
