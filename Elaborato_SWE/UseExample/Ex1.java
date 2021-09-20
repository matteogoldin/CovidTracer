package UseExample;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import Model.*;
import Model.Contact.risk;
import Model.Evidence.evType;
import Model.Notifier.NotType;
import Model.Result.res;
import Model.Subject.gender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BusinessLogic.Tracer_PageController;
import MedicalDomain.TypeOfSymptom.sym;

class Ex1 {
	// Soggetto1 è stato in contatto (alto rischio) con Soggetto2 positivo due settimane fa, 
	//non ha presentato sintomi sottoponendosi a tampone è risultato positivo
	
	Subject sub;
	Evidence evd;
	Notifier not;
	Tracer_PageController tpc;
	Tracer tr;
	SubjectQueue sq;
	History hs;
	Subject secSub;	
	
	

	@BeforeEach
	void setUp() throws Exception {
		sub=new Subject("Alessandro","Ugolini","GLNLSMN99T16B036G",LocalDate.of(1999,12,16),gender.male);
		evd=new Evidence(evType.reported);
		not=new Notifier("123456","paolorossi68",NotType.citizen);
		tr=new Tracer("DoctorHouse","dothouse");
		sq=new SubjectQueue();
		sq.addSubject(sub);
		hs=new History();	
		tpc=new Tracer_PageController(tr,sq,hs);
		secSub=new Subject("Matteo","Goldin","GLDMTT99H13B036X",LocalDate.of(1999,6,13),gender.male);
		tpc.addContact(secSub,risk.high,sub,new TimePoint(LocalDate.now().minusDays(14)), evd);
		tpc.addSymptom(sym.noSymptom, sub, new TimePoint(LocalDate.now().minusDays(5)), evd);
		tpc.addResult(res.positive, sub,new TimePoint(LocalDate.now().minusDays(7)),evd);
	}

	@Test
	void test() {
		Prescription pr=tpc.analyzeSub(sub);
		float prob=pr.getCovidProb();
		assertTrue((Math.abs(0.95 - prob) < 0.01));
		System.out.println("Prescrizione: "+ pr.getDiag());
		System.out.println("Note: "+ pr.getNotes());
	}

}
