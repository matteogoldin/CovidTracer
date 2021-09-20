package Model;

public class Contact extends Observation{
	public enum risk{
		low,
		medium,
		high
	}
	
	private Subject secondarySub;
	private risk rsk;
	
	public Contact(Subject secondarySub, risk rsk, Subject sub, TimePoint refTime,Evidence ev, Notifier not) {
		super(sub,refTime,ev,not);
		this.secondarySub=secondarySub;
		this.rsk=rsk;
	}
	
	public Subject getSecondarySub() {
		return secondarySub;
	}

	public risk getRsk() {
		return rsk;
	}

}
