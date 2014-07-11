package com.yc.nlp.prop;

import java.io.Serializable;
import java.util.Map;

public class NormalProb extends BaseProb implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void add(String key, Integer value) {
		if (!exist(key)) {
			data.put(key, 0d);
		}
		data.put(key, data.get(key) + value);
		total += value;
	}

	@Override
	public Map<String, Double> getData() {
		return data;
	}

}
