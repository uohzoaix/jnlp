package com.yc.nlp.classification;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.pojo.ClassifyResult;
import com.yc.nlp.prop.AddOneProb;
import com.yc.nlp.util.MemFile;

/**
 * 贝叶斯分类器使用
 * 
 * @author uohzoaix
 * 
 */
public class Bayes {

	private static Logger logger = LoggerFactory.getLogger(Bayes.class);

	private Map<String, AddOneProb> d;
	private Double total;

	public Bayes() {
		logger.debug("initialize bayes begin...");
		d = new HashMap<String, AddOneProb>();
		total = 0.0;
		logger.debug("initialize bayes end...");
	}

	public void save(String fname) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", this.total);
		Map<String, AddOneProb> probdata = Collections.synchronizedMap(new HashMap<String, AddOneProb>());
		for (Map.Entry<String, AddOneProb> entry : this.d.entrySet()) {
			probdata.put(entry.getKey(), entry.getValue());
		}
		data.put("d", probdata);
		MemFile.saveToFile(data, fname);
	}

	public void load(String fname) {
		try {
			byte[] result = MemFile.loadFromFile(fname, this);
			if (result != null) {
				MemFile.bayesLoadToMem(result, this);
				return;
			}
			throw new Exception("Bayes读取" + fname + "文件出错！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void train(List<Object[]> data) {
		for (Object[] d : data) {
			String c = d[1].toString();
			if (!this.d.containsKey(c)) {
				this.d.put(c, new AddOneProb());
			}
			for (String word : (List<String>) d[0]) {
				this.d.get(c).add(word, 1);
			}
		}
		for (String key : this.d.keySet()) {
			this.total += this.d.get(key).getSum();
		}
	}

	public ClassifyResult classify(List<String> x) {
		Map<String, Double> tmp = new HashMap<String, Double>();
		for (String key : this.d.keySet()) {
			tmp.put(key, 0.0);
			for (String word : x) {
				tmp.put(key, tmp.get(key) + Math.log(this.d.get(key).getSum()) - Math.log(this.total) + Math.log(this.d.get(key).frequency(word)));
			}
		}
		String ret = "";
		double prob = 0;
		for (String key : this.d.keySet()) {
			double now = 0;
			for (String otherKey : this.d.keySet()) {
				now += Math.exp(tmp.get(otherKey) - tmp.get(key));
			}
			now = 1 / now;
			if (now > prob) {
				ret = key;
				prob = now;
			}
		}
		return new ClassifyResult(ret, prob);
	}
}
