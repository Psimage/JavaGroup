package my.javagroup;

import sun.org.mozilla.javascript.internal.ast.ThrowStatement;

import javax.imageio.ImageIO;
import javax.management.remote.rmi._RMIConnection_Stub;
import java.awt.*;
import java.awt.font.ImageGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Admin
 * Date: 10.12.11
 * Time: 0:57
 */
public class ResourceManager {
    private static ResourceManager ourInstance = new ResourceManager();
    private Map<String, BufferedImage> res = new HashMap<>();

    public static ResourceManager getInstance() {
        return ourInstance;
    }

    private ResourceManager() {
    }

    public static BufferedImage loadImage(String path) {
        URL url = ResourceManager.class.getResource(path);
        System.out.println();
        BufferedImage img = null;

        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public void addImage(String name, BufferedImage img) {
        res.put(name, img);
    }

    public BufferedImage getImage(String name) {
        BufferedImage img = res.get(name);
        if(img == null) {
            System.out.println("Can't find resouece: " + name);
            System.out.println(res);
            throw new NullPointerException();
        }
        return img.getSubimage(0, 0, img.getWidth(), img.getHeight());
    }

    public int getCount() { return res.size(); }
}
