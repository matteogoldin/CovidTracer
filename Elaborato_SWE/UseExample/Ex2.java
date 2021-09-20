package UseExample;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BusinessLogic.Tracer_PageController;
import MedicalDomain.TypeOfSymptom.sym;
import Model.*;
import Model.Evidence.evType;
import Model.Notifier.NotType;
import Model.Subject.gender;

class Ex2 {
	
	//Nessun contatto ravvisato, presenza di sintomi (fever,fatigue,chest pain) 
	
	Subject sub;
	Evidence evd;
	Notifier not;
	Tracer_PageController tpc;
	Tracer tr;
	SubjectQueue sq;
	History hs;

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
		tpc.addSymptom(sym.fever, sub, new TimePoint(LocalDate.now().minusDays(5)), evd);
		tpc.addSymptom(sym.fatigue, sub, new TimePoint(LocalDate.now().minusDays(5)), evd);
		tpc.addSymptom(sym.chestPain, sub, new TimePoint(LocalDate.now().minusDays(5)), evd);
	}

	@Test
	void test() {
		Prescription pr=tpc.analyzeSub(sub);
		float prob=pr.getCovidProb();
		assertTrue((Math.abs(0.13 - prob) < 0.01));
		System.out.println("Prescrizione: "+ pr.getDiag());
		System.out.println("Note: "+ pr.getNotes());
	}

}
