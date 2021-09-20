package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import BusinessLogic.Tracer_PageController;
import MedicalDomain.Evaluator;
import MedicalDomain.TypeOfSymptom.sym;
import Model.Evidence;
import Model.History;
import Model.Observation;
import Model.Subject;
import Model.SubjectQueue;
import Model.TimePoint;
import Model.Tracer;
import Model.Evidence.evType;
import Model.Result.res;
import Model.Subject.gender;
import unbbayes.prs.bn.ProbabilisticNetwork;

class EvaluatorTest {
	Subject sub;
	ArrayList<Observation> tmpObs;
	Tracer_PageController tpc;
	Tracer tr;
	SubjectQueue sq;
	History hs;
	Evidence evd;

	@BeforeEach
	void setUp(){
		sub=new Subject("Alessandro","Ugolini","GLNLSMN99T16B036G",LocalDate.of(1999,12,16),gender.male);
		tmpObs=new ArrayList<Observation>();
		tr=new Tracer("DoctorHouse","dothouse");
		sq=new SubjectQueue();
		sq.addSubject(sub);
		hs=new History();	
		tpc=new Tracer_PageController(tr,sq,hs);
	    evd=new Evidence(evType.reported);
	}

	@Test
	void FileFoundTest() {
		TimePoint refTime=new TimePoint(LocalDate.now().minusDays(15));
		tpc.addResult(res.positive, sub, refTime, evd);
		tpc.addSymptom(sym.fever, sub, refTime, evd);
		tmpObs.add(sub.getActiveObs().get(0));
		tmpObs.add(sub.getActiveObs().get(1));
		Evaluator.evaluation(sub,tmpObs);
	}

}
