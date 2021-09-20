package Model;

import java.time.LocalDate;

public class Prescription {
	Subject sub;
	Tracer trac;
	TimePoint date;
	boolean rlb;
	String diag;
	String notes;
	float covidProb;
	
	public Prescription(Subject sub, Tracer trac, boolean rlb, float covidProb) {
		this.sub = sub;
		this.trac = trac;
		this.covidProb=covidProb;
		writeDiag(covidProb);
		writeNotes(rlb);
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
		if(covidProb<0.11) {  //TODO parametri nel medical domain
			diag="Il Soggetto è considerato non a rischio; non sono previsti ulteriori adempimenti \n";
		}else if(covidProb<0.19) {
			diag="Il Soggetto è considerato potenzialmente a rischio; "
					+ "il Soggetto deve rispettare l'isolamento domestico fino all'ottenimento di un tampone con esito negativo \n";
		}else {
			diag="Il Soggetto è considerato ad alto rischio;"
					+ "il Soggetto deve sottoporsi a un tampone e, indipendentemente dall'esito, rispettare l'isolamento domestico "
					+ "per 15 giorni, al termine dei quali sottoporsi a un nuovo tampone \n";
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
