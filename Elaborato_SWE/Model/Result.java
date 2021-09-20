package Model;

public class Result extends Observation{
	public enum res{
		positive,
		negative
	}
	private res r;
	
	public Result(res r,Subject sub, TimePoint refTime,Evidence ev,Notifier not){
		super(sub,refTime,ev,not);
		this.r=r;
	}

	public res getR() {
		return r;
	}	
}
