package com.yc.nlp.util.prob;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseProb implements Serializable {

	private static final long serialVersionUID = 1L;
	public Map<String, Double> data;
	public Double total;
	public Double none;

	public BaseProb() {
		this.data = new HashMap<String, Double>();
		this.total = 0.0;
		this.none = 0d;
	}

	public boolean exist(String key) {
		return this.data.containsKey(key);
	}

	public double getSum() {
		return this.total;
	}

	public double get(String key) {
		return this.exist(key) ? this.data.get(key) : none;
	}

	public double frequency(String key) {
		return get(key) / total;
	}

	public Set<String> samples() {
		return data.keySet();
	}

	public abstract void add(String key, Integer value);

	public abstract Map<String, Double> getData();

}
