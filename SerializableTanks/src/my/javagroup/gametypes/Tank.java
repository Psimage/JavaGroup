package my.javagroup.gametypes;

import my.javagroup.Rand;
import my.javagroup.core.DynamicObject;

import java.awt.*;
import java.io.Serializable;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 19:43
 */
public class Tank extends DynamicObject implements Serializable {
    //Image is not serializable
    private transient Image img = null;
    //That's why we store only name of asosiated resource
    //and loading it from his ResourceManager
    private String imgName = "default";

    public Tank() {
        // -2 because of default and map resources
        int n = Rand.getInstance().nextInt(rm.getCount() - 2);
        imgName = Integer.toString(n);
        img = rm.getImage(imgName);
        setSize(img.getWidth(null), img.getHeight(null));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(img, 0, 0, null);
    }

    //Serialization
    private Object readResolve() {
        //if static field is not serialized, there is no reason to do this
        //rm = ResourceManager.getInstance();
        img = rm.getImage(imgName);
        return this;
    }
}
