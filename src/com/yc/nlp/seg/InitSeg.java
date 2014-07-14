package com.yc.nlp.seg;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitSeg {

	private static Logger logger = LoggerFactory.getLogger(InitSeg.class);

	private Seg segger;

	public InitSeg() {
		logger.debug("initialize initseg begin...");
		segger = new Seg();
		segger.load("com/yc/nlp/seg/seg.marshal");
		logger.debug("initialize initseg end...");
	}

	/**
	 * 对所有的中文进行分词
	 * @param sent
	 * @return
	 */
	public List<String> seg(String sent) {
		List<String> words = new ArrayList<String>();
		Pattern pattern = Pattern.compile("([u4E00-u9FA5]+)");
		for (String s : pattern.split(sent)) {
			s = s.trim();
			if ("".equals(s)) {
				continue;
			}
			pattern = Pattern.compile("[\u4E00-\u9FA5]");
			if (pattern.matcher(s).find()) {
				words.addAll(singleSeg(s));
			} else {
				for (String word : s.split("\\s+")) {
					word = word.trim();
					if (!"".equals(word)) {
						words.add(word);
					}
				}
			}
		}
		return words;
	}

	/**
	 * 对单独的句子进行分词
	 * @param sent
	 * @return
	 */
	public List<String> singleSeg(String sent) {
		return segger.seg(sent);
	}

	public static void main(String[] args) {
	}
}
