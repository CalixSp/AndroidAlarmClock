package androidIOAlarmClock2.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android_serialport_api.SerialPort;

/**
 * Controls the core drawing routines, such as drawing the various clock panes.
 * Also contains the core links the the other classes, so this is where most of the action happens.
 */
class ClockPanel extends SurfaceView implements SurfaceHolder.Callback {
		
		public static ConsoleActivity context;
		public static ArrayList<GraphicObject> graphics=new ArrayList<GraphicObject>();
		ClockPart[] clockPanes=new ClockPart[4];
		public static ToggleButton[] toggleButtons=new ToggleButton[3];
		static UpdateTime thread;
		static Time alarm, clock;
		public static Time currentTime;
		public static boolean checkedAlarm=false;
		
		public ClockPanel(ConsoleActivity context) {
			super(context);
			this.context=context;
			getHolder().addCallback(this);
            thread = new UpdateTime(getHolder(), this);
            setFocusable(true);
            
            //create clockPanes for hour, minute, and second
        	Bitmap clockBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.timebackground);
    	    Matrix matrix = new Matrix();
    	    float scale=(float)0.2/clockBitmap.getWidth()*ConsoleActivity.width;
    	    matrix.postScale(scale,scale);
    	    Bitmap clockBitmapScaled = Bitmap.createBitmap(clockBitmap, 0, 0,
    		    	(int)(clockBitmap.getWidth()), (int)(clockBitmap.getHeight()), matrix, true);
        	ClockPart.setup( clockBitmapScaled, Color.argb(255,255,255,255) , 96 );
        	clockPanes[0]=new ClockPart((int)(ConsoleActivity.width/2-ClockPart.getStaticWidth()*2.2),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.HOUR);
        	clockPanes[1]=new ClockPart((int)(ConsoleActivity.width/2-ClockPart.getStaticWidth()*1.1),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.MINUTE);
        	clockPanes[2]=new ClockPart((int)(ConsoleActivity.width/2),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.SECOND);
        	clockPanes[3]=new ClockPart((int)(ConsoleActivity.width/2+ClockPart.getStaticWidth()*1.1),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.AM_PM);
        	graphics.add(clockPanes[0]); graphics.add(clockPanes[1]); graphics.add(clockPanes[2]); graphics.add(clockPanes[3]);
        	
        	//draw toggle buttons for changing the time
        	Bitmap redBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.redbutton);
        	Bitmap greenBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.greenbutton);
    	    matrix = new Matrix();
    	    scale=(float)0.25/clockBitmap.getWidth()*ConsoleActivity.width;
    	    matrix.postScale(scale,scale/4);
    	    Bitmap redBitmapScaled = Bitmap.createBitmap(redBitmap, 0, 0,
    		    	(int)(redBitmap.getWidth()), (int)(redBitmap.getHeight()), matrix, true);
    	    Bitmap greenBitmapScaled = Bitmap.createBitmap(greenBitmap, 0, 0,
    		    	(int)(greenBitmap.getWidth()), (int)(greenBitmap.getHeight()), matrix, true);
    	    ToggleButton.setup(greenBitmapScaled, redBitmapScaled);
    	    toggleButtons[0]=new ToggleButton( (int)(ConsoleActivity.width/2-ToggleButton.getStaticWidth()*1.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Mode: Alarm", "Mode: Clock") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		currentTime=(currentTime==clock)?alarm:clock;
    	    		ConsoleActivity.svState1=!ConsoleActivity.svState1;
    	    	}
    	    };
    	    toggleButtons[1]=new ToggleButton( (int)(ConsoleActivity.width/2-ToggleButton.getStaticWidth()*0.5),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Set Time", "Lock Time"){
    	    	public void clicked() {
    	    		super.clicked();
    	    		ConsoleActivity.svState2=!ConsoleActivity.svState2;
    	    	}
    	    };
    	    toggleButtons[2]=new ToggleButton( (int)(ConsoleActivity.width/2+ToggleButton.getStaticWidth()*0.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Alarm On" , "Alarm Off") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		if(getState()==false) ClockPanel.checkedAlarm=false;
    	    		if(Alarm.isRunning()) Alarm.stop();
    	    		ConsoleActivity.svState3=!ConsoleActivity.svState3;
    	    	}
    	    };
    	    /*toggleButtons[3]=new ToggleButton( (int)(ConsoleActivity.width/2+ToggleButton.getStaticWidth()*0.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6+ToggleButton.getStaticHeight()*1.2) , "Connect to Watch", "Already Connected") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		allowReadWrite();
    	    		ConsoleActivity.svState4=!ConsoleActivity.svState4;
    	    	}
    	    };*/
    	    for(int i=0; i<3; i++)
    	    	graphics.add(toggleButtons[i]);
    	    
    	    //construct the objects that will keep track of the time and the alarm behavior
        	alarm=new Time();
        	clock=new Time();
        	ConsoleActivity.svAlarmTime=alarm;
        	ConsoleActivity.svClockTime=clock;
        	currentTime=clock;

		}
		
		public ClockPanel(ConsoleActivity context, boolean svState1, boolean svState2, boolean svState3, boolean svState4, Time svAlarmTime, Time svClockTime) {
			super(context);
			this.context=context;
			getHolder().addCallback(this);
            thread = new UpdateTime(getHolder(), this);
            setFocusable(true);
            
            //clockPanes
        	Bitmap clockBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.timebackground);
    	    Matrix matrix = new Matrix();
    	    float scale=(float)0.2/clockBitmap.getWidth()*ConsoleActivity.width;
    	    matrix.postScale(scale,scale);
    	    Bitmap clockBitmapScaled = Bitmap.createBitmap(clockBitmap, 0, 0,
    		    	(int)(clockBitmap.getWidth()), (int)(clockBitmap.getHeight()), matrix, true);
        	ClockPart.setup( clockBitmapScaled, Color.argb(255,255,255,255) , 96 );
        	clockPanes[0]=new ClockPart((int)(ConsoleActivity.width/2-ClockPart.getStaticWidth()*2.2),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.HOUR);
        	clockPanes[1]=new ClockPart((int)(ConsoleActivity.width/2-ClockPart.getStaticWidth()*1.1),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.MINUTE);
        	clockPanes[2]=new ClockPart((int)(ConsoleActivity.width/2),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.SECOND);
        	clockPanes[3]=new ClockPart((int)(ConsoleActivity.width/2+ClockPart.getStaticWidth()*1.1),ConsoleActivity.height/2-ClockPart.getStaticHeight()/2,Calendar.AM_PM);
        	graphics.add(clockPanes[0]); graphics.add(clockPanes[1]); graphics.add(clockPanes[2]); graphics.add(clockPanes[3]);
        	
        	//toggle buttons
        	Bitmap redBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.redbutton);
        	Bitmap greenBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.greenbutton);
    	    matrix = new Matrix();
    	    scale=(float)0.25/clockBitmap.getWidth()*ConsoleActivity.width;
    	    matrix.postScale(scale,scale/4);
    	    Bitmap redBitmapScaled = Bitmap.createBitmap(redBitmap, 0, 0,
    		    	(int)(redBitmap.getWidth()), (int)(redBitmap.getHeight()), matrix, true);
    	    Bitmap greenBitmapScaled = Bitmap.createBitmap(greenBitmap, 0, 0,
    		    	(int)(greenBitmap.getWidth()), (int)(greenBitmap.getHeight()), matrix, true);
    	    ToggleButton.setup(greenBitmapScaled, redBitmapScaled);
    	    toggleButtons[0]=new ToggleButton( (int)(ConsoleActivity.width/2-ToggleButton.getStaticWidth()*1.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Mode: Alarm", "Mode: Clock") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		currentTime=(currentTime==clock)?alarm:clock;
    	    		ConsoleActivity.svState1=!ConsoleActivity.svState1;
    	    	}
    	    };
    	    toggleButtons[1]=new ToggleButton( (int)(ConsoleActivity.width/2-ToggleButton.getStaticWidth()*0.5),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Set Time", "Lock Time"){
    	    	public void clicked() {
    	    		super.clicked();
    	    		ConsoleActivity.svState2=!ConsoleActivity.svState2;
    	    	}
    	    };
    	    toggleButtons[2]=new ToggleButton( (int)(ConsoleActivity.width/2+ToggleButton.getStaticWidth()*0.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6) , "Alarm On" , "Alarm Off") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		if(getState()==false) ClockPanel.checkedAlarm=false;
    	    		if(Alarm.isRunning()) Alarm.stop();
    	    		ConsoleActivity.svState3=!ConsoleActivity.svState3;
    	    	}
    	    };
    	    /*toggleButtons[3]=new ToggleButton( (int)(ConsoleActivity.width/2+ToggleButton.getStaticWidth()*0.6),(int)(ConsoleActivity.height/2+ClockPart.getStaticHeight()*0.6+ToggleButton.getStaticHeight()*1.2) , "Connect to Watch", "Already Connected") {
    	    	public void clicked() {
    	    		super.clicked();
    	    		allowReadWrite();
    	    		ConsoleActivity.svState4=!ConsoleActivity.svState4;
    	    	}
    	    };*/
    	    
    	    toggleButtons[0].setState(svState1);
    	    toggleButtons[1].setState(svState2);
    	    toggleButtons[2].setState(svState3);
    	    //toggleButtons[3].setState(svState4);
    	    
    	    for(int i=0; i<3; i++)
    	    	graphics.add(toggleButtons[i]);
    	    
        	alarm=svAlarmTime;
        	clock=svClockTime;
        	currentTime=clock;

		} 
		
		/**
		 * Try to request read/write access to the USB port
		 */
		public void allowReadWrite() {
			Process process = null;
			try {
				process = new ProcessBuilder()
				   .command("/dev", "chmod 777 ttyACM0")
				   .redirectErrorStream(true)
				   .start();
			
		     InputStream in = process.getInputStream();
		     OutputStream out = process.getOutputStream();

		    // readStream(in);
			} catch (IOException e) { e.printStackTrace(); }
		    finally { if(process!=null) process.destroy(); }
			/*try {
				
				Process su=Runtime.getRuntime().exec( new String[]{"su","cd dev","chmod 777 ttyACM0"} );
				///String cmd = "cd dev";
				//su.getOutputStream().write(cmd.getBytes());
				//cmd = "chmod 777 ttyACM0";
				//su.getOutputStream().write(cmd.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}*/
		 }
		
		
		/**
		 * Control the changing of the alarm modes and the changing of the time by
		 * clicking on the alarm times
		 */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	if(toggleButtons[1].getState()==true) {
	        	for(int i=0; i<clockPanes.length; i++) {
		        	if(clockPanes[i].contains(event)) {
			        	if(event.getAction()==MotionEvent.ACTION_UP) {
			        		clockPanes[i].clicked();
		        		}
		        	}
	        	}
        	}
        	for(int i=0; i<toggleButtons.length; i++) {
	        	if(toggleButtons[i].contains(event)) {
		        	if(event.getAction()==MotionEvent.ACTION_UP) {
		        		toggleButtons[i].clicked();
	        		}
	        	}
        	}
        	return true;
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        	if(canvas!=null) {
        		canvas.drawColor(Color.BLACK);
        		for (GraphicObject graphic : graphics) {
        			graphic.draw(canvas);
        		}
        	}
        }
     
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
     
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	thread.setRunning(true);
            thread.start();
        }
     
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        	if(thread!=null)
        		thread.interrupt();
        }
        
        public static void interruptThread() {
        	//if(thread!=null) 
        	//	thread.interrupt();
        	//Log.d("MyApp","asdfjsdhfjhsjkfjksgfkdfjkhasdhfjksd");
        }
        
        /**
    	 * Check if we want to start the alarm
    	 */
        public static void checkAlarm() {
        	if(toggleButtons[2].getState()==true && alarm.equals(clock) && !checkedAlarm) {
        		try {
        			System.out.println("connecting");
					context.connect("/dev/ttyACM0",115200);
				} catch (SecurityException e) {
					//Looper.prepare();
					//Toast.makeText(context, "watch not connected", 1000).show();
				} catch (IOException e) {
				}
        		ConsoleActivity.newAlarm();
        		checkedAlarm=true;
        	}
        }
        
        
        
    }