package androidIOAlarmClock2.sample;

import java.util.Iterator;
import java.util.LinkedList;

import android.widget.TextView;

/**
 * Holds all of the data to determine trends that hint that the user has fallen asleep
 * 
 * @author David
 *
 */
public class DataStore {
	
	public static LinkedList<DataPoint> data=new LinkedList<DataPoint>();
	private static DataStore instance;
	private static Vector3 accSum=new Vector3(0,0,0);
	public static Vector3 accNormalizer=new Vector3(0,0,0);
	private static int dataStoreLim=300;
	private static Vector3 lastVel=new Vector3(0,0,0), lastPos=new Vector3(0,0,0);
	private static Vector3 newAcc=new Vector3(0,0,0),newNormalAcc=new Vector3(0,0,0),
		newVel=new Vector3(0,0,0),newPos=new Vector3(0,0,0);
	
	public static DataStore getInstance() {
		if(instance==null) instance=new DataStore();
		return instance;
	}
	
	/**
	 * Add a new datapoint to the list and perform a check 
	 * 
	 * @param buffer
	 */
	public void add(byte[] buffer) {
		synchronized(ReadWriteThread.getInstance()) {
			
			if(buffer.length!=7 || buffer[4]==0 || buffer[5]==0 || buffer[6]==0)
				return;
			
			newAcc=new Vector3(buffer[4],buffer[5],buffer[6]);
			
			accSum=Vector3.add(accSum,newAcc);
			
			//if(useAccNormalizer)
				newNormalAcc=Vector3.subtract( newAcc, accNormalizer );
			//else
				//newAcc=new Vector3(buffer[4],buffer[5],buffer[6]);
			
			if(newNormalAcc.magnitude()>50) {
				newVel=Vector3.add(lastVel,newAcc);
			}
			else{
				newVel=lastVel;
			}
			newPos=Vector3.add(lastPos,newVel);
			
			if(newVel.magnitude()<25) lastVel=new Vector3(0,0,0);
			else lastVel=newVel.mult(0.9);
			
			lastPos=newPos;
			
			//if(data.size()==dataStoreLim-1) {
				//lastVel=new Vector3(0,0,0);
				//lastPos=new Vector3(0,0,0);
			//}
			
			data.add(new DataPoint( newAcc, newNormalAcc, newVel, newPos ));
			
			if(data.size()>dataStoreLim) {
				DataPoint old=data.removeFirst();
				accSum=Vector3.subtract(accSum,old.acc);
			}
			
			accNormalizer=accSum.div(data.size());
			
		}
	}
	
	/**
	 * Check if the user is awake by looking for a simple above a threshold pattern in the normalized data
	 * 
	 * @return
	 */
	public static int checkAwake() {
		synchronized(ReadWriteThread.getInstance()) {
			if(data.size()<dataStoreLim-1) return 0;
			Vector3 avg=accSum.div(data.size());
			double div=0;
			Iterator<DataPoint> it=data.iterator();
			while(it.hasNext()) {
				div+=Vector3.difference(avg,it.next().acc);
			}
			div=div/data.size();
			data.clear();
			accSum=new Vector3(0,0,0);
			if(div<10) return 1;
			return 2;
		}
	}
	
}
