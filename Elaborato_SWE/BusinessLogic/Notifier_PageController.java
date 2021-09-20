package BusinessLogic;

import Model.*;
import Model.Contact.risk;
import Model.Result.res;
import Model.Symptom.exhibit;
import MedicalDomain.TypeOfSymptom.sym;


public class Notifier_PageController {
	protected Notifier not;
	private SubjectQueue subQ;
	private History hist;
	
	public Notifier_PageController(Notifier not,SubjectQueue subQ, History hist) {
		this.not=not;
		this.subQ=subQ;
		this.hist=hist;
	}
	
	public void doLogin(){}  //verifica requisiti d'accesso
	
	
	public void addResult(res r,Subject sub,TimePoint refTime,Evidence ev) {
		subQ.addSubject(sub);
		Observation obs=new Result(r,sub,refTime,ev,not);
		sub.addActiveObs(obs);
		hist.add_obs(obs);
	}
	
	public void addContact(Subject secSub,risk rsk,Subject sub,TimePoint refTime,Evidence ev) {
		subQ.addSubject(sub);
		Observation obs=new Contact(secSub,rsk,sub,refTime,ev,not);
		sub.addActiveObs(obs);
		hist.add_obs(obs);
	}
	
	public void addSymptom(sym s,Subject sub,TimePoint refTime,Evidence ev) {
		subQ.addSubject(sub);
		if(s==sym.noSymptom) {
			//se aggiungiamo una osservazione noSymptom controlliamo se ci sono osservazioni di tipo Symptom senza endTime 
			boolean noEndTime=false;
			for(Observation obs : sub.getSymptoms()) {
				Symptom smp=(Symptom) obs;
				if(smp.getEndTime()==null)
					noEndTime=true;
			}
			
			if(sub.getSymptoms().size()!=0 && noEndTime) {
				for(Observation obs : sub.getSymptoms()) { 
				Symptom smp=(Symptom) obs;
					if(smp.getEndTime()==null && smp.getEx()!=exhibit.none) {
						smp.setEndTime(refTime);
					}
				}
			}
			else{
				Observation obs=new Symptom(s,sub,refTime,ev,not);
				sub.addActiveObs(obs);
				hist.add_obs(obs);
			}
			
		}
		else{
			Observation obs=new Symptom(s,sub,refTime,ev,not);
			sub.addActiveObs(obs);
			hist.add_obs(obs);
		}
	}
	
	public void addSymptom(sym s,Subject sub,TimePoint refTime,Evidence ev,TimePoint endTime) {
		//TO-DO: lanciare eccezzione se s==noSymptom
		Observation obs=new Symptom(s,sub,refTime,ev,not,endTime);
		sub.addActiveObs(obs);
		hist.add_obs(obs);
	}


}
