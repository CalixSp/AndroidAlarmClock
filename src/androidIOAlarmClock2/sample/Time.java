package androidIOAlarmClock2.sample;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Simple class to control the time keeping and changing of the clock panes.
 * 
 * @author David
 *
 */
public class Time {
	int hours, minutes, seconds;
	boolean am;
	long lastTimeSec;
	
	public Time() {
		Calendar cal=new GregorianCalendar();
		am=cal.get(Calendar.AM_PM)==Calendar.AM?true:false;
		hours=cal.get(Calendar.HOUR);
		minutes=cal.get(Calendar.MINUTE);
		seconds=cal.get(Calendar.SECOND);
		lastTimeSec=System.currentTimeMillis()/1000;
	}
	
	public boolean equals(Time t) {
		return (am==t.am && hours==t.hours && minutes==t.minutes && seconds==t.seconds);
	}
	
	public String get(int dataType) {
		String res="";
		if(dataType==Calendar.AM_PM) return am?"am":"pm";
		else if(dataType==Calendar.HOUR) res=Integer.toString(hours);
		else if(dataType==Calendar.MINUTE) res=Integer.toString(minutes);
		else if(dataType==Calendar.SECOND) res=Integer.toString(seconds);
		if(res.length()==1) res="0"+res;
		return res;
	}
	
	/**
	 * This is what happens when user clicks on a clock pane. Behaves like an alarm clock to set time.
	 * Mostly for setting the alarm time.
	 * 
	 * @param dataType
	 */
	public void change(int dataType) {
		if(dataType==Calendar.AM_PM) am=!am;
		else if(dataType==Calendar.HOUR) hours=(hours==12)?1:hours+1;
		else if(dataType==Calendar.MINUTE) minutes=(minutes==59)?0:minutes+1;
		else if(dataType==Calendar.SECOND) seconds=(seconds==59)?0:seconds+1;
	}
	
	/**
	 * Update seconds by the discretized amount of seconds that have passed then pass
	 * this along to minutes and hours
	 * 
	 * @param curTime
	 */
	public void update(long curTime) {
		long curTimeSec=curTime/1000;
		if(curTimeSec==lastTimeSec) return;
		seconds+=curTimeSec-lastTimeSec;
		lastTimeSec=curTimeSec;
		if(seconds==60) {
			seconds=0;
			minutes++;
			if(minutes==60) {
				minutes=0;
				hours++;
				if(hours==12) {
					am=!am;
				}
				else if(hours==13) {
					hours=0;
				}
			}
		}
	}
}
