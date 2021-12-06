package Model;

import java.time.LocalDate;
import MedicalDomain.CovidProbThreshold;

public class Prescription {
	private Subject sub;
	private Tracer trac;
	private TimePoint date;
	private TimePoint intrDate;
	private boolean rlb;
	private String diag;
	private String notes;
	private float covidProb;
	
	public Prescription(Subject sub, Tracer trac, boolean rlb, float covidProb) {
		this.sub = sub;
		this.trac = trac;
		this.covidProb=covidProb;
		writeDiag(covidProb);
		writeNotes(rlb);
		date=new TimePoint(LocalDate.now());
		intrDate=date;
	}
	
	public Prescription(Subject sub, Tracer trac, boolean rlb, float covidProb,TimePoint intrDate) {
		//se si vuole conoscere la probabilità che un soggetto fosse positivo in una data passata possiamo soltanto ritornare tale prob senza prescrivere procedure
		this.sub = sub;
		this.trac = trac;
		this.covidProb=covidProb;
		this.intrDate=intrDate;
		diag="Il soggetto nella data d'interesse era positivo con probabilità:"+Float.toString(covidProb);
		notes="";
		date=new TimePoint(LocalDate.now());
	}

	private void writeNotes(boolean rlb) {
		if(!rlb) {
			notes="Le informazioni riguardanti il Soggetto sono inconsistenti o insufficienti per stabilire una diagnosi accurata;"
					+ "si rende neccessario contattare il soggetto per raccogliere nuove informazioni affidabili";
		}else {
			notes="Nessuna nota";
		}
			
	}

	private void writeDiag(float covidProb) {
		if(covidProb<CovidProbThreshold.lowRisk) {
			diag="Il Soggetto è considerato non a rischio; non sono previsti ulteriori adempimenti \n";
		}else if(covidProb<CovidProbThreshold.mediumRisk) {
			diag="Il Soggetto è considerato potenzialmente a rischio; "
					+ "il Soggetto deve rispettare l'isolamento domestico fino all'ottenimento di un tampone con esito negativo \n";
		}else {
			diag="Il Soggetto è considerato ad alto rischio;"
					+ "il Soggetto deve sottoporsi a un tampone e, indipendentemente dall'esito, rispettare l'isolamento domestico "
					+ "per 7 giorni, al termine dei quali sottoporsi a un nuovo tampone \n";
		}
	}

	public float getCovidProb() {
		return covidProb;
	}

	public String getDiag() {
		return diag;
	}

	public void setDiag(String diag) {
		this.diag = diag;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
