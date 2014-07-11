package com.yc.nlp.pojo;

public class WordTag {
	private String word;
	private String tag;

	public WordTag(String word, String tag) {
		this.word = word;
		this.tag = tag;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return getTag() + "-" + getWord();
	}
}
