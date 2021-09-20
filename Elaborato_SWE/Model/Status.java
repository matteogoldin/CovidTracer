package Model;

public class Status {
	private boolean active;
	
	public Status() {
		active=true;
	}

	public boolean getActive() {
		return active;
	}

	public void setNotActive() {
		active = false;
	}
}
