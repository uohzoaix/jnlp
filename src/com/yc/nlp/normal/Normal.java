package com.yc.nlp.normal;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.util.MemFile;

public class Normal {

	private static Logger logger = LoggerFactory.getLogger(Normal.class);

	private Set<String> stop = new HashSet<String>();
	private Map<String, String> pinyin = new HashMap<String, String>();

	public Normal() {
		logger.debug("initialize normal begin...");
		try {
			stop = initStop("stopwords.txt");
			pinyin = initPinyin("pinyin.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("initialize normal end...");
	}

	public Set<String> initStop(String stopFile) throws Exception {
		BufferedReader br = MemFile.readFile(stopFile, this);
		if (br != null) {
			return MemFile.stopFile(br);
		}
		throw new Exception("Normal读取" + stopFile + "出错");
	}

	public Map<String, String> initPinyin(String pyFile) throws Exception {
		BufferedReader br = MemFile.readFile(pyFile, this);
		if (br != null) {
			return MemFile.pyFile(br, pinyin);
		}
		throw new Exception("Normal读取" + pyFile + "出错");
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
