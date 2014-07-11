package com.yc.nlp.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BM25 {

	private Integer d;
	private Double avgdl;
	private List<List<String>> docs;
	private List<Map<String, Integer>> f = new ArrayList<Map<String, Integer>>();
	private Map<String, Integer> df = new HashMap<String, Integer>();
	private Map<String, Double> idf = new HashMap<String, Double>();
	private Double k1;
	private Double b;

	public BM25(List<List<String>> docs) {
		this.d = docs.size();
		double sum = 0;
		for (List<String> doc : docs) {
			sum += doc.size();
		}
		this.avgdl = sum / this.d;
		this.docs = docs;
		this.k1 = 1.5;
		this.b = 0.75;
		init();
	}

	public Integer getD() {
		return d;
	}

	public void setD(Integer d) {
		this.d = d;
	}

	public Double getAvgdl() {
		return avgdl;
	}

	public void setAvgdl(Double avgdl) {
		this.avgdl = avgdl;
	}

	public List<List<String>> getDocs() {
		return docs;
	}

	public void setDocs(List<List<String>> docs) {
		this.docs = docs;
	}

	public List<Map<String, Integer>> getF() {
		return f;
	}

	public void setF(List<Map<String, Integer>> f) {
		this.f = f;
	}

	public Map<String, Integer> getDf() {
		return df;
	}

	public void setDf(Map<String, Integer> df) {
		this.df = df;
	}

	public Map<String, Double> getIdf() {
		return idf;
	}

	public void setIdf(Map<String, Double> idf) {
		this.idf = idf;
	}

	public Double getK1() {
		return k1;
	}

	public void setK1(Double k1) {
		this.k1 = k1;
	}

	public Double getB() {
		return b;
	}

	public void setB(Double b) {
		this.b = b;
	}

	private void init() {
		for (List<String> doc : docs) {
			Map<String, Integer> tmp = new HashMap<String, Integer>();
			for (String ch : doc) {
				String word = ch.toString();
				tmp.put(word, tmp.containsKey(word) ? tmp.get(word) + 1 : 1);
			}
			this.f.add(tmp);
			for (Map.Entry<String, Integer> entry : tmp.entrySet()) {
				String word = entry.getKey();
				this.df.put(word, df.containsKey(word) ? df.get(word) + 1 : 1);
			}
		}
		for (Map.Entry<String, Integer> entry : df.entrySet()) {
			this.idf.put(entry.getKey(), Math.log(this.d - entry.getValue() + 0.5) - Math.log(entry.getValue() + 0.5));
		}
	}

	public double sim(List<String> doc, Integer index) {
		double score = 0;
		for (String ch : doc) {
			String word = ch.toString();
			if (!this.f.get(index).containsKey(word)) {
				continue;
			}
			score += (this.idf.get(word) * this.f.get(index).get(word) * (this.k1 + 1) / (this.f.get(index).get(word) + this.k1 * (1 - this.b + this.b * this.d / this.avgdl)));
		}
		return score;
	}

	public List<Double> simall(List<String> doc) {
		List<Double> scores = new ArrayList<Double>();
		for (int i = 0; i < this.d; i++) {
			scores.add(this.sim(doc, i));
		}
		return scores;
	}
}
