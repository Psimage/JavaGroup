package my.javagroup;

import java.util.Random;

/**
 * User: Admin
 * Date: 10.12.11
 * Time: 2:52
 */
public class Rand {
    private static Random ourInstance = new Random(System.currentTimeMillis());

    public static Random getInstance() {
        return ourInstance;
    }

    private Rand() {
    }
}
