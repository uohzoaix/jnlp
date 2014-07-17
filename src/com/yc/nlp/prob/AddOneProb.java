package com.yc.nlp.prob;

import java.io.Serializable;

public class AddOneProb extends BaseProb implements Serializable {

	private static final long serialVersionUID = 1L;

	public AddOneProb() {
		super();
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
}
