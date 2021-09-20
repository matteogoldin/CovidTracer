package Model;

import Model.Notifier.NotType;

public class Tracer extends Notifier{
	private String id;
	private String psw;
	private NotType nt;
	
	public Tracer(String id,String psw) {
		super(id,psw,NotType.doctor);		
	}
}
