package com.yc.nlp.normal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZH {

	private final static Map<String, String> zh2hans = new HashMap<String, String>();

	private Integer maxl = 0;

	public ZH() {
		initHans("zh2hans.txt");
		for (String key : zh2hans.keySet()) {
			if (key.length() > maxl) {
				maxl = key.length();
			}
		}
	}

	public void initHans(String hansFile) {
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(new File(this.getClass().getClassLoader().getResource(this.getClass().getPackage().getName().replace(".", "/") + "/" + hansFile).getPath()));
			br = new BufferedReader(reader);
			String[] words = null;
			String line = null;
			while ((line = br.readLine()) != null) {
				words = line.trim().split("\\|");
				zh2hans.put(words[0], words.length == 1 ? "" : words[1]);
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
