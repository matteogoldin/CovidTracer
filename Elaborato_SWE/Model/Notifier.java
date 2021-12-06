package Model;

public class Notifier {
	private String id;
	private String psw;
	private NotType nt;
	
	public enum NotType{
		citizen,
		doctor,
		employer,
		school,
		lab,
		other
	}
	
	public Notifier(String id,String psw,NotType nt) {
		this.id=id;
		this.psw=psw;
		this.nt=nt;
	}

}
