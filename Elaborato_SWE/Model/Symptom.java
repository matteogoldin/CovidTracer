package Model;

import MedicalDomain.TypeOfSymptom;
import MedicalDomain.TypeOfSymptom.sym;
import Model.Contact.risk;

public class Symptom extends Observation{
	public enum exhibit{
		lowRisk,
		mediumRisk,
		highRisk,
		none;
	}
	private exhibit ex;
	private sym s;
	private TimePoint endTime;
	
	
	public Symptom(sym s, Subject sub, TimePoint refTime,Evidence ev,Notifier not, TimePoint endTime) {
		super(sub,refTime,ev,not);
		this.s=s;
		this.ex=TypeOfSymptom.classifySymptom(s);
		this.endTime=endTime;
		
	}

	public Symptom(sym s, Subject sub, TimePoint refTime,Evidence ev,Notifier not) {
		super(sub,refTime,ev,not);
		this.s=s;
		this.ex=TypeOfSymptom.classifySymptom(s);
		this.endTime=null;		
	}	
	
	public sym getS() {
		return s;
	}

	public TimePoint getEndTime() {
		return endTime;
	}

	public void setEndTime(TimePoint endTime) {
		this.endTime = endTime;
	}

	public exhibit getEx() {
		return ex;
	}
		
}
