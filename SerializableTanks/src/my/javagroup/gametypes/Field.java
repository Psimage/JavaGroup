package my.javagroup.gametypes;

import com.sun.org.apache.bcel.internal.generic.IFEQ;
import my.javagroup.ResourceManager;
import my.javagroup.TanksApplication;
import my.javagroup.core.DynamicObject;
import my.javagroup.util.Serialization;
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
    private DynamicObject selected = null;
    private Image backgroundImg = null;

    public Field() {
        backgroundImg = ResourceManager.getInstance().getImage("background_map");

        MouseAdapter ma = new MouseAdapter() {
            Point draggedFrom = new Point();

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    Point mouseLoc = e.getPoint();
                    Component c = getComponentAt(mouseLoc);

                    //Don't touch swing! Only my classes
                    if(c != Field.this && c instanceof DynamicObject) {
                        selected = (DynamicObject)c;
                        draggedFrom.setLocation(mouseLoc.x - selected.getX(), e.getPoint().y - selected.getY());
                        //todo: It can hurt swing components.
                        Field.this.setComponentZOrder(selected, 0);
                    }
                    else {
                        selected = null;
                    }

                    //todo: optimization to repaint()
                    repaint();

                    //double-click
                    if(e.getClickCount() == 2 && selected != null) {
                        addSelectedComponentToFile(TanksApplication.TEMP_OBJ_FILE);
                    }
                }
                else if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2) {
                    addComponentFromFile(TanksApplication.TEMP_OBJ_FILE);
                }
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
                    DynamicObject[] array = getComponentsAt(selected.getBounds());
                    if(array.length > 1) {
                        //Destroy all objects!
                        for(DynamicObject obj : array) {
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
        remove(selected);
        repaint();
    }

    public void addComponentFromFile(String path) {
        DynamicObject obj = null;
        obj = Serialization.deserializeObjectFromFile(path);
        add(obj);
        repaint();
    }

    @Override
    public Component add(Component comp) {
        if(DynamicObject.class.isInstance(comp)) {
            DynamicObject[] array = getComponentsAt(comp.getBounds());
            if(array != null) {
                //Destroy all objects!
                for(DynamicObject obj : array) {
                    destroyDynamicObject(obj);
                }
                repaint();
                return null;
            }
        }

        return super.add(comp);
    }

    public void destroyDynamicObject(DynamicObject obj) {
        //todo: in far future would be cool with animation
        remove(obj);
        if(obj == selected) {
            selected = null;
        }
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
    public DynamicObject[] getComponentsAt(Rectangle rect) {
        ArrayList<DynamicObject> list = new ArrayList<>();
        DynamicObject[] array = new DynamicObject[0];

        for(Component c : getComponents()) {
            if(DynamicObject.class.isInstance(c)) {
                if(c.getBounds().intersects(rect)) {
                    list.add((DynamicObject)c);
                }
            }
        }

        return list.isEmpty() ? null : list.toArray(array);
    }

    public DynamicObject getSelectedComponent() {
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
