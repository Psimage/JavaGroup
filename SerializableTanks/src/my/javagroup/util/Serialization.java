package my.javagroup.util;

import my.javagroup.TanksApplication;
import my.javagroup.core.DynamicObject;

import java.io.*;

/**
 * User: Admin
 * Date: 11.12.11
 * Time: 1:36
 */
public class Serialization {
    public static <T> void serializeObjectToFile(String path, T obj) {
        //It's bad... :( But good for my application :P
        TanksApplication.changedByMe = true;

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Can automatically cast to preferred type (when side effect occurs)
    public static <T> T deserializeObjectFromFile(String path) {
        T obj = null;
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            obj = (T)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
