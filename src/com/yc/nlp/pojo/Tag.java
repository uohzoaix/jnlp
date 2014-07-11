package com.yc.nlp.pojo;

public class Tag {
	private Pre prefix;
	private Double score;
	private String suffix;

	public Tag(Pre prefix, Double score, String suffix) {
		this.prefix = prefix;
		this.score = score;
		this.suffix = suffix;
	}

	public Pre getPrefix() {
		return prefix;
	}

	public void setPrefix(Pre prefix) {
		this.prefix = prefix;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
