package androidIOAlarmClock2.sample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Simple class to control the drawing behavior of one object
 * 
 * @author David
 *
 */
class GraphicObject {
        protected Bitmap bitmap;
        protected int x,y;
     
        public GraphicObject(Bitmap bitmap, int x, int y) {
            this.bitmap = bitmap;
        	this.x=x;
        	this.y=y;
        }
        
        public void setCoords(int x, int y) {
        	this.x=x;
        	this.y=y;
        }
        
        public Bitmap getGraphic() {
            return bitmap;
        }
     
        public int getX() {
        	return x;
        }	
        
        public int getY() {
        	return y;
        }
        
        public int getWidth() {
        	return bitmap.getWidth();
        }
        
        public int getHeight() {
        	return bitmap.getHeight();
        }
        
        public void click() {}
        
        public boolean contains(MotionEvent e) {
    		int sx=x, sy=y;
    		int ex=sx+bitmap.getWidth(), ey=sy+bitmap.getHeight();
    		float x=e.getX(), y=e.getY();
    		if(x>ex || x<sx || y>ey || y<sy) return false;
    		else return true;
    	}
        
        public void draw(Canvas canvas) {
        	canvas.drawBitmap(bitmap,x,y,null);
        }
    }