package my.javagroup.deprecated;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 18:51
 */

/**
 * All game core must implement this interface
 */
public interface Updatable {
    /**
     * Called every game tick
     * @param dt delta time in seconds. The time elapsed between the previous and the current game tick.
     * @return if <b>false</b> the program will exit, else - continue.
     */
    abstract public boolean update(float dt);
}
