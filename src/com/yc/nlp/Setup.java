package com.yc.nlp;

import com.yc.nlp.normal.Normal;
import com.yc.nlp.seg.InitSeg;
import com.yc.nlp.sentiment.Sentiment;
import com.yc.nlp.tag.Tag;

public class Setup {

	private static InitSeg seg;
	private static Normal normal;
	private static Sentiment sentiment;
	private static Tag tag;

	private static Setup instance;

	static {
		if (instance == null) {
			instance = new Setup();
		}
	}

	private Setup() {
		init();
	}

	private static void init() {
		seg = new InitSeg();
		normal = new Normal();
		sentiment = new Sentiment();
		tag = new Tag();
	}

	public static InitSeg getSeg() {
		return seg;
	}

	public static Normal getNormal() {
		return normal;
	}

	public static Sentiment getSentiment() {
		return sentiment;
	}

	public static Tag getTag() {
		return tag;
	}

}
