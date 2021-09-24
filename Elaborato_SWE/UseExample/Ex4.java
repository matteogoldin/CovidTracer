package UseExample;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BusinessLogic.Tracer_PageController;
import MedicalDomain.TypeOfSymptom.sym;
import Model.Evidence;
import Model.History;
import Model.Notifier;
import Model.Prescription;
import Model.Subject;
import Model.SubjectQueue;
import Model.TimePoint;
import Model.Tracer;
import Model.Contact.risk;
import Model.Evidence.evType;
import Model.Notifier.NotType;
import Model.Result.res;
import Model.Subject.gender;

public class Ex4 {
	/* Vogliamo ottenere la probabilità che un soggetto fosse positivo in una certa data (nel passato).Su di lui si hanno le 
	 seguenti Osservazioni: due contatti ad alto rischio con soggetti positivi con una certa prob (<1), sintomi 
	 (fever,hoarseVoice,cough) manifestati dopo due giorni e terminati dopo una settimana, due tamponi (uno precedente alla data
	 d'interesse e negativo l'altro successivo e positivo) */
	Subject sub;
	Evidence evd;
	Notifier not;
	Tracer_PageController tpc;
	Tracer tr;
	SubjectQueue sq;
	History hs;
	Subject secSub1;
	Subject secSub2;
	TimePoint intrDate;

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
		secSub1=new Subject("Matteo","Goldin","GLDMTT99H13B036X",LocalDate.of(1999,6,13),gender.male);
		secSub2=new Subject("Gianna","Giannetti","GNNGNN70T44D612Z",LocalDate.of(1970,12,4),gender.female);
		intrDate=new TimePoint(LocalDate.now().minusDays(30));
		
		tpc.addContact(secSub1, risk.high, sub, new TimePoint(intrDate.getRecord().minusDays(20)), evd,(float) 0.5);
		tpc.addContact(secSub2, risk.high, sub, new TimePoint(intrDate.getRecord().minusDays(10)), evd,(float) 0.3);
		tpc.addResult(res.negative, sub, new TimePoint(intrDate.getRecord().minusDays(7)), evd,0);
		tpc.addResult(res.positive, sub, new TimePoint(intrDate.getRecord().plusDays(5)), evd,3);
		tpc.addSymptom(sym.fever, sub, new TimePoint(intrDate.getRecord().minusDays(8)), evd,new TimePoint(intrDate.getRecord().minusDays(1)));
		tpc.addSymptom(sym.hoarseVoice, sub, new TimePoint(intrDate.getRecord().minusDays(8)), evd,new TimePoint(intrDate.getRecord().minusDays(1)));
		tpc.addSymptom(sym.cough, sub, new TimePoint(intrDate.getRecord().minusDays(8)), evd,new TimePoint(intrDate.getRecord().minusDays(1)));
	}

	@Test
	void test() {
		Prescription pr=tpc.analyzeSub(sub,intrDate);
		float prob=pr.getCovidProb();
		assertTrue((Math.abs(0.796 - prob) < 0.01));
		System.out.println("Prescrizione: "+ pr.getDiag());
		System.out.println("Note: "+ pr.getNotes());
	
	}

}
