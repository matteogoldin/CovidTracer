package Model;

import java.time.Duration;

public class TimeInterval implements TimeRecord{
	
	private TimePoint t1;
	private TimePoint t2;
	private long interval;
	
	public TimeInterval(TimePoint t1,TimePoint t2) {
		if(t1.compareTo(t2)>0) {
			this.t2=t1;
			this.t1=t2;
					
		}else {
			this.t1=t1;
			this.t2=t2;
		}
		interval=this.getInterval(t2);
		
	}
	
	public long getInterval(TimePoint t2) { //TO-DO l'interfaccia TimeRecord serve???
		 Duration span = Duration.between(t1.getRecord(), t2.getRecord());
		 long interval=span.toDays();
		 return interval;
	}
	
	public TimePoint getT1() {
		return t1;
	}
	
	public TimePoint getT2() {
		return t2;
	}
	
	public void setT1(TimePoint t1) {
		this.t1 = t1;
	}
	
	public void setT2(TimePoint t2) {
		this.t2 = t2;
	}
	
}
