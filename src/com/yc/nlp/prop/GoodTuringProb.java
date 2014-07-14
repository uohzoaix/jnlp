package com.yc.nlp.prop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yc.nlp.util.GoodTuring;

public class GoodTuringProb extends BaseProb implements Serializable {

	private static final long serialVersionUID = 1L;
	public Boolean handled;

	public GoodTuringProb() {
		super();
		handled = false;
	}

	@Override
	public void add(String key, Integer value) {
		if (!exist(key)) {
			data.put(key, 0d);
		}
		data.put(key, data.get(key) + value);
	}

	@SuppressWarnings("unchecked")
	public double get(String key) {
		if (!handled) {
			handled = true;
			List<Object> result = GoodTuring.main(data);
			none = Double.valueOf(result.get(0).toString());
			data = (Map<String, Double>) result.get(1);
			total = 0.0;
			for (double value : data.values()) {
				total += value;
			}
		}
		if (!exist(key)) {
			return none;
		}
		return data.get(key);
	}

	@Override
	public Map<String, Double> getData() {
		return data;
	}
}
