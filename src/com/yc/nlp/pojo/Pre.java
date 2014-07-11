package com.yc.nlp.pojo;

public class Pre {
	private String one;
	private String two;

	public Pre(String one, String two) {
		this.one = one;
		this.two = two;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pre) {
			if (((Pre) obj).getOne().equals(this.getOne()) && ((Pre) obj).getTwo().equals(this.getTwo())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return one.hashCode() + two.hashCode();
	}

	@Override
	public String toString() {
		return getOne() + "-" + getTwo();
	}

}
