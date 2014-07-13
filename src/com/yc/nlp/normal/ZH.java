package com.yc.nlp.normal;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import com.yc.nlp.util.MemFile;

public class ZH {

	private final static Map<String, String> zh2hans = new HashMap<String, String>();

	private Integer maxl = 0;

	public ZH() {
		try {
			initHans("zh2hans.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String key : zh2hans.keySet()) {
			if (key.length() > maxl) {
				maxl = key.length();
			}
		}
	}

	public Map<String, String> initHans(String hansFile) throws Exception {
		BufferedReader br = MemFile.readFile(hansFile, this);
		if (br != null) {
			return MemFile.hansFile(br, zh2hans);
		}
		throw new Exception("ZH读取" + hansFile + "出错");
	}

	public String transfer(String sentence) {
		String ret = "";
		Integer pos = 0;
		while (pos < sentence.length()) {
			boolean find = false;
			for (int i = maxl; i > 0; i--) {
				String word = sentence.substring(pos, (pos + i) > sentence.length() ? sentence.length() : (pos + i));
				if (zh2hans.containsKey(word)) {
					ret += zh2hans.get(word);
					pos += i;
					find = true;
					break;
				}
			}
			if (!find) {
				ret += sentence.substring(pos, pos + 1);
				pos += 1;
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		System.out.println(new ZH().transfer("飛機飛向藍天"));
		System.out.println(System.currentTimeMillis() - begin);
	}
}
