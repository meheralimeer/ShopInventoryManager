package shopinventorymanager.utils;

import java.io.File;
import shopinventorymanager.model.Item;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Service responsible for monitoring and notifying about item expiry dates.
 * Provides scheduled checks and audio-visual notifications.
 * 
 * @author meheralimeer
 */
public class NotificationService {
    /** Scheduler for running periodic expiry checks */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Starts the expiry check service.
     * Performs immediate check at startup and schedules daily checks at 8 AM.
     */
    public void startExpiryCheck() {
        // Check immediately at startup
        checkExpiredItems();

        // Schedule daily check at 8 AM
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        long initialDelay = java.time.Duration.between(now, nextRun).toMillis();
        scheduler.scheduleAtFixedRate(() -> {
            checkExpiredItems();
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    /**
     * Checks for expired or expiring items and triggers notifications.
     */
    private void checkExpiredItems() {
        try {
            List<Item> items = FileHandler.loadItems();
            LocalDate today = LocalDate.now();
            items.stream()
                    .filter(item -> item.getExpiryDate().isBefore(today) || item.getExpiryDate().isEqual(today))
                    .forEach(this::showExpiryNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a notification with sound for an expired or expiring item.
     * @param item The item that has expired or is expiring
     */
    private void showExpiryNotification(Item item) {
        SwingUtilities.invokeLater(() -> {
            Clip clip = null;
            try {
                ////java.net.URL soundURL = getClass().getResource("res/sound/alert.wav");
                AudioInputStream audioIn = AudioSystem
                        .getAudioInputStream(new File("res/sound/alert.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();

                String message = item.getExpiryDate().isEqual(LocalDate.now())
                        ? "Item " + item.getName() + " expires today!"
                        : "Item " + item.getName() + " has expired!";

                JOptionPane.showMessageDialog(null,
                        message,
                        "Expiry Alert",
                        JOptionPane.WARNING_MESSAGE);

                // Stop sound after dialog is closed
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                    clip.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
