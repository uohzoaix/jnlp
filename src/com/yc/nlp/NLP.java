package com.yc.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.normal.Normal;
import com.yc.nlp.pojo.Result;
import com.yc.nlp.seg.InitSeg;
import com.yc.nlp.sentiment.Sentiment;
import com.yc.nlp.sim.BM25;
import com.yc.nlp.tag.Tag;
import com.yc.nlp.textrank.KeyWordTextRank;
import com.yc.nlp.textrank.TextRank;

public class NLP {

	private static Logger logger = LoggerFactory.getLogger(NLP.class);

	private String doc;
	private BM25 bm25;
	private InitSeg seg;
	private Normal normal;
	private Sentiment sentiment;
	private Tag tag;
	private TextRank textRank;
	private KeyWordTextRank kvTextRank;

	@SuppressWarnings("unchecked")
	public NLP(Object doc) {
		if (doc instanceof String) {
			this.doc = doc.toString();
		} else {
			try {
				List<List<String>> convert = (List<List<String>>) doc;
				this.bm25 = new BM25(convert);
			} catch (Exception e) {
				logger.error("暂不支持第三种数据格式");
			}
		}
		this.seg = Setup.getSeg();
		this.normal = Setup.getNormal();
		this.sentiment = Setup.getSentiment();
		this.tag = Setup.getTag();
	}

	public List<String> words() {
		return seg.seg(doc);
	}

	public List<String> sentences() {
		return normal.getSentence(this.doc);
	}

	public String han() {
		return normal.zh2hans(this.doc);
	}

	public String pinyin() {
		List<String> words = this.words();
		String ret = "";
		for (String word : words) {
			ret += normal.getPinyin(word) + " ";
		}
		return ret;
	}

	public double sentiments() {
		return sentiment.classify(doc);
	}

	public List<Result> tags() throws Exception {
		List<String> words = new ArrayList<String>(this.words());
		List<String> tags = this.tag.tag(words);
		List<Result> result = new ArrayList<Result>();
		if (words.size() == tags.size()) {
			for (int i = 0; i < words.size(); i++) {
				result.add(new Result(words.get(i), tags.get(i)));
			}
		}
		return result;
	}

	public List<Map<String, Integer>> tf() {
		return this.bm25.getF();
	}

	public Map<String, Double> idf() {
		return this.bm25.getIdf();
	}

	public List<Double> sim(List<String> doc) {
		return this.bm25.simall(doc);
	}

	public List<String> summary(Integer... limit) {
		Integer size = 0;
		if (limit == null || limit.length == 0) {
			size = 5;
		} else {
			size = limit[0];
		}
		List<List<String>> doc = new ArrayList<List<String>>();
		List<String> sents = this.sentences();
		for (String sent : sents) {
			List<String> words = this.seg.seg(sent);
			words = this.normal.filterStop(words);
			doc.add(words);
		}
		this.textRank = new TextRank(doc);
		this.textRank.solve();
		List<String> ret = new ArrayList<String>();
		List<Integer> indexes = this.textRank.topIndex(size);
		for (Integer index : indexes) {
			ret.add(sents.get(index));
		}
		return ret;
	}

	public List<String> keywords(Integer... limit) {
		Integer size = 0;
		if (limit == null || limit.length == 0) {
			size = 5;
		} else {
			size = limit[0];
		}
		List<List<String>> doc = new ArrayList<List<String>>();
		List<String> sents = this.sentences();
		for (String sent : sents) {
			List<String> words = this.seg.seg(sent);
			words = this.normal.filterStop(words);
			doc.add(words);
		}
		this.kvTextRank = new KeyWordTextRank(doc);
		this.kvTextRank.solve();
		List<String> ret = new ArrayList<String>();
		List<String> indexes = this.kvTextRank.topIndex(size);
		for (String index : indexes) {
			ret.add(index);
		}
		return ret;
	}
}
