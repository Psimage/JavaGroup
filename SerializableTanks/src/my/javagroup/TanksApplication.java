package my.javagroup;

import my.javagroup.core.DynamicObject;
import my.javagroup.deprecated.Game;
import my.javagroup.gametypes.Field;
import my.javagroup.gametypes.Tank;
import my.javagroup.util.Serialization;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * User: Admin
 * Date: 09.12.11
 * Time: 19:50
 */
public class TanksApplication extends JFrame {
    private DefaultFileMonitor fileMonitor;
    public static boolean changedByMe = false;

    private Field field;

    private static final int TANK_COUNT = 10;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 300;

    //only for debug and testing
    private static final int ATLAS_TILE_WIDTH = 24;
    private static final int ATLAS_TILE_HEIGHT = 24;

    public static final String TEMP_OBJ_FILE = "Temp.object";

    TanksApplication () {
        super("Serialization & Deserialization");
        loadResources();
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);

        //createMenuBar();

        //add game field
        field = new Field();
        setContentPane(field);

        field.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);

        createFileMonitor();
    }

    private void createFileMonitor() {
        FileSystemManager fsManager = null;
        try {
            fsManager = VFS.getManager();
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        FileObject file = null;
        try {
            file = fsManager.resolveFile(new File(TEMP_OBJ_FILE).getAbsolutePath());
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        fileMonitor = new DefaultFileMonitor(new FileListener() {
            @Override
            public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
                System.out.println("created");
            }

            @Override
            public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
                System.out.println("deleted");
            }

            @Override
            public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
                //System.out.println(String.format("File [%s] changed event from [%s]",
                //      fileChangeEvent.getFile().getName(), this));
                if(changedByMe == false)
                    field.addComponentFromFile(TEMP_OBJ_FILE);

                changedByMe = false;
            }
        });

        fileMonitor.addFile(file);
        fileMonitor.setChecksPerRun(1);
        fileMonitor.setDelay(100);
        fileMonitor.start();
    }

    private void loadResources() {
        ResourceManager rm = ResourceManager.getInstance();

        rm.addImage("default", ResourceManager.loadImage("resources/default.bmp"));
        rm.addImage("background_map", ResourceManager.loadImage("resources/map.jpg"));

        BufferedImage atlas = ResourceManager.loadImage("resources/1.png");
        final int COLUMNS_IN_ATLAS = atlas.getWidth()/ATLAS_TILE_WIDTH;
        final int ROWS_IN_ATLAS = atlas.getHeight()/ATLAS_TILE_HEIGHT;

        int i = 0;

        for(int r = 0; r < ROWS_IN_ATLAS; r++) {
            for(int c = 0; c < COLUMNS_IN_ATLAS ; c++) {
                rm.addImage(Integer.toString(i++), atlas.getSubimage(c*ATLAS_TILE_WIDTH,
                        r*ATLAS_TILE_HEIGHT, ATLAS_TILE_WIDTH, ATLAS_TILE_HEIGHT));
            }
        }
    }

    private void createMenuBar() {
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

    public static void main(String[] args) {
        TanksApplication app = new TanksApplication();

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
}