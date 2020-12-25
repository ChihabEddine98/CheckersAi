package src.model;

import java.util.HashMap;

public class StateSet {
	HashMap<String,Integer> set;
	public StateSet() {
		this.set = new HashMap<String,Integer>();
	}
	
	public void add(Game etat, int value) {
		this.set.put(etat.toString(),Integer.valueOf(value));
	}
	
	public Integer getValue(Game etat) {
		return this.set.get(etat.toString());
	}
	
	public int size() {
		return this.set.size();
	}
}
