package com.yc.nlp.seg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yc.nlp.pojo.Result;
import com.yc.nlp.pojo.WordTag;
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
				for (String str : line.split("\\s+")) {
					if (!str.trim().equals(""))
						wts.add(new WordTag(str.split("/")[0], str.split("/")[1]));
				}
			}
			File extendFiles = new File("extend");
			if (extendFiles.isDirectory() && extendFiles.listFiles().length > 0) {
				for (File file : extendFiles.listFiles()) {
					reader = new FileReader(file);
					br = new BufferedReader(reader);
					line = null;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						List<WordTag> wts = new ArrayList<WordTag>();
						wordTags.add(wts);
						for (String str : line.split("\\s")) {
							wts.add(new WordTag(str.split("/")[0], str.split("/")[1]));
						}
					}
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
		this.segger.train(wordTags);
	}

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
		seg.train("data.txt");//主要是用来放置一些简单快速的中文分词和词性标注的程序
		//seg.save("seg1.marshal");
		System.out.println(seg.seg("这个东西真心很赞"));
		//System.out.println(seg.getClass().getPackage().getName());
	}
}
