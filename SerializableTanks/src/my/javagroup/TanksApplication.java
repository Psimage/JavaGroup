package my.javagroup;

import my.javagroup.core.DynamicObject;
import my.javagroup.deprecated.Game;
import my.javagroup.gametypes.Field;
import my.javagroup.gametypes.Tank;
import my.javagroup.util.Serialization;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 19:50
 */
public class TanksApplication extends JFrame {
    private Field field;

    private static final int TANK_COUNT = 20;

    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;

    //only for debug and testing
    private static final int ATLAS_TILE_WIDTH = 24;
    private static final int ATLAS_TILE_HEIGHT = 24;

    public static final String TEMP_OBJ_FILE = "Temp.object";

    TanksApplication () {
        super("Serialization & Deserialization");
        loadResources();
        init();
    }

    public static void main(String[] args) {
        TanksApplication app = new TanksApplication();

        app.loadResources();
        app.init();

        for(int i = 0; i < TANK_COUNT; i++) {
            Tank tank = new Tank();
            do {
                tank.setLocation(Rand.getInstance().nextInt(WINDOW_WIDTH - ATLAS_TILE_WIDTH),
                        Rand.getInstance().nextInt(WINDOW_HEIGHT - ATLAS_TILE_HEIGHT));
            }
            while(app.getField().getComponentsAt(tank.getBounds()) != null);
            app.add(tank);
        }

        app.setVisible(true);
    }

    private void init() {
        field = new Field();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add game field
        setContentPane(field);

        setLayout(null);
        setResizable(false);

        createMenuBar();

        field.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);

    }

    public void loadResources() {
        ResourceManager rm = ResourceManager.getInstance();

        rm.addImage("default", ResourceManager.loadImage("resources/default.bmp"));
        rm.addImage("background_map", ResourceManager.loadImage("resources/map.jpg"));

        BufferedImage atlas = ResourceManager.loadImage("resources/1.png");
        final int COLUMNS_IN_ATLAS = atlas.getWidth()/ATLAS_TILE_WIDTH;
        final int ROWS_IN_ATLAS = atlas.getHeight()/ATLAS_TILE_HEIGHT;

        int i = 0;

        //todo: in atlas we have some emply tiles. Don't forget
        for(int r = 0; r < ROWS_IN_ATLAS; r++) {
            for(int c = 0; c < COLUMNS_IN_ATLAS ; c++) {
                rm.addImage(Integer.toString(i++), atlas.getSubimage(c*ATLAS_TILE_WIDTH,
                        r*ATLAS_TILE_HEIGHT, ATLAS_TILE_WIDTH, ATLAS_TILE_HEIGHT));
            }
        }
    }

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("Main");

        final JMenuItem menuItem1 = new JMenuItem("Serialize");
        menuItem1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                field.addSelectedComponentToFile(TanksApplication.TEMP_OBJ_FILE);
            }
        });

        final JMenuItem menuItem2 = new JMenuItem("Deserialize");
        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.addComponentFromFile(TanksApplication.TEMP_OBJ_FILE);
            }
        });

        menu.add(menuItem2);
        menu.add(menuItem1);
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if(field.hasSelectedComponent()) {
                    menuItem1.setEnabled(true);
                }
                else {
                    menuItem1.setEnabled(false);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {}

            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    public Field getField() {
        return field;
    }

}