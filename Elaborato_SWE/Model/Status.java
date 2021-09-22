package Model;

public class Status {
	private boolean active;
	
	public Status() {
		active = true;
	}
	
	public Status(boolean active) {
		this.active = active;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean bool) {
		active = bool;
	}
}
