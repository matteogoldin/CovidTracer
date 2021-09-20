package Model;

import java.util.ArrayList;

public class History {
	private ArrayList<Observation> obs_collection;
	
	public History() {
		obs_collection=new ArrayList<Observation>();
	} 
	
	public void add_obs(Observation obs) {
		obs_collection.add(obs);
	}
	
	
}
