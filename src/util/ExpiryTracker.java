package util;

import logic.BloodStockDAO;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiryTracker {
    
    private static ScheduledExecutorService scheduler;

    public static void startTracker() {
        // Prevent starting multiple trackers if the admin logs out and back in
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            
            // The Logic: What to do, Initial Delay, How often to repeat, Unit of time
            scheduler.scheduleAtFixedRate(() -> {
                
                int expiredCount = BloodStockDAO.sweepExpiredBags();
                
                if (expiredCount > 0) {
                    // This prints to your Eclipse console so you know the silent thread is working!
                    System.out.println("[SYSTEM SWEEP] Auto-expired " + expiredCount + " outdated blood bags.");
                }
                
            }, 0, 24, TimeUnit.HOURS); // Runs immediately, then once every 24 hours
        }
    }

    // Good practice to shut down background threads when the app completely closes
    public static void stopTracker() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}