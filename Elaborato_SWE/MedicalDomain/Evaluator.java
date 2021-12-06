package MedicalDomain;

import java.io.File;
import java.util.ArrayList;
import Model.*;
import Model.Contact.risk;
import Model.Result.res;
import Model.Subject.gender;
import Model.Symptom.exhibit;
import unbbayes.prs.Node;
import unbbayes.prs.bn.*;
import unbbayes.io.*;

public class Evaluator {

	public static float evaluation(Subject sub,ArrayList<Observation> tmpObs) {
		//Step 1: sulla base del soggetto selezionare la bn corretta
		//Step 2: applicare l'inferenza sulla rete scelta basandosi su tmpObs
		//Step 3: ritornare la probabilità della v.a. Covid
		ProbabilisticNetwork net;
		try{
			net=selectBN(sub);
		}
		catch(Exception e){
			System.out.println("File not found");
			return -1;
		}
		return spreadEvidence(tmpObs,net);
	}
	
	private static ProbabilisticNetwork selectBN (Subject sub) throws Exception{
		ProbabilisticNetwork net;
		int age=sub.getAge();
		gender g=sub.getGnd();
		
		if(age<30) {
			if(g==gender.male) {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}else {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}
			
		}else if(age<60){
			if(g==gender.male) {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}else {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}
		}else {
			if(g==gender.male) {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}else {
				net=( ProbabilisticNetwork )new NetIO ().load(new File("./CovidBN.net"));
			}	
		}
		return net;
	}
	
	public static float spreadEvidence(ArrayList<Observation> tmpObs,ProbabilisticNetwork net) {
		JunctionTreeAlgorithm jTree=new JunctionTreeAlgorithm();
		jTree.setNet(net);
		jTree.run();
		Contact c=null;
		for(Observation obs:tmpObs) {
			if(obs.isResult()) {
				Result r=(Result)obs;
				ProbabilisticNode result =( ProbabilisticNode )net.getNode("Result");
				if(r.getR()==res.positive) {
					result.addFinding(0);
				}else if(r.getR()==res.negative){
					result.addFinding(1);
				}
				
			}else if(obs.isSymptom()) {
				Symptom s=(Symptom) obs;
				ProbabilisticNode symptom =( ProbabilisticNode )net.getNode("Symptoms");
				if(s.getEx()==exhibit.lowRisk) {
					symptom.addFinding(0);
				}else if(s.getEx()==exhibit.mediumRisk){
					symptom.addFinding(1);
				}else if(s.getEx()==exhibit.highRisk) {
					symptom.addFinding(2);
				}else if(s.getEx()==exhibit.none) {
					symptom.addFinding(3);
				}
				
			}else if(obs.isContact()){
				c=(Contact) obs;
				ProbabilisticNode contact =( ProbabilisticNode )net.getNode("Contacts");
				if(c.getRsk()==risk.low) {
					contact.addFinding(0);
				}else if(c.getRsk()==risk.medium) {
					contact.addFinding(1);
				}else if(c.getRsk()==risk.high) {
					contact.addFinding(2);
				}
			}	
		}
		try {
			net.updateEvidences();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		float covidProb=((ProbabilisticNode) net.getNode("Covid19")).getMarginalAt(0);
		if(c!=null)
			covidProb*=c.getCovProb();
		return covidProb;
		/*for(Node node:net.getNodes()) {  //stampa probabilità
			System.out.println( node.getDescription( ) ) ;
			for ( int i = 0 ; i < node . getStatesSize( ) ; i ++ ) {
			System.out.println ( node.getStateAt ( i ) + " : " + ( ( ProbabilisticNode ) node ) . getMarginalAt ( i ) ) ; }
		}*/
		
	}
}


