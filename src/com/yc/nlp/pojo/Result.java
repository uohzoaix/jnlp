package com.yc.nlp.pojo;

public class Result {
	private String word;
	private String ch;

	public Result(String word, String ch) {
		this.word = word;
		this.ch = ch;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	@Override
	public String toString() {
		return getWord() + "-" + getCh();
	}

}
