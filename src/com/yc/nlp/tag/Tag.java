package com.yc.nlp.tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.pojo.Result;
import com.yc.nlp.pojo.WordTag;
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
		FileReader reader = null;
		BufferedReader br = null;
		List<List<WordTag>> wordTags = new ArrayList<List<WordTag>>();
		try {
			reader = new FileReader(new File(this.getClass().getClassLoader().getResource(this.getClass().getPackage().getName().replace(".", "/") + "/" + fileName).getPath()));
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				List<WordTag> wts = new ArrayList<WordTag>();
				wordTags.add(wts);
				String[] words = line.split("\\s+");
				for (String str : words) {
					if (!str.trim().equals(""))
						wts.add(new WordTag(str.split("/")[0], str.split("/")[1]));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
