package my.javagroup.core;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 19:23
 */

import my.javagroup.ResourceManager;

import java.awt.*;
import java.io.Serializable;

//todo: Deprecated.Refactor!
public abstract class DynamicObject extends Component implements Serializable {
    protected static ResourceManager rm = ResourceManager.getInstance();
}