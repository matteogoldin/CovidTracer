package Model;

import java.time.*;

public class TimePoint implements TimeRecord,Comparable<TimePoint> {
	
	private LocalDate record;
	
	public TimePoint(LocalDate record) {
		this.record=record;
	}
		
	public long getInterval(TimePoint t2) {
		 long t1D=record.toEpochDay();
		 long t2D=t2.getRecord().toEpochDay();
		 long interval = t2D-t1D;
		 return interval;
	}
	
	public int compareTo(TimePoint t) { //potrebbe non servire più
		return this.record.compareTo(t.getRecord());
	}
	
	public LocalDate getRecord() {
		return record;
	}

	public void setRecord(LocalDate record) {
		this.record = record;
	}
	
}
