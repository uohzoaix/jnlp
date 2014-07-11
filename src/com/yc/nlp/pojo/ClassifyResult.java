package com.yc.nlp.pojo;

public class ClassifyResult {
	private String ret;
	private Double prob;

	public ClassifyResult(String ret, Double prob) {
		this.ret = ret;
		this.prob = prob;
	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public Double getProb() {
		return prob;
	}

	public void setProb(Double prob) {
		this.prob = prob;
	}

}
