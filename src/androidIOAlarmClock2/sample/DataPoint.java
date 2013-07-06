package androidIOAlarmClock2.sample;

/**
 * Contains the information about one point of acceleration data
 * 
 * @author David
 *
 */
public class DataPoint {
	
	Vector3 pos, vel, acc, normalAcc, ratio;
	
	public DataPoint(Vector3 acc, Vector3 normalAcc, Vector3 vel, Vector3 pos) {
		this.acc=acc;
		this.normalAcc=normalAcc;
		this.vel=vel;
		this.pos=pos;
		double sum=Math.abs(acc.x)+Math.abs(acc.y)+Math.abs(acc.z);
		ratio=new Vector3(acc.x/sum,acc.y/sum,acc.z/sum);
	}
	
}