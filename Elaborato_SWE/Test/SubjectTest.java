package Test;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import MedicalDomain.TypeOfSymptom.sym;
import Model.*;
import Model.Contact.risk;
import Model.Evidence.evType;
import Model.Notifier.NotType;
import Model.Result.res;
import Model.Subject.gender;

public class SubjectTest {
	Subject sub;
	
	@BeforeEach
	public void setUp() {
		sub=new Subject("Alessandro","Ugolini","GLNLSMN99T16B036G",LocalDate.of(1999,12,16),gender.male);
		Observation obs1=createContact(sub);
		Observation obs2=createSymptom(sub);
		Observation obs3=createResult(sub);
		sub.addObs(obs1);
		sub.addObs(obs2);
		sub.addObs(obs3);
	}
	
	
	@Test
	public void getOneSymptomsTest() {
		
		ArrayList<Observation> symList=sub.getSymptoms();
		assertEquals("Same length",1,symList.size());
		assertTrue(symList.get(0).isSymptom());
	}
	
	@Test
	public void getNoSymptoms() {
		sub.getObsList().remove(1);
		ArrayList<Observation> symList=sub.getSymptoms();
		assertEquals("Same length",0,symList.size());
	}	
	
	@Test
	public void getOneResultsTest() {
		ArrayList<Observation> resList=sub.getResults();
		assertEquals("Same length",1,resList.size());
		assertTrue(resList.get(0).isResult());
	}
	
	@Test
	public void getNoResultsTest() {
		sub.getObsList().remove(2);
		ArrayList<Observation> resList=sub.getResults();
		assertEquals("Same length",0,resList.size());
	}
	
	@Test
	public void getOneContactsTest() {
		ArrayList<Observation> contactList=sub.getContacts();
		assertEquals("Same length",1,contactList.size());
		assertTrue(contactList.get(0).isContact());
	}
	
	@Test
	public void getNoContactTest() {
		sub.getObsList().remove(0);
		ArrayList<Observation> contactList=sub.getContacts();
		assertEquals("Same length",0,contactList.size());
	}
	
	public Contact createContact(Subject sub) {
		Subject secSub=new Subject("Matteo","Goldin","GLDMTT99H13B036X",LocalDate.of(1999,6,13),gender.male);
		risk rsk=risk.high;
		TimePoint refTime=new TimePoint(LocalDate.now());
		Evidence evd=new Evidence(evType.reported);
		Notifier not=new Notifier("123456","paolorossi68",NotType.employer);
		Contact c=new Contact(secSub,rsk,sub,refTime,evd,not);
		return c;
	}
	
	public Symptom createSymptom(Subject sub) {
		TimePoint refTime=new TimePoint(LocalDate.now());
		Evidence evd=new Evidence(evType.reported);
		Notifier not=new Notifier("123456","paolorossi68",NotType.citizen);
		Symptom s=new Symptom(sym.fever,sub,refTime,evd,not);
		return s;
	}
	
	public Result createResult(Subject sub) {
		TimePoint refTime=new TimePoint(LocalDate.now());
		Evidence evd=new Evidence(evType.reported);
		Notifier not=new Notifier("123456","paolorossi68",NotType.citizen);
		Result r=new Result(res.positive,sub,refTime,evd,not,2);
		return r;
	}
	
	
	
	

}
