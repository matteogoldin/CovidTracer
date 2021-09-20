package MedicalDomain;

import Model.Symptom.exhibit;

public abstract class TypeOfSymptom {
	public enum sym{
		fever,
		cough,
		tasteSmellLoss,
		fatigue,
		shortBreath,
		diarrhea,
		delirium,
		skippedMeals,
		abdominalPain,
		chestPain,
		hoarseVoice,
		noSymptom
	}
	
	public static exhibit classifySymptom(sym s) { //classifica i sintomi per fascia di rischio
		exhibit ex=exhibit.none;
		if(s==sym.tasteSmellLoss || s==sym.cough) {
			ex=exhibit.highRisk;
		}	
		if(s==sym.fever || s==sym.skippedMeals || s==sym.chestPain || s==sym.hoarseVoice) {
			ex=exhibit.mediumRisk;
		}
		if(s==sym.fatigue || s==sym.shortBreath || s==sym.diarrhea || s==sym.delirium || s==sym.abdominalPain) {
			ex=exhibit.lowRisk;
		}
		return ex;		
	}
	

}
