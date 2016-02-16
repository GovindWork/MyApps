package drive.solution.com.drivecoach.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class HealthStatusUpdate extends Service {

    private SyncThread syncThread = null;

    private static final int syncInterval = 1; //1 minutes

    public HealthStatusUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        syncThread = new SyncThread(getBaseContext());
        syncThread.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (syncThread != null) {
            syncThread.stopThread();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START_STICKY so that the service thread is restart if Android System
        // terminates it.
        return Service.START_STICKY;
    }

    public class SyncThread extends Thread {

        private Context mContext = null;
        private boolean running = true;

        public SyncThread(Context context) {
            mContext = context;
            running = true;
        }

        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    try {
                        Thread.sleep(syncInterval * 30 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!running) {
                        break;
                    }
                    Log.d("Sync Thread","Thead is waking up.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.d("Health Status service", "Sync Thread:Exit");
            }
        }
    }
}
