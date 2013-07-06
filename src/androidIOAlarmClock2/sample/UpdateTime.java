package androidIOAlarmClock2.sample;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Simple thread that updates the UI every approximately one second.
 * Only trick here is that the thread does not update at consistent second time
 * intervals so we have to check the actual time whenever we set it.
 * 
 * @author David
 *
 */
class UpdateTime extends Thread {
        private SurfaceHolder _surfaceHolder;
        private ClockPanel _panel;
        private boolean _run = false;
 
        public UpdateTime(SurfaceHolder surfaceHolder, ClockPanel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
            ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        	
    		// This schedule a runnable task every 1 second
    		scheduleTaskExecutor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
        }
 
        public SurfaceHolder getSurfaceHolder() {
            return _surfaceHolder;
        }
        
        public void setRunning(boolean run) {
            _run = run;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.onDraw(c);
                        ClockPanel.clock.update(System.currentTimeMillis());
                        ClockPanel.checkAlarm();
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
}