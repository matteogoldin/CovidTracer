package Model;
import java.util.ArrayList;
import java.time.*;

public class Subject {
	public enum gender{
		male,
		female;
	}

	private final String name;
	private final String surname;
	private final String cf;
	private final LocalDate birthDate;
	private final gender gnd;
	private ArrayList<Observation> activeObs;

	public Subject(String name, String surname, String cf,LocalDate birthDate,gender gnd) {
		this.name=name;
		this.surname=surname;
		this.cf=cf;
		this.birthDate=birthDate;
		this.gnd=gnd;
		activeObs=new ArrayList<Observation>();
	}
	
	public void addActiveObs(Observation obs) {
		activeObs.add(obs);
	}

	public ArrayList<Observation> getActiveObs() {
		return activeObs;
	}
	
	public ArrayList<Observation> getContacts(){
		ArrayList<Observation> conts=new ArrayList<Observation>();
		for(Observation obs:this.activeObs) {
			if(obs.isContact())
				conts.add(obs);
		}
		return conts;
	}
	
	public ArrayList<Observation> getSymptoms(){
		ArrayList<Observation> symps=new ArrayList<Observation>();
		for(Observation obs:this.activeObs) {
			if(obs.isSymptom())
				symps.add(obs);
		}
		return symps;
	}
	
	public ArrayList<Observation> getResults(){
		ArrayList<Observation> results=new ArrayList<Observation>();
		for(Observation obs:this.activeObs) {
			if(obs.isResult())
				results.add(obs);
		}
		return results;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public gender getGnd() {
		return gnd;
	}
	
	public int getAge() {
		LocalDate currentDate=LocalDate.now();
		return Period.between(birthDate, currentDate).getYears();
	}
	
}
