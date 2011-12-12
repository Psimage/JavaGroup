package my.javagroup.gametypes;

import com.sun.org.apache.bcel.internal.generic.IFEQ;
import my.javagroup.ResourceManager;
import my.javagroup.TanksApplication;
import my.javagroup.util.Serialization;
import sun.awt.windows.ThemeReader;
import sun.font.TrueTypeFont;
import sun.org.mozilla.javascript.internal.ast.NewExpression;
import sun.org.mozilla.javascript.internal.ast.Yield;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * User: Admin
 * Date: 10.12.11
 * Time: 3:11
 */
public class Field extends Container {
    private Tank selected = null;
    private Image backgroundImg = null;

    public Field() {
        backgroundImg = ResourceManager.getInstance().getImage("background_map");

        MouseAdapter ma = new MouseAdapter() {
            Point draggedFrom = new Point();

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    mouseButton1Action(e);

                    //double-click
                    if(e.getClickCount() == 2 && selected != null) {
                        addSelectedComponentToFile(TanksApplication.TEMP_OBJ_FILE);
                    }
                }
                else if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2) {
                    addComponentFromFile(TanksApplication.TEMP_OBJ_FILE);
                }
            }

            private void mouseButton1Action(MouseEvent e) {
                Point mouseLoc = e.getPoint();
                Component c = getComponentAt(mouseLoc);

                //Don't touch swing! Only my classes
                if(c != Field.this && Tank.class.isInstance(c)) {
                    Tank tank = (Tank)c;
                    if(tank.getState() == Tank.STATE_NORMAL) {
                        selected = tank;
                        draggedFrom.setLocation(mouseLoc.x - selected.getX(), e.getPoint().y - selected.getY());
                        //todo: It can hurt swing components.
                        Field.this.setComponentZOrder(selected, 0);
                    }
                    else {
                        selected = null;
                    }
                }
                else {
                    selected = null;
                }

                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //todo: out of border logic. Refactor
                if(selected != null) {
                    int dx = e.getX() - draggedFrom.x;
                    int dy = e.getY() - draggedFrom.y;

                    if(dx >= 0 && (dx <= getWidth() - selected.getWidth())) {
                        selected.setLocation(dx, selected.getY());
                    }

                    if(dy >= 0 && (dy <= getHeight() - selected.getHeight())) {
                        selected.setLocation(selected.getX(), dy);
                    }

                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(selected != null) {
                    //always returns selected object
                    Tank[] array = getComponentsAt(selected.getBounds());
                    if(array.length > 1) {
                        //Destroy all objects!
                        for(Tank obj : array) {
                            destroyDynamicObject(obj);
                        }
                        repaint();
                    }
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void addSelectedComponentToFile(String path) {
        Serialization.serializeObjectToFile(path, selected);
        destroyDynamicObject(selected);
        repaint();
    }

    public void addComponentFromFile(String path) {
        final Tank obj = Serialization.deserializeObjectFromFile(path);
        obj.create();
        add(obj);

        Thread t = new Thread()  {
            public void run() {
                while(!obj.isCreated()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Tank[] array = getComponentsAt(obj.getBounds());
                if(array.length > 1) {
                    //Destroy all objects!
                    for(Tank obj1 : array) {
                        destroyDynamicObject(obj1);
                    }
                }
            }
        };

        t.start();
    }

    @Override
    public Component add(Component comp) {
        if(Tank.class.isInstance(comp)) {
            Tank tank = (Tank)comp;
            if(tank.getState() == Tank.STATE_NORMAL) {
                Tank[] array = getComponentsAt(tank.getBounds());
                if(array != null) {
                    //Destroy all objects!
                    for(Tank obj : array) {
                        destroyDynamicObject(obj);
                    }
                    repaint();
                    return null;
                }
            }
        }

        return super.add(comp);
    }

    public void destroyDynamicObject(final Tank obj) {
        //todo: in far future would be cool with animation
        obj.destroy();

        if(obj == selected) {
            selected = null;
            repaint();
        }

        Thread t = new Thread()  {
            public void run() {
                while(true) {
                    if(obj.isDestroyed()) {
                       remove(obj);
                       break;
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
    }

    @Override
    public void paint(Graphics g) {
        paintBackground(g);

        //Paint all components
        super.paint(g);

        if(selected != null) {
            paintSelectedComponent(g);
        }
    }

    public boolean hasSelectedComponent() {
        return selected != null;
    }

    private void paintBackground(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        AffineTransform temp = g2D.getTransform();
        g2D.scale(getWidth() / (double) backgroundImg.getWidth(null),
                getHeight() / (double) backgroundImg.getHeight(null));
        g2D.drawImage(backgroundImg, 0, 0, null);
        g2D.setTransform(temp);
    }

    private void paintSelectedComponent(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.YELLOW);
        g.drawRect(selected.getX(), selected.getY(), selected.getWidth(), selected.getHeight());
        g.setColor(c);
    }

    //Basic collision testing (rect vs rect)
    public Tank[] getComponentsAt(Rectangle rect) {
        ArrayList<Tank> list = new ArrayList<>();
        Tank[] array = new Tank[0];

        for(Component c : getComponents()) {
            if(Tank.class.isInstance(c)) {
                Tank tank = (Tank)c;
                if(tank.getState() == Tank.STATE_NORMAL) {
                    if(tank.getBounds().intersects(rect)) {
                        list.add(tank);
                    }
                }
            }
        }

        return list.isEmpty() ? null : list.toArray(array);
    }

    public Tank getSelectedComponent() {
        return selected;
    }

    @Override
    public void remove(Component comp) {
        if(comp == selected) {
            selected = null;
        }
        super.remove(comp);
    }
}
