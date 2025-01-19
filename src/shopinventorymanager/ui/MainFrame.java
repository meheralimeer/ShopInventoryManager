package shopinventorymanager.ui;

import javax.swing.JFrame;

/**
 * The main application window for the Shop Inventory Manager.
 * Extends JFrame to provide the primary GUI container.
 * 
 * @author meheralimeer
 */
public class MainFrame extends JFrame {
    /** Panel for displaying and managing inventory items */
    private ViewItemsPanel viewItemsPanel;

    /**
     * Constructs the main application frame.
     * Initializes the window properties and components.
     */
    public MainFrame() {
        setTitle("Shop Inventory Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setIconImage(ViewItemsPanel.getResizedIcon("res/icons/app.png").getImage());
        initComponents();
    }

    /**
     * Initializes and sets up the GUI components.
     * Creates and adds the ViewItemsPanel to the frame.
     * 
     * @see ViewItemsPanel
     */
    private void initComponents() {
        viewItemsPanel = new ViewItemsPanel();
        add(viewItemsPanel);
    }
}
