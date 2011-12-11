package my.javagroup.deprecated;

import java.awt.*;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 20:03
 */

//Deprecated
public class Game {
    private static Game ourInstance = new Game();
    private static long time = System.currentTimeMillis();

    public static Game getInstance() {
        return ourInstance;
    }

    private Game() {
    }

    public void render(Graphics g) {
        //todo: implement
    }

    public void update() {
        time = System.currentTimeMillis() - time;
        float dt = time/1000;
        //todo: implement update(dt)
    }
}
