package com.yc.nlp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodTuring {

	public static List<Double> getz(List<Double> r, List<Double> nr) {
		List<Double> z = new ArrayList<Double>();
		z.add(2 * nr.get(0) / r.get(1));
		for (int i = 0; i < nr.size() - 2; i++) {
			z.add(2 * nr.get(i + 1) / (r.get(i + 2) - r.get(i)));
		}
		z.add(nr.get(nr.size() - 1) / (r.get(r.size() - 1) - r.get(r.size() - 2)));
		return z;
	}

	public static List<Double> leastSquare(List<Double> rd, List<Double> zd) {
		List<Double> result = new ArrayList<Double>();
		double sumX = 0, sumY = 0, sumXY = 0, square = 0, b = 0;
		for (Double value : rd) {
			sumX += value;
		}
		for (Double value : zd) {
			sumY += value;
		}
		double meanX = sumX / rd.size(), meanY = sumY / zd.size();
		for (int i = 0; i < rd.size(); i++) {
			square += Math.pow(rd.get(i) - meanX, 2);
			sumXY += (rd.get(i) - meanX) * (zd.get(i) - meanY);
		}
		b = sumXY / square;
		result.add(meanY - b * meanX);
		result.add(b);
		return result;
	}

	public static List<Object> main(Map<String, Double> data) {
		List<Double> r = new ArrayList<Double>(), rd = new ArrayList<Double>(), zd = new ArrayList<Double>(), nr = new ArrayList<Double>(), prob = new ArrayList<Double>(), z = new ArrayList<Double>();
		List<Double> values = new ArrayList<Double>(data.values());
		Collections.sort(values);
		for (double value : values) {
			if (r.size() == 0 || r.get(r.size() - 1) != value) {
				r.add(value);
				nr.add(1d);
			} else {
				if (nr.size() == 0) {
					nr.add(1d);
				} else {
					nr.set(nr.size() - 1, nr.get(nr.size() - 1) + 1);
				}
			}
		}
		double total = 0d;
		Map<Double, Integer> rr = new HashMap<Double, Integer>();
		for (double value : r) {
			Integer idx = r.indexOf(value);
			if (idx < nr.size())
				total += value * nr.get(idx);
			rr.put(value, idx);
			rd.add(Math.log(value));
		}
		z = getz(r, nr);
		for (double value : z) {
			zd.add(Math.log(value));
		}
		List<Double> square = leastSquare(rd, zd);
		boolean useGoogTuring = false;
		nr.add(Math.exp(square.get(0) + square.get(1) * Math.log(r.get(r.size() - 1) + 1)));
		for (int i = 0; i < r.size(); i++) {
			double goodTuring = (r.get(i) + 1) * (Math.exp(square.get(1) * (Math.log(r.get(i) + 1) - Math.log(r.get(i)))));
			double turing = (i + 1 < r.size()) ? (r.get(i) + 1) * nr.get(i + 1) / nr.get(i) : goodTuring;
			double diff = Math.pow(Math.pow(r.get(i) + 1, 2) / nr.get(i) * nr.get(i + 1) / nr.get(i) * (1 + nr.get(i + 1) / nr.get(i)), 0.5) * 1.65;
			if (!useGoogTuring && Math.abs(goodTuring - turing) > diff) {
				prob.add(turing);
			} else {
				useGoogTuring = true;
				prob.add(goodTuring);
			}
		}
		double sump = 0d;
		for (double value : nr) {
			Integer idx = nr.indexOf(value);
			if (idx < prob.size())
				sump += value * prob.get(idx);
		}
		for (int i = 0; i < prob.size(); i++) {
			prob.set(i, (1 - nr.get(0) / total) * prob.get(i) / sump);
		}
		// nr.get(0)/total/total;
		List<Object> mixResult = new ArrayList<Object>();
		Map<String, Double> result = new HashMap<String, Double>();
		for (Map.Entry<String, Double> entry : data.entrySet()) {
			result.put(entry.getKey(), prob.get(rr.get(entry.getValue())));
		}
		mixResult.add(nr.get(0) / total / total);
		mixResult.add(result);
		return mixResult;
	}

	public static void main(String[] args) {
		Map<String, Double> data = new HashMap<String, Double>();
		data.put("1", 1d);
		data.put("2", 1d);
		data.put("3", 1d);
		data.put("4", 2d);
		data.put("5", 2d);
		data.put("6", 3d);
		data.put("7", 1d);
		data.put("8", 2d);
		data.put("9", 3d);
		System.out.println(main(data).get(0));
		System.out.println(main(data).get(1));
	}
}
