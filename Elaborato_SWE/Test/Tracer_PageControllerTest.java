package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import BusinessLogic.Tracer_PageController;
import MedicalDomain.TypeOfSymptom.sym;
import Model.*;
import Model.Contact.risk;
import Model.Evidence.evType;
import Model.Notifier.NotType;
import Model.Result.res;
import Model.Subject.gender;
import Model.Symptom.exhibit;

class Tracer_PageControllerTest {
	Subject sub;
	Evidence evd;
	Notifier not;
	Tracer_PageController tpc;
	Tracer tr;
	SubjectQueue sq;
	History hs;
	Subject secSub;
	TimePoint today;
	

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
		today=new TimePoint(LocalDate.now());
	}

	@Nested
	class deleteNotRelTests{
		
		@Test
		void deleteOldObs() {
			TimePoint refTime=new TimePoint(LocalDate.now().minusDays(30));
			tpc.addResult(res.positive, sub, refTime, evd,2);
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(15));
			tpc.addSymptom(sym.fever, sub, refTime2, evd);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(sub.getObsList().size(),1);
			assertTrue(sub.getObsList().get(0).isSymptom());
		}
		
		@Test
		void deleteOldSymptoms() {
			TimePoint refTime1=new TimePoint(LocalDate.now().minusDays(15));
			TimePoint endTime1=new TimePoint(LocalDate.now());
			tpc.addSymptom(sym.fever, sub, refTime1, evd,endTime1);
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(10));
			TimePoint endTime2=new TimePoint(LocalDate.now());
			tpc.addSymptom(sym.fever, sub, refTime2, evd,endTime2);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(1,sub.getObsList().size());
			assertEquals(sub.getObsList().get(0).getRefTime(),refTime2);
		}
		
		@Test
		void moreResultSameDate() {
			TimePoint refTime=new TimePoint(LocalDate.now());
			tpc.addResult(res.positive, sub, refTime, evd,3);
			tpc.addResult(res.negative, sub, refTime, evd,3);
			boolean reliable=tpc.deleteNotRel(sub,refTime);
			assertFalse(reliable);
			assertEquals(0,sub.getObsList().size());
			tpc.addResult(res.positive, sub, refTime, evd,2);
			tpc.addResult(res.positive, sub, refTime, evd,2);
			reliable=tpc.deleteNotRel(sub,refTime);
			assertTrue(reliable);
			assertEquals(1,sub.getObsList().size());
		}
		
		@Test
		void deleteSubsumedResult() {
			TimePoint refTime1=new TimePoint(LocalDate.now());
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(10));
			tpc.addResult(res.positive, sub, refTime1, evd,2);
			tpc.addResult(res.negative, sub, refTime2, evd,3);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(1,sub.getObsList().size());
			assertEquals(refTime1,sub.getObsList().get(0).getRefTime());
		}
		
		@Test
		void notDeleteResultNoSymptom() {
			TimePoint refTime1=new TimePoint(LocalDate.now());
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(10));
			tpc.addResult(res.negative, sub, refTime2, evd,3);
			tpc.addSymptom(sym.noSymptom, sub, refTime1, evd);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(2,sub.getObsList().size());
		}
		
		@Test
		void deleteDeprecatedResult() {
			TimePoint refTime1=new TimePoint(LocalDate.now());
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(10));
			tpc.addResult(res.negative, sub, refTime2, evd,3);
			tpc.addSymptom(sym.abdominalPain, sub, refTime1, evd);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(1,sub.getObsList().size());
			assertTrue(sub.getObsList().get(0).isSymptom());
		}
		
		@Test
		void deleteResultRecentContacts() {
			TimePoint refTime1=new TimePoint(LocalDate.now());
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(2));
			tpc.addResult(res.negative, sub, refTime1, evd,3);
			tpc.addContact(secSub,risk.high,sub,refTime2,evd);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(1,sub.getObsList().size());
			assertTrue(sub.getObsList().get(0).isContact());
		}
		
		@Test
		void notDeleteResultOldContact() {
			TimePoint refTime1=new TimePoint(LocalDate.now());
			TimePoint refTime2=new TimePoint(LocalDate.now().minusDays(4));
			tpc.addResult(res.negative, sub, refTime1, evd,1);
			tpc.addContact(secSub,risk.high,sub,refTime2,evd);
			tpc.deleteNotRel(sub,new TimePoint(LocalDate.now()));
			assertEquals(2,sub.getObsList().size());
		}
		
	}
	
	@Nested
	class mergeContactTests{
		TimePoint refTimeCh1,refTimeCh2,refTimeCm,refTimeCl;
		ArrayList<Observation> tmpObs;
		
		@BeforeEach
		void setUpMergeContact() {
			refTimeCh1=new TimePoint(LocalDate.now().minusDays(10));
			refTimeCh2=new TimePoint(LocalDate.now().minusDays(9));
			refTimeCm=new TimePoint(LocalDate.now().minusDays(8));
			refTimeCl=new TimePoint(LocalDate.now().minusDays(7));
			tmpObs=new ArrayList<Observation>();
		}
		
		@Test
		void HighRiskContact() {
			tpc.addContact(sub, risk.high, sub,refTimeCh1, evd);
			tpc.addContact(secSub, risk.high, sub,refTimeCh2, evd);
			tpc.addContact(secSub, risk.medium, sub,refTimeCm, evd);
			tpc.addContact(secSub, risk.low, sub,refTimeCl, evd);
			tpc.mergeContacts(sub, tmpObs,new TimePoint(LocalDate.now()));
			assertEquals(1,tmpObs.size());
			assertTrue(tmpObs.get(0).isContact());
			Contact c=(Contact) tmpObs.get(0);
			assertEquals(risk.high,c.getRsk());
			assertEquals(secSub,c.getSecondarySub());
			assertEquals(refTimeCh2.getRecord(),c.getRefTime().getRecord());
		}
		
		@Test
		void NoContact() {
			tpc.addResult(res.negative, sub, refTimeCl, evd,3);
			tpc.mergeContacts(sub, tmpObs,new TimePoint(LocalDate.now()));
			assertEquals(0,tmpObs.size());
		}		
	}
	
	@Nested
	class mergeSymptomsTests{
		TimePoint refTimeSh1,refTimeSh2,refTimeSm,refTimeSl,refTimeSn;
		ArrayList<Observation> tmpObs;
		
		@BeforeEach
		void setUpMergeContact() {
			refTimeSh1=new TimePoint(LocalDate.now().minusDays(10));
			refTimeSh2=new TimePoint(LocalDate.now().minusDays(9));
			refTimeSm=new TimePoint(LocalDate.now().minusDays(8));
			refTimeSl=new TimePoint(LocalDate.now().minusDays(7));
			refTimeSn=new TimePoint(LocalDate.now().minusDays(6));
			tmpObs=new ArrayList<Observation>();
		}
		
		@Test
		void HighRiskSymptom() {
			tpc.addSymptom(sym.cough, sub, refTimeSh1, evd);
			tpc.addSymptom(sym.cough, sub, refTimeSh2, evd);
			tpc.addSymptom(sym.fever, sub, refTimeSm, evd);
			tpc.addSymptom(sym.fatigue, sub, refTimeSl, evd);
			tpc.addSymptom(sym.noSymptom, sub, refTimeSn, evd);
			tpc.mergeSymptoms(sub, tmpObs,new TimePoint(LocalDate.now()));
			assertEquals(1,tmpObs.size());
			assertTrue(tmpObs.get(0).isSymptom());
			Symptom s=(Symptom) tmpObs.get(0);
			assertEquals(exhibit.highRisk,s.getEx());
			assertEquals(refTimeSh2.getRecord(),s.getRefTime().getRecord());
		}
		
		@Test
		void NoSymptomObs() {
			tpc.addResult(res.negative, sub, refTimeSl, evd,2);
			tpc.mergeContacts(sub, tmpObs,new TimePoint(LocalDate.now()));
			assertEquals(0,tmpObs.size());
		}	
	}
	
	@Nested
	class searchResultTests{
		TimePoint refTime1,refTime2,refTime3;
		ArrayList<Observation> tmpObs;
		
		@BeforeEach
		void setUpMergeContact() {
			refTime1=new TimePoint(LocalDate.now().minusDays(10));
			refTime2=new TimePoint(LocalDate.now().minusDays(9));
			refTime3=new TimePoint(LocalDate.now().minusDays(8));
			tmpObs=new ArrayList<Observation>();
		}
		@Test
		void resultFound() {
			tpc.addResult(res.negative, sub, refTime1, evd,2);
			tpc.addResult(res.positive, sub, refTime2, evd,2);
			tpc.addResult(res.negative, sub, refTime3, evd,2);
			tpc.deleteNotRel(sub,today);
			tpc.mergeResult(sub, tmpObs,today);
			assertEquals(1,tmpObs.size());
			assertTrue(tmpObs.get(0).isResult());
		}
		
		void noResultFound() {	
			tpc.deleteNotRel(sub,today);
			tpc.mergeResult(sub, tmpObs,today);
			assertEquals(0,tmpObs.size());
		}
	}
}
