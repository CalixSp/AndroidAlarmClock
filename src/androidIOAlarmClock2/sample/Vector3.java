package androidIOAlarmClock2.sample;

/**
 * Simple data structure to hold an acceleration vector of three values.
 * 
 * @author David
 *
 */
public class Vector3 {
	
	double x,y,z;
	
	public Vector3(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x+b.x,a.y+b.y,a.z+b.z);
	}
	
	public static Vector3 subtract(Vector3 a, Vector3 b) {
		return new Vector3(a.x-b.x,a.y-b.y,a.z-b.z);
	}
	
	public static double difference(Vector3 a, Vector3 b) {
		return Math.abs(a.x-b.x)+Math.abs(a.y-b.y)+Math.abs(a.z-b.z);
	}
	
	public static double difference2(Vector3 a, Vector3 b) {
		double dx=a.x-b.x, dy=a.y-b.y, dz=a.z-b.z;
		return Math.abs(dx*dx+dy*dy+dz*dz);
	}
	
	public Vector3 div(int b) {
		return new Vector3(x/b,y/b,z/b);
	}
	
	public Vector3 mult(double b) {
		return new Vector3(x*b,y*b,z*b);
	}
	
	public double magnitude() {
		return x*x+y*y+z*z;
	}
	
	public void reduce(int amount) {
		if(x>=amount) x-=amount;
		if(x<=-amount) x+=amount;
		if(y>=amount) y-=amount;
		if(y<=-amount) y+=amount;
		if(z>=amount) z-=amount;
		if(z<=-amount) z+=amount;
	}
	
	public Vector3 quantize(int threshold) {
		return new Vector3( ((int)x)/threshold*threshold , ((int)y)/threshold*threshold , ((int)z)/threshold*threshold);	
	}
}
