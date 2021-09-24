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
import MedicalDomain.DayThreshold;

public class Tracer_PageController extends Notifier_PageController{


	public Tracer_PageController(Tracer trac, SubjectQueue subQ, History hist) {
		super(trac,subQ,hist);
	}
	
	@Override
	public void doLogin() {} //requisiti di accesso più stringenti rispetto a Notifier
	
	public Prescription analyzeSub(Subject sub) {  //data di interesse impostata di default ad oggi
		TimePoint today=new TimePoint(LocalDate.now());
		ArrayList<Observation> tmpObs=new ArrayList<Observation>();
		boolean reliable=manageObs(sub,tmpObs,today);
		float covidProb=Evaluator.evaluation(sub,tmpObs);
		return addPrescription(sub,covidProb,reliable);
	}
	
	public Prescription analyzeSub(Subject sub, TimePoint intrDate) {
		ArrayList<Observation> tmpObs=new ArrayList<Observation>();
		boolean reliable=manageObs(sub,tmpObs,intrDate);
		float covidProb=Evaluator.evaluation(sub,tmpObs);
		return addPrescription(sub,covidProb,reliable,intrDate);
	}

	private boolean manageObs(Subject sub,ArrayList<Observation> tmpObs,TimePoint intrDate) {
		//gestisce le osservazioni attive di un soggetto, eliminando quelle non più rilevanti e creando delle meta-osservazioni sintesi
		boolean reliable;
		reliable=deleteNotRel(sub,intrDate); //Scarto Obs non rilevanti
		mergeResult(sub,tmpObs,intrDate); //cerca l'unico eventuale Result rimasto e lo inserisce in tmpObs
		mergeSymptoms(sub,tmpObs,intrDate); //crea la meta-osservazione che sintetizza i sintomi
		mergeContacts(sub,tmpObs,intrDate); //crea la meta-osservazione che sintetizza i contatti
		setStatusTrue(sub);
		return reliable;
	}


	public boolean deleteNotRel(Subject sub, TimePoint intrDate) {
		boolean reliable=true;
		ArrayList<Observation> obsDeleteList=new ArrayList<Observation>();
		
		for(Observation obs : sub.getObsList()) {
			 if((obs.getRefTime().getInterval(intrDate)>DayThreshold.covidCourse || obs.getRefTime().getInterval(intrDate)<-DayThreshold.covidCourse) && obs.isActive()) {
				 obs.setStatus(false);
			 }
		}
		
		for(Observation obs : sub.getSymptoms()) {
			Symptom s=(Symptom) obs;
			if(s.getEndTime()!=null && (s.getRefTime().getInterval(intrDate)>DayThreshold.postSymptomCourse || s.getRefTime().getInterval(intrDate)<-DayThreshold.preSymptomCourse) && obs.isActive()) {  
				//a)se i sintomi si sono manifestati 14 o più giorni prima dell data di interesse il soggetto non è più contagioso
				//b)se i sintomi si sono manifestati dopo più di 7 giorni dalla data d'interesse non sono rilevanti
				obs.setStatus(false);
			}
			if(s.getEndTime()==null && obs.isActive() && s.getRefTime().getInterval(intrDate)>DayThreshold.postSymptomCourse) {
				//non sappiamo se i sintomi sono terminati
				reliable=false;
			}
		}

		for(Observation obs : sub.getResults()) { //gestione Result
			TimePoint tp=obs.getRefTime();
			for(Observation src:sub.getObsList()) {
				if(obs.isActive()) {
					if(src.isResult() && tp.getInterval(src.getRefTime())==0 && src.isActive() && obs!=src) {
						//se abbiamo 2+ risultati tampone in uno stesso giorno dobbiamo gestirli
						Result rObs=(Result) obs;
						Result rSrc=(Result) src;
						if(rObs.getR()==rSrc.getR()) {
							//se i risultati sono analoghi ne elimino uno
							src.setStatus(false);
						}
						else {
							//se i risultati sono contrastanti elimino entrambi e dichiaro inaffidabile
							src.setStatus(false);
							obs.setStatus(false);
							if(!obsDeleteList.contains(obs))
								obsDeleteList.add(obs);
							if(!obsDeleteList.contains(src))
								obsDeleteList.add(src);
							reliable=false;
							
						}
					}
					
					if(src.isResult() && intrDate.getInterval(src.getRefTime())<0 && intrDate.getInterval(tp)<0 && tp.getInterval(src.getRefTime())>0 && obs.isActive() && src.isActive()) { 
						//se entrambe i Result precedono intrDate "elimino" i meno recenti
						obs.setStatus(false);
					}
					
					if(src.isResult() && intrDate.getInterval(src.getRefTime())>=0 && intrDate.getInterval(tp)>=0 && tp.getInterval(src.getRefTime())<0 && obs.isActive() && src.isActive()) { 
						//se entrambe i Result succedono intrDate elimino i meno recenti
						obs.setStatus(false);
					}
					
					if(tp.getInterval(src.getRefTime())>0 && intrDate.getInterval(tp)<0 && obs.isActive() && src.isActive()) { 
						//elimino Result negativo precedenti alla intrDate se ho obs successive != noSymptom
						Result r=(Result) obs;
						if(r.getR()==res.negative) {
							if(src.isSymptom()) {
								Symptom s=(Symptom) src;
								if((s.getEx()!=exhibit.none)){
									obs.setStatus(false);
								}
							}
							else {
								obs.setStatus(false);
							}
						}
					}
					if(src.isContact() && intrDate.getInterval(tp)<=0 && tp.getInterval(src.getRefTime())>=-DayThreshold.incubationTime && tp.getInterval(src.getRefTime())<=0 && obs.isActive() && src.isActive()) {
						//eliminare Result negativo se ho contatto precedente all'intrDate recente (entro due giorni)
						Result r=(Result) obs;
						if(r.getR()==res.negative) 
							obs.setStatus(false);
					}			
				}
			}
		}
		
		for(Observation obs: sub.getContacts()) {
			if(obs.getRefTime().getInterval(intrDate)<0) {
				obs.setStatus(false);
			}
		}
		
		/*ArrayList<Observation> ActiveObsCopy=new ArrayList<Observation>(sub.getObsList());
		//la copia serve a evitare la ConcurrentModificationException
		for(Observation obs: ActiveObsCopy) { 
			if(!obs.isActive())
				sub.getObsList().remove(obs);
		}*/
		for(Observation obs: obsDeleteList) {
			sub.getObsList().remove(obs);
		}
		return reliable;	
	}
	
	
	public void mergeResult(Subject sub, ArrayList<Observation> tmpObs, TimePoint intrDate) {
		//la funzione deleteNotRel garantisce che rimangano attive al più due Result (una precedente e una successiva la intrDate)
		ArrayList<Observation> activeResult = sub.getActiveResults();
		if(sub.getActiveResults().size()==0) {return;}
		if(sub.getActiveResults().size()==1) 
			tmpObs.add(activeResult.get(0));
		else {
			Result r1=(Result) activeResult.get(0);
			Result r2=(Result) activeResult.get(1);
			if(r1.getRefTime().getInterval(r2.getRefTime())<0) {
				r1=r2;
				r2=(Result) activeResult.get(0);
			}
			
			if(r1.getR() == r2.getR()) {
				tmpObs.add(new Result(r1.getR(),sub,null,null,not,(r1.getVl()+r2.getVl())/2));
			}
			else {
				//abbiamo due tamponi con esiti discordi
				if(r2.getR() == res.positive && r2.getVl() == 3 && r2.getRefTime().getInterval(intrDate)>-DayThreshold.covidAdvancedState) {
					//se il secondo tampone è positivo e a carica alta e tra la intrDate e la data del tampone...
					//... sono passati meno di 6 giorni il soggetto era già positivo nella intrDate
					tmpObs.add(new Result(res.positive,sub,null,null,not,sub.calcVl(intrDate)));
				}
				else if(r1.getR() == res.positive && r1.getVl() == 3 && r1.getRefTime().getInterval(intrDate)<DayThreshold.covidAdvancedCourse){
					//se entro i 15 giorni precedenti alla intrDate si ha un tampone positivo con carica alta il soggetto è probabilmente ancora positivo
					tmpObs.add(new Result(res.positive,sub,null,null,not,sub.calcVl(intrDate)));
				}
				//si prende il result più recente
				else if(r1.getRefTime().getInterval(intrDate)<-r2.getRefTime().getInterval(intrDate)) {
					tmpObs.add(r1);
				}
				else if(r1.getRefTime().getInterval(intrDate)>-r2.getRefTime().getInterval(intrDate)) {
					tmpObs.add(r2);
				}
				//se non si rientra in nessuno di questi casi non si considerano entrambi i tamponi
			}
		
		}
		
	}
	
	
	public void mergeContacts(Subject sub, ArrayList<Observation> tmpObs, TimePoint intrDate) {
		Subject secSub=null;
		risk rsk=null;
		TimePoint refTime=new TimePoint(intrDate.getRecord().minusDays(30));
		Evidence evd=null;
		float covProb = 0; 
		for(Observation obs:sub.getActiveContacts()) {
			Contact c=(Contact) obs;
			covProb += c.getCovProb();
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
			if(covProb>1)
				covProb=1;
			Observation metaCont=new Contact(secSub,rsk,sub,refTime,evd,not,covProb);
			tmpObs.add(metaCont);
		}
	}

	public void mergeSymptoms(Subject sub, ArrayList<Observation> tmpObs, TimePoint intrDate) {
		exhibit ex=null;
		Evidence evd=null;
		TimePoint refTime=new TimePoint(intrDate.getRecord().minusDays(30));
		sym smp=null;
	
		for(Observation obs:sub.getActiveSymptoms()) {
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
	

	private Prescription addPrescription(Subject sub,float covidProb, boolean reliable) {
		Prescription presc=new Prescription(sub,(Tracer)not,reliable,covidProb);
		return presc;
	}
	
	private Prescription addPrescription(Subject sub,float covidProb, boolean reliable,TimePoint intrDate) {
		Prescription presc=new Prescription(sub,(Tracer)not,reliable,covidProb,intrDate);
		return presc;
	}
	
	private void requestInfo(Subject sub) {} //richiesta di ulteriori info
	
	

	private void setStatusTrue(Subject sub) {
		for(Observation obs: sub.getObsList())
			obs.setStatus(true);
		
	}
}
