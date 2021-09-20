package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BusinessLogic.Notifier_PageController;
import MedicalDomain.TypeOfSymptom.sym;
import Model.*;
import Model.Notifier.NotType;
import Model.Subject.gender;

class Notifier_PageControllerTest {
	TimePoint refTime1,refTime2,refTime3;
	Subject sub;
	Notifier not;
	SubjectQueue sq;
	History hs;
	Notifier_PageController npc;
	

	@BeforeEach
	void setUp(){
		refTime1=new TimePoint(LocalDate.now().minusDays(10));
		refTime2=new TimePoint(LocalDate.now().minusDays(10));
		refTime3=new TimePoint(LocalDate.now().minusDays(5));
		not=new Notifier("123456","paolorossi68",NotType.citizen);
		sub=new Subject("Alessandro","Ugolini","GLNLSMN99T16B036G",LocalDate.of(1999,12,16),gender.male);
		sq=new SubjectQueue();
		sq.addSubject(sub);
		hs=new History();	
		npc=new Notifier_PageController(not,sq,hs);
	}

	@Test
	void addSymptomTest() {
		npc.addSymptom(sym.delirium, sub, refTime1, null);
		npc.addSymptom(sym.diarrhea, sub, refTime2, null);
		npc.addSymptom(sym.noSymptom, sub, refTime3, null);
		assertEquals(2,sub.getObsList().size());
		Symptom s1=(Symptom) sub.getObsList().get(0);
		assertEquals(refTime3.getRecord(),s1.getEndTime().getRecord());
		Symptom s2=(Symptom) sub.getObsList().get(0);
		assertEquals(refTime3.getRecord(),s2.getEndTime().getRecord());
	}

}
