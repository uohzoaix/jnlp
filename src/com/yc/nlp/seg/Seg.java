package com.yc.nlp.seg;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yc.nlp.pojo.Result;
import com.yc.nlp.pojo.WordTag;
import com.yc.nlp.util.MemFile;
import com.yc.nlp.util.TnT;

public class Seg {

	private TnT segger;

	public Seg() {
		segger = new TnT();
	}

	public void save(String fname) {
		this.segger.save(fname);
	}

	public void load(String fname) {
		this.segger.load(fname);
	}

	/**
	 * 训练文件
	 * @param fileName
	 */
	public void train(String fileName) {
		List<List<WordTag>> wordTags = new ArrayList<List<WordTag>>();
		BufferedReader br = MemFile.readFile(fileName, this);
		if (br != null) {
			wordTags = MemFile.segFile(br, wordTags);
		}
		// 加载自定义的训练文件
		File extendFiles = new File("extend");
		if (extendFiles.isDirectory() && extendFiles.listFiles().length > 0) {
			for (File file : extendFiles.listFiles()) {
				br = MemFile.readFile(file.getName(), this);
				if (br != null) {
					wordTags = MemFile.segFile(br, wordTags);
				}
			}
		}
		this.segger.train(wordTags);
	}

	/**
	 * 分词
	 * @param sentence
	 * @return
	 */
	public List<String> seg(String sentence) {
		List<String> ret = new ArrayList<String>();
		try {
			List<String> data = new ArrayList<String>();
			char[] chars = sentence.toCharArray();
			for (Character ch : chars) {
				data.add(ch.toString());
			}
			List<Result> results = this.segger.tag(data);
			StringBuilder sb = new StringBuilder();
			for (Result result : results) {
				if (result.getCh().equals("s")) {
					ret.add(result.getWord());
				} else if (result.getCh().equals("e")) {
					sb.append(result.getWord());
					ret.add(sb.toString());
					sb.delete(0, sb.length());
				} else {
					sb.append(result.getWord());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void main(String[] args) {
		Seg seg = new Seg();
		seg.train("data.txt");// 主要是用来放置一些简单快速的中文分词和词性标注的程序
		seg.save("seg1.marshal");
		System.out.println(seg.seg("这个东西真心很赞"));
	}
}
