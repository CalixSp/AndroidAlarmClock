package androidIOAlarmClock2.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * Controls the reading and writing of the USB that brings in the watch data
 * 
 * @author David
 *
 */
public class ReadWriteThread extends Thread {

	//protected static Application mApplication;
	//protected static SerialPort mSerialPort;
	protected static OutputStream mOutputStream;
	private static InputStream mInputStream;
	private static Activity reciever;
	private static DataStore dataStore;
	public static ReadWriteThread instance;
	public static int tries;
	private static boolean stillZero;
	int goodDumps=0;
	
	public static ReadWriteThread getInstance() {
		if(instance==null) instance=new ReadWriteThread();
		return instance;
	}
	
	public static void setStuff(Activity a, InputStream is, OutputStream os) {
		reciever=a;
		mInputStream=is;
		mOutputStream=os;
		dataStore=DataStore.getInstance();
		tries=0;
	}
	
	/**
	 * All of the action happens here. Continually check if new data comes into the port then
	 * send it to the datastore when it does.
	 */
	public void run() {
		super.run();
		while(!isInterrupted()) {
			int size;
			try {
				
				//write acc request
				mOutputStream.write(new byte[]{(byte)0xFF,0x08,0x07,0x00,0x00,0x00,0x00});
				
				//read in acc data
				byte[] buffer = new byte[7];
				size=mInputStream.read(buffer);
				/*if(tries==0) stillZero=true;
				if(size>0) { stillZero=false; ClockPanel.turnWatchOn=false; }
				if(stillZero && tries>25) ClockPanel.turnWatchOn=true;
				tries++;*/
				if(size>0 && ClockPanel.toggleButtons[2].getState()==false) {
					dataStore.add(buffer);
					int ans=DataStore.checkAwake();
					if(ans==1) { goodDumps=0; ConsoleActivity.newAlarm(); }
					if(ans==2) { goodDumps++; }
					if(goodDumps==4) break;
				}
				
			} catch (IOException e) {
				//Toast.makeText(reciever, "error writing to watch", 1000);
				e.printStackTrace();
				return;
			}
		}
	}
}