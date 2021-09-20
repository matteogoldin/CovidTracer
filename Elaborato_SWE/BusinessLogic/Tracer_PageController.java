package BusinessLogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import MedicalDomain.Evaluator;
import MedicalDomain.TypeOfSymptom.sym;
import Model.*;
import Model.Contact.risk;
import Model.Result.res;
import Model.Symptom.exhibit;

public class Tracer_PageController extends Notifier_PageController{


	public Tracer_PageController(Tracer trac, SubjectQueue subQ, History hist) {
		super(trac,subQ,hist);
	}
	
	@Override
	public void doLogin() {} //requisiti di accesso più stringenti rispetto a Notifier
	
	public Prescription analyzeSub(Subject sub) {
		ArrayList<Observation> tmpObs=new ArrayList<Observation>();
		boolean reliable=manageObs(sub,tmpObs);
		float covidProb=Evaluator.evaluation(sub,tmpObs);
		return addPrescription(sub,covidProb,reliable);
	}

	private boolean manageObs(Subject sub,ArrayList<Observation> tmpObs) {
		//gestisce le osservazioni attive di un soggetto, eliminando quelle non più rilevanti e creando delle meta-osservazioni sintesi
		boolean reliable;
		reliable=deleteNotRel(sub); //Scarto Obs non rilevanti
		searchResult(sub,tmpObs); //cerca l'unico eventuale Result rimasto e lo inserisce in tmpObs
		mergeSymptoms(sub,tmpObs); //crea la meta-osservazione che sintetizza i sintomi
		mergeContacts(sub,tmpObs); //crea la meta-osservazione che sintetizza i contatti
		return reliable;
	}

	public boolean deleteNotRel(Subject sub) {
		TimePoint today=new TimePoint(LocalDate.now()); 
		boolean reliable=true;
		
		for(Observation obs : sub.getActiveObs()) {
			 if(obs.getRefTime().getInterval(today)>28 && obs.isActive()) {
				 obs.setStatus();
			 }
		}
		
		for(Observation obs : sub.getSymptoms()) {
			Symptom s=(Symptom) obs;
			if(s.getEndTime()!=null && s.getRefTime().getInterval(today)>14 && obs.isActive()) {  
				//se i sintomi si sono manifestati 14 o più giorni prima il soggetto non è più contagioso
				obs.setStatus();
			}
			if(s.getEndTime()==null && obs.isActive() && s.getRefTime().getInterval(today)>14) {
				//non sappiamo se i sintomi sono terminati
				reliable=false;
			}
		}

		for(Observation obs : sub.getResults()) { //gestione Result
			TimePoint tp=obs.getRefTime();
			for(Observation src:sub.getActiveObs()) {
				if(obs.isActive()) {
					if(src.isResult() && tp.getInterval(src.getRefTime())==0 && src.isActive() && obs!=src) {
						//se abbiamo 2+ risultati tampone in uno stesso giorno dobbiamo gestirli
						Result rObs=(Result) obs;
						Result rSrc=(Result) src;
						if(rObs.getR()==rSrc.getR()) {
							//se i risultati sono analoghi ne elimino uno
							src.setStatus();
						}
						else {
							//se i risultati sono contrastanti elimino entrambi e dichiaro inaffidabile
							src.setStatus();
							obs.setStatus();
							reliable=false;
						}
					}
					if(src.isResult() && tp.getInterval(src.getRefTime())>0 && obs.isActive() && src.isActive()) { 
						//eliminiamo i result meno recenti
						obs.setStatus();
					}
					if(tp.getInterval(src.getRefTime())>0 && obs.isActive() && src.isActive()) { 
						//elimino Result negativo se ho obs successive != noSymptom
						Result r=(Result) obs;
						if(r.getR()==res.negative) {
							if(src.isSymptom()) {
								Symptom s=(Symptom) src;
								if((s.getEx()!=exhibit.none)){
									obs.setStatus();
								}
							}
							else {
								obs.setStatus();
							}
						}
					}
					if(src.isContact() && tp.getInterval(src.getRefTime())>=-2 && tp.getInterval(src.getRefTime())<=0 && obs.isActive() && src.isActive()) {
						//eliminare Result negativo se ho contatto precedente recente (entro due giorni)
						Result r=(Result) obs;
						if(r.getR()==res.negative) 
							obs.setStatus();
					}			
				}
			}
		}
		ArrayList<Observation> ActiveObsCopy=new ArrayList<Observation>(sub.getActiveObs());
		//la copia serve a evitare la ConcurrentModificationException
		for(Observation obs: ActiveObsCopy) { 
			if(!obs.isActive())
				sub.getActiveObs().remove(obs);
		}	
		return reliable;	
	}
	
	
	public void mergeContacts(Subject sub, ArrayList<Observation> tmpObs) {
		//TO-DO: capire se può essere utile insierire metodo getContacts() e isContact() 
		Subject secSub=null;
		risk rsk=null;
		TimePoint refTime=new TimePoint(LocalDate.now().minusDays(30));
		Evidence evd=null;
		for(Observation obs:sub.getContacts()) {
			Contact c=(Contact) obs;
			if(c.getRsk()==risk.high) {
				rsk=risk.high;
				if(c.getRefTime().getInterval(refTime)<0) {
					secSub=c.getSecondarySub();
					refTime=c.getRefTime();
					evd=c.getEv();
				}					
			}
			if(c.getRsk()==risk.medium && rsk!=risk.high) {
				rsk=risk.medium;
				if(c.getRefTime().getInterval(refTime)<0) {
					secSub=c.getSecondarySub();
					refTime=c.getRefTime();
					evd=c.getEv();
				}					
			}
			if(c.getRsk()==risk.low && rsk!=risk.high && rsk!=risk.medium) {
				rsk=risk.low;
				if(c.getRefTime().getInterval(refTime)<0) {
					secSub=c.getSecondarySub();
					refTime=c.getRefTime();
					evd=c.getEv();
				}					
			}
		}
		if(rsk!=null) {
			Observation metaCont=new Contact(secSub,rsk,sub,refTime,evd,not);
			tmpObs.add(metaCont);
		}
	}

	public void mergeSymptoms(Subject sub, ArrayList<Observation> tmpObs) {
		//FACOLTATIVO: scalare rischio sulla base dell'evidenza
		exhibit ex=null;
		Evidence evd=null;
		TimePoint refTime=new TimePoint(LocalDate.now().minusDays(30));
		sym smp=null;
	
		for(Observation obs:sub.getSymptoms()) {
			Symptom s=(Symptom) obs;
			if(s.getEx()==exhibit.highRisk) {
				ex=exhibit.highRisk;
				refTime=s.getRefTime();
				evd=s.getEv();
				smp=s.getS();
			}
			if(s.getEx()==exhibit.mediumRisk && ex!=exhibit.highRisk) {
				ex=exhibit.mediumRisk;
				refTime=s.getRefTime();
				evd=s.getEv();
				smp=s.getS();
			}
			if(s.getEx()==exhibit.lowRisk && ex!=exhibit.mediumRisk && ex!=exhibit.highRisk) {
				ex=exhibit.lowRisk;
				refTime=s.getRefTime();
				evd=s.getEv();
				smp=s.getS();
			}
			if(s.getEx()==exhibit.none && ex!=exhibit.lowRisk && ex!=exhibit.mediumRisk && ex!=exhibit.highRisk) {
				ex=exhibit.none;
				refTime=s.getRefTime();
				evd=s.getEv();
				smp=s.getS();
			}
		}
		if(ex!=null) {
			Observation metaSym=new Symptom(smp,sub,refTime,evd,not);
			tmpObs.add(metaSym);
		}
	}

	public void searchResult(Subject sub, ArrayList<Observation> tmpObs) {
		for(Observation obs:sub.getResults()) {
				tmpObs.add(obs);
		}
	}
	

	private Prescription addPrescription(Subject sub,float covidProb, boolean reliable) {
		Prescription presc=new Prescription(sub,(Tracer)not,reliable,covidProb);
		return presc;
	}
	
	private void requestInfo(Subject sub) {} //richiesta di ulteriori info

}
