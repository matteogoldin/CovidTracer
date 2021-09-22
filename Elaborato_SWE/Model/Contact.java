package Model;

public class Contact extends Observation{
	public enum risk{
		low,
		medium,
		high
	}
	
	private Subject secondarySub;
	private risk rsk;
	private float covProb;
	
	public Contact(Subject secondarySub, risk rsk, Subject sub, TimePoint refTime,Evidence ev, Notifier not,float covProb) {
		super(sub,refTime,ev,not);
		this.secondarySub=secondarySub;
		this.rsk=rsk;
		this.covProb = covProb;
	}
	
	public Contact(Subject secondarySub, risk rsk, Subject sub, TimePoint refTime,Evidence ev, Notifier not) {
		super(sub,refTime,ev,not);
		this.secondarySub=secondarySub;
		this.rsk=rsk;
		this.covProb = 1;
	}
	
	public Subject getSecondarySub() {
		return secondarySub;
	}

	public risk getRsk() {
		return rsk;
	}

	public float getCovProb() {
		return covProb;
	}

	public void setCovProb(float covProb) {
		this.covProb = covProb;
	}
	
	

}
