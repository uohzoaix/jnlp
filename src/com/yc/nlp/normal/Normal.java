package com.yc.nlp.normal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Normal {

	private static Logger logger = LoggerFactory.getLogger(Normal.class);

	private Set<String> stop = new HashSet<String>();
	private Map<String, String> pinyin = new HashMap<String, String>();

	public Normal() {
		logger.debug("initialize normal begin...");
		initStop("stopwords.txt");
		initPinyin("pinyin.txt");
		logger.debug("initialize normal end...");
	}

	public void initStop(String stopFile) {
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(new File(this.getClass().getClassLoader().getResource(this.getClass().getPackage().getName().replace(".", "/") + "/" + stopFile).getPath()));
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				stop.add(line);
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
	}

	public void initPinyin(String pyFile) {
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(new File(this.getClass().getClassLoader().getResource(this.getClass().getPackage().getName().replace(".", "/") + "/" + pyFile).getPath()));
			br = new BufferedReader(reader);
			String line = null;
			String[] words = null;
			while ((line = br.readLine()) != null) {
				words = line.trim().split("\\s+", 2);
				pinyin.put(words[0], words[1]);
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
	}

	public List<String> filterStop(List<String> words) {
		List<String> filters = new ArrayList<String>();
		for (String word : words) {
			if (!stop.contains(word)) {
				filters.add(word);
			}
		}
		return filters;
	}

	public String zh2hans(String sent) {
		return new ZH().transfer(sent);
	}

	public List<String> getSentence(String doc) {
		Pattern lineBreak = Pattern.compile("[\r\n]");
		Pattern delimiter = Pattern.compile("[，。？！；]");
		List<String> sentences = new ArrayList<String>();
		for (String line : lineBreak.split(doc)) {
			line = line.trim();
			if ("".equals(line)) {
				continue;
			}
			for (String sent : delimiter.split(line)) {
				sent = sent.trim();
				if ("".equals(sent)) {
					continue;
				}
				sentences.add(sent);
			}
		}
		return sentences;
	}

	public String getPinyin(String word) {
		if (pinyin.containsKey(word)) {
			return pinyin.get(word);
		}
		String ret = "";
		for (Character w : word.toCharArray()) {
			if (pinyin.containsKey(w.toString())) {
				ret += pinyin.get(w.toString());
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		System.out.println(Long.parseLong("c208000a40", 16));
	}
}
