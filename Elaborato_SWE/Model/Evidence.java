package Model;

public class Evidence {
	public enum evType{
		ascertained,
		reported
	}
	private evType ev;
	
	public Evidence(evType ev) {
		this.ev=ev;
	}

	public evType getEv() {
		return ev;
	}
	
	
	
}
