package Model;

import Model.Notifier.NotType;

public class Tracer extends Notifier{
	public Tracer(String id,String psw) {
		super(id,psw,NotType.doctor);		
	}
}
