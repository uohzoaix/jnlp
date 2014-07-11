package com.yc.nlp.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yc.nlp.NLP;

public class TestNLP {

	public static void main(String[] args) throws Exception {
		NLP nlp = new NLP("这个东西真心很赞");
		System.out.println(nlp.words());
		System.out.println(nlp.tags());
		System.out.println(nlp.sentiments());
		System.out.println(nlp.pinyin());
		nlp = new NLP("「繁體字」「繁體中文」的叫法在臺灣亦很常見。");
		System.out.println(nlp.han());
		String text = "自然语言处理是计算机科学领域与人工智能领域中的一个重要方向。\r\n它研究能实现人与计算机之间用自然语言进行有效通信的各种理论和方法。\r\n自然语言处理是一门融语言学、计算机科学、数学于一体的科学。\r\n因此，这一领域的研究将涉及自然语言，即人们日常使用的语言，\r\n所以它与语言学的研究有着密切的联系，但又有重要的区别。\r\n自然语言处理并不是一般地研究自然语言，\r\n而在于研制能有效地实现自然语言通信的计算机系统，\r\n特别是其中的软件系统。因而它是计算机科学的一部分。";
		nlp = new NLP(text);
		System.out.println(nlp.keywords(5));
		System.out.println(nlp.summary(3));
		System.out.println(nlp.sentences());
		List<List<String>> test = new ArrayList<List<String>>();
		test.add(Arrays.asList("这篇", "文章"));
		test.add(Arrays.asList("那篇", "论文"));
		test.add(Arrays.asList("这个"));
		nlp = new NLP(test);
		System.out.println(nlp.tf());
		System.out.println(nlp.idf());
		System.out.println(nlp.sim(Arrays.asList("文章")));
	}
}
