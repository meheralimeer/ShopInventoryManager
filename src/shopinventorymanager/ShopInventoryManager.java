package shopinventorymanager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import shopinventorymanager.ui.MainFrame;
import shopinventorymanager.utils.NotificationService;

/**
 * The main class for the Shop Inventory Manager application.
 * This class initializes the GUI and starts the notification service.
 * 
 * @author meheralimeer
 * @version 1.0
 */
public class ShopInventoryManager {
    /**
     * The main entry point for the application.
     * Sets up the system look and feel, creates and displays the main window,
     * and initializes the notification service for product expiry checks.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);

            // Start the notification service
            NotificationService notificationService = new NotificationService();
            notificationService.startExpiryCheck();
        });
    }
}