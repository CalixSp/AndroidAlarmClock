package androidIOAlarmClock2.sample;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * One of hour/minute/second panel for the clock. Controls drawing mainly.
 */
public class ClockPart extends GraphicObject{

	private Bitmap text;
	private int dataType;
	public static Paint paint;
	public static Bitmap background;
	
	public ClockPart(int x, int y, int dataType) {
		super(background,x,y);
		this.dataType=dataType;
	}
	
	/**
	 * Establish most of the components of the clockpart
	 */
	public static void setup(Bitmap bitmap, int color, int size) {
		paint=new Paint();
		paint.setColor(color);
		background=bitmap;
		paint.setTextSize((int)(getStaticHeight()));
	}

	public static int getStaticWidth() {
		return background.getWidth();
	}

	public static int getStaticHeight() {
		return background.getHeight();
	}
	
	/**
	 * Called from ClockPanel in the drawing routine to draw the single clock pane
	 */
	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x, y,  null);
		String timeS=ClockPanel.currentTime.get(dataType);
		int textX=(int)(x+getStaticWidth()/2-paint.measureText(timeS)/2);
		canvas.drawText(timeS, textX, y+(int)(getStaticHeight()*0.9), paint);
	}
	
	/**
	 * Change the time of the pane if it is clicked on. Called from the Clock Panel.
	 */
	public void clicked() {
		ClockPanel.currentTime.change(dataType);
	}
}
