package com.yc.nlp.sentiment;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.Setup;
import com.yc.nlp.classification.Bayes;
import com.yc.nlp.normal.Normal;
import com.yc.nlp.pojo.ClassifyResult;
import com.yc.nlp.seg.InitSeg;
import com.yc.nlp.util.MemFile;

public class Sentiment {

	private static Logger logger = LoggerFactory.getLogger(Sentiment.class);

	private Bayes classifier;
	private InitSeg seg;
	private Normal normal;

	public Sentiment() {
		logger.debug("initialize sentiment begin...");
		this.classifier = new Bayes();
		seg = Setup.getSeg();
		normal = Setup.getNormal();
		// seg = new InitSeg();
		// normal = new Normal();
		this.load("com/yc/nlp/sentiment/sentiment.marshal");
		logger.debug("initialize sentiment end...");
	}

	public void save(String fname) {
		this.classifier.save(fname);
	}

	public void load(String fname) {
		this.classifier.load(fname);
	}

	public List<String> handle(String doc) {
		return normal.filterStop(new ArrayList<String>(seg.seg(doc)));
	}

	public void train(String negFile, String posFile) {
		List<String> negDocs = new ArrayList<String>();
		List<String> posDocs = new ArrayList<String>();
		BufferedReader br = MemFile.readFile(negFile, this);
		if (br != null) {
			negDocs = MemFile.sentimentFile(br, negDocs);
		}
		br = MemFile.readFile(posFile, this);
		if (br != null) {
			posDocs = MemFile.sentimentFile(br, posDocs);
		}

		List<Object[]> data = new ArrayList<Object[]>();
		for (String sent : negDocs) {
			List<String> words = this.handle(sent);
			Object[] arr = new Object[2];
			arr[0] = words;
			arr[1] = "neg";
			data.add(arr);
		}
		for (String sent : posDocs) {
			List<String> words = this.handle(sent);
			Object[] arr = new Object[2];
			arr[0] = words;
			arr[1] = "pos";
			data.add(arr);
		}
		this.classifier.train(data);
	}

	public double classify(String sent) {
		ClassifyResult result = this.classifier.classify(this.handle(sent));
		if (result.getRet().equals("pos")) {
			return result.getProb();
		}
		return 1 - result.getProb();
	}

	public static void main(String[] args) {
		Sentiment sentiment = new Sentiment();
		// sentiment.train("neg.txt", "pos.txt");
		// sentiment.save("sentiment.marshal");
		sentiment.load("com/yc/nlp/sentiment/sentiment.marshal");
	}
}
