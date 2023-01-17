package cli.utility;

import java.util.HashMap;

public class DataCache {
    private static HashMap<String, String> cacheSingularPluralData = new HashMap<>();

    public static void insertIntoCache(String key, String value) {
        boolean test = !cacheSingularPluralData.containsKey(key);
        if(!cacheSingularPluralData.containsKey(key))
            cacheSingularPluralData.put(key, value);
        boolean test2 = true;

    }

    public static String getFromCache(String key) {
        if(!cacheSingularPluralData.containsKey(key)) {
            return "Not found";
        }
        return cacheSingularPluralData.get(key);
    }
}
