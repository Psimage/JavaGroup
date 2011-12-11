package my.javagroup.gametypes;

import my.javagroup.Rand;
import my.javagroup.ResourceManager;
import my.javagroup.core.DynamicObject;
import my.javagroup.util.ImageRender;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.Serializable;
import java.security.PrivateKey;

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
    //ResourceManager is singleton!
    private transient ResourceManager rm = ResourceManager.getInstance();

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
        rm = ResourceManager.getInstance();
        img = rm.getImage(imgName);
        return this;
    }
}
