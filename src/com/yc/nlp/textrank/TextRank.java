package com.yc.nlp.textrank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yc.nlp.sim.BM25;

public class TextRank {

	private List<List<String>> docs;
	private BM25 bm25;
	private Integer D;
	private Double d;
	private List<List<Double>> weight;
	private List<Double> weightSum;
	private List<Double> vertex;
	private Integer maxIter;
	private Double minDiff;
	private Map<Integer, Double> top;

	public TextRank(List<List<String>> docs) {
		this.docs = docs;
		this.bm25 = new BM25(docs);
		this.D = docs.size();
		this.d = 0.85;
		this.weight = new ArrayList<List<Double>>();
		this.weightSum = new ArrayList<Double>();
		this.vertex = new ArrayList<Double>();
		this.maxIter = 200;
		this.minDiff = 0.001;
		this.top = new LinkedHashMap<Integer, Double>();
	}

	public void solve() {
		for (List<String> doc : this.docs) {
			List<Double> scores = this.bm25.simall(doc);
			this.weight.add(scores);
			double sum = 0;
			for (Double score : scores) {
				sum += score;
			}
			this.weightSum.add(sum);
			this.vertex.add(1.0);
		}
		int iterNum = 0;
		while (iterNum < this.maxIter) {
			iterNum++;
			List<Double> m = new ArrayList<Double>();
			double maxDiff = 0;
			for (int i = 0; i < this.D; i++) {
				m.add(1 - this.d);
				for (int j = 0; j < this.D; j++) {
					if (j == i || this.weightSum.get(j) == 0 || this.weightSum.get(j) == 0.0) {
						continue;
					}
					m.set(m.size() - 1, m.get(m.size() - 1) + this.d * this.weight.get(i).get(j) / this.weightSum.get(j) * this.vertex.get(j));
				}
				if (Math.abs(m.get(m.size() - 1) - this.vertex.get(i)) > maxDiff) {
					maxDiff = Math.abs(m.get(m.size() - 1) - this.vertex.get(i));
				}
			}
			this.vertex = m;
			if (maxDiff <= this.minDiff) {
				break;
			}
		}
		for (int i = 0; i < this.vertex.size(); i++) {
			this.top.put(i, this.vertex.get(i));
		}
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(this.top.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		this.top.clear();
		for (Iterator<Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, Double> entry = it.next();
			this.top.put(entry.getKey(), entry.getValue());
		}
	}

	public List<Integer> topIndex(Integer limit) {
		List<Integer> indexes = new ArrayList<Integer>();
		Integer num = 0;
		for (Map.Entry<Integer, Double> entry : this.top.entrySet()) {
			if (num == limit) {
				break;
			}
			indexes.add(entry.getKey());
			num++;
		}
		return indexes;
	}

	public List<List<String>> top(Integer limit) {
		List<List<String>> docs = new ArrayList<List<String>>();
		Integer num = 0;
		for (Map.Entry<Integer, Double> entry : this.top.entrySet()) {
			if (num == limit) {
				break;
			}
			docs.add(this.docs.get(entry.getKey()));
			num++;
		}
		return docs;
	}
}
