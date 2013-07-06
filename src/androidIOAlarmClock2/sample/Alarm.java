package androidIOAlarmClock2.sample;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class Alarm {

	public static ConsoleActivity activity;
	private static Thread thread;
	public static MediaPlayer mp;
	private static boolean _run=false;
	
	/**
	 * Start playing sound from media player in new thread
	 */
	public static void play() {
		mp = MediaPlayer.create(activity.getApplicationContext(), R.drawable.alarm_beep);
		mp.start();
		_run=true;
		thread=new Thread( new Runnable() {
			public void run() {
				while(_run) {
					if(!mp.isPlaying()) {
						mp = MediaPlayer.create(activity.getApplicationContext(), R.drawable.alarm_beep); 
						try {
							mp.prepare();
						} catch (Exception e) {}
						mp.start();
					}
				}
			}
		});
		thread.start();	
	}
	
	/**
	 * See if sound is currently playing
	 */
	public static boolean isRunning() {
		return _run;
	}
	
	/**
	 * Halt sound
	 */
	public static void stop() {
		_run=false;
		mp.stop();
	}
	
	/**
	 * Kill thread
	 */
	public static void interrupt() {
		if(thread!=null)
			thread.interrupt();
	}	
}
