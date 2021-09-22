package Model;

import java.time.LocalDate;

public abstract class Observation {
	private TimePoint refTime;
	private TimePoint date;
	private Status status;  
	private Evidence ev;
	private Subject sub;
	private Notifier not;
	
	
	public Observation(Subject sub,TimePoint refTime,Evidence ev,Notifier not) {
		this.sub=sub;
		this.refTime=refTime; 
		this.ev=ev;	
		this.not=not;
		status= new Status();
		date=new TimePoint(LocalDate.now());
	}

	public TimePoint getDate() {
		return date;
	}


	public void setDate(TimePoint date) {
		this.date = date;
	}


	public Status getStatus() {
		return status;
	}
	
	public boolean isActive() {
		return status.getActive();
	}


	public void setStatus(boolean bool) {
		this.status.setActive(bool);
	}


	public TimePoint getRefTime() {
		return refTime;
	}


	public void setRefTime(TimePoint refTime) {
		this.refTime = refTime;
	}


	public Evidence getEv() {
		return ev;
	}


	public void setEv(Evidence ev) {
		this.ev = ev;
	}

	public Subject getSub() {
		return sub;
	}


	public void setSub(Subject sub) {
		this.sub = sub;
	}


	public void setNot(Notifier not) {
		this.not = not;
	}


	/*public void setStatus(Status status) {
		this.status = status;
	}*/
	
	public boolean isContact() {
		return (this instanceof Contact); 
	}
	
	public boolean isSymptom() {
		return (this instanceof Symptom);
	}
	
	public boolean isResult() {
		return (this instanceof Result);
	}

}
