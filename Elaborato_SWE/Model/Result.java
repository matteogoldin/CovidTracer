package Model;

public class Result extends Observation{
	public enum res{
		positive,
		negative
	}
	
	private res r;
	private int vl; //carica virale compresa tra 0 e 3
	
	public Result(res r,Subject sub, TimePoint refTime,Evidence ev,Notifier not,int vl){
		super(sub,refTime,ev,not);
		this.r=r;
		this.vl=vl;
	}

	public res getR() {
		return r;
	}

	public int getVl() {
		return vl;
	}
	
}
