package lesson3;

import java.util.*;

public class PhoneCatalog {

    private final HashMap<String, HashSet<String>> phones;

    public PhoneCatalog() {
        phones = new HashMap<>();
    }

    public void add(String name, String phone) {

        HashSet<String> phonesSet = phones.get(name);

        if (phonesSet == null) {
            phonesSet = new HashSet<>();
            phonesSet.add(phone);
            phones.put(name, phonesSet);
        } else {
            phonesSet.add(phone);
        }
    }

    public List<Object> get(String name) {

        HashSet<String> phonesSet = phones.get(name);
        return phonesSet == null ? new ArrayList<>() : Arrays.asList(phonesSet.toArray());
    }
}
