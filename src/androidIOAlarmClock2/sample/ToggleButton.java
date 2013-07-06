package androidIOAlarmClock2.sample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Simple button for controlling the toggle behavior of the alarm/time/and other buttons.
 * Toggles the color of the button and the text displayed.
 * 
 * @author David
 *
 */
public class ToggleButton extends GraphicObject{

	private boolean toggle;
	private static Paint paint;
	private static Bitmap b1,b2;
	private String s1,s2;
	
	public ToggleButton(int x, int y, String s1, String s2) {
		super(b1,x,y);
		toggle=false;
		this.s1=s1;
		this.s2=s2;
	}
	
	public void setState(boolean b){
		toggle=b;
	}
	public static void setup(Bitmap bitmap1, Bitmap bitmap2) {
		b1=bitmap1;
		b2=bitmap2;
		paint=new Paint();
		paint.setColor(Color.argb(255,255,255,255));
		paint.setTextSize((int)(getStaticHeight()*0.8));
	}
	
	public static int getStaticWidth() {
		return b1.getWidth();
	}
	
	public static int getStaticHeight() {
		return b1.getHeight();
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(toggle) {
			canvas.drawBitmap(b1, x, y,  null);
			int textX=(int)(x+getStaticWidth()/2-paint.measureText(s1)/2);
			canvas.drawText(s1, textX, y+(int)(getStaticHeight()*0.8), paint);
		}
		else {
			canvas.drawBitmap(b2, x, y,  null);
			int textX=(int)(x+getStaticWidth()/2-paint.measureText(s2)/2);
			canvas.drawText(s2, textX, y+(int)(getStaticHeight()*0.8), paint);
		}
	}
	
	public boolean getState() {
		return toggle;
	}
	
	public void clicked() {
		toggle=!toggle;
	}
	
}
