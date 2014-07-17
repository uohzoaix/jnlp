package com.yc.nlp.tag;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.pojo.Result;
import com.yc.nlp.pojo.WordTag;
import com.yc.nlp.util.MemFile;
import com.yc.nlp.util.TnT;

public class Tag {

	private static Logger logger = LoggerFactory.getLogger(Tag.class);

	private TnT tnt;

	public Tag() {
		logger.debug("initialize tag begin...");
		tnt = new TnT();
		load("com/yc/nlp/tag/tag.marshal");
		logger.debug("initialize tag end...");
	}

	public void save(String fname) {
		this.tnt.save(fname);
	}

	public void load(String fname) {
		this.tnt.load(fname);
	}

	public void train(String fileName) {
		List<List<WordTag>> wordTags = new ArrayList<List<WordTag>>();
		BufferedReader br = MemFile.readFile(fileName, this);
		if (br != null) {
			wordTags = MemFile.tagFile(br, wordTags);
		}
		this.tnt.train(wordTags);
	}

	public List<Result> tagAll(List<String> words) throws Exception {
		return this.tnt.tag(words);
	}

	public List<String> tag(List<String> words) throws Exception {
		List<Result> results = tagAll(words);
		List<String> tags = new ArrayList<String>();
		for (Result result : results) {
			tags.add(result.getCh());
		}
		return tags;
	}

	public static void main(String[] args) {
		Tag tag = new Tag();
		tag.train("199801.txt");
		tag.save("tag.marshal");
	}
}
