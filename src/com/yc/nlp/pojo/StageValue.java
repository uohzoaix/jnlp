package com.yc.nlp.pojo;

import java.io.Serializable;

public class StageValue implements Serializable {
	private static final long serialVersionUID = 871588630083195569L;
	private Double score;
	private String value;

	public StageValue(Double score, String value) {
		this.score = score;
		this.value = value;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
