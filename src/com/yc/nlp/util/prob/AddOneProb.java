package com.yc.nlp.util.prob;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AddOneProb extends BaseProb implements Serializable {

	private static final long serialVersionUID = 1L;

	public AddOneProb() {
		data = new HashMap<String, Double>();
		total = 0.0;
		none = 1d;
	}

	@Override
	public void add(String key, Integer value) {
		total += value;
		if (!exist(key)) {
			data.put(key, 1d);
			total += 1;
		}
		data.put(key, data.get(key) + value);
	}

	@Override
	public Map<String, Double> getData() {
		return data;
	}

}
