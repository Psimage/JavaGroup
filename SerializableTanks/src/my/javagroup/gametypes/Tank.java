package my.javagroup.gametypes;

import my.javagroup.Rand;
import my.javagroup.ResourceManager;
import my.javagroup.core.DynamicObject;
import my.javagroup.util.ImageRender;
import sun.awt.windows.ThemeReader;

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
    //static == transient?
    private static ResourceManager rm = ResourceManager.getInstance();

    private boolean destroyed = false;
    private boolean created = false;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DIES = 1;
    public static final int STATE_CREATES = 2;
    private int state = STATE_NORMAL;

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

    public void destroy() {
        state = STATE_DIES;
        Thread t = new Thread()  {
            private double tsp = 0;
            public void run() {
                while(tsp <= 1.0) {
                    img = ImageRender.setSemiTransparency(img, tsp);
                    repaint();

                    tsp += 0.01;

                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                destroyed = true;
            }
        };

        t.start();
    }

    public void create() {
        state = STATE_CREATES;
        created = false;
        Thread t = new Thread()  {
            private double tsp = 1.0;
            public void run() {
                while(tsp >= 0) {
                    img = ImageRender.setSemiTransparency(rm.getImage(imgName), tsp);
                    repaint();

                    tsp -= 0.01;

                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                state = STATE_NORMAL;
                created = true;
            }
        };

        t.start();
    }

    //Serialization
    private Object readResolve() {
        //if static field is not serialized, there is no reason to do this
        //rm = ResourceManager.getInstance();
        img = rm.getImage(imgName);
        return this;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isCreated() {
        return created;
    }

    public int getState() {
        return state;
    }
}
