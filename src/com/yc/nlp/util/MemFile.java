package com.yc.nlp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yc.nlp.pojo.WordTag;
import com.yc.nlp.prob.AddOneProb;
import com.yc.nlp.prob.BaseProb;

public class MemFile {

	private static DataOutputStream out;
	private static DataInputStream in;

	/**
	 * 将内存中的数据写到文件中
	 * 
	 * @param fname
	 */
	@SuppressWarnings("rawtypes")
	public static void loadFromMem(String fname, Object obj) {
		Map<String, Object> data = new HashMap<String, Object>();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(!field.isAccessible());
				Object value = field.get(obj);
				field.setAccessible(!field.isAccessible());
				if (value instanceof Set) {
					data.put(field.getName(), (Set) value);
				} else if (value instanceof BaseProb) {
					data.put(field.getName(), ((BaseProb) value));
				} else {
					data.put(field.getName(), value);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		saveToFile(data, fname);
	}

	/**
	 * 将文件内容导入到内存
	 * 
	 * @param fname
	 * @throws Exception
	 */
	public static byte[] loadFromFile(String fname, Object obj) throws Exception {
		byte[] result = null;
		try {
			byte[] bytes = new byte[2048 * 10000];
			byte[] temp = null;
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(obj.getClass().getClassLoader().getResource(fname)
					.getPath()))));
			int num = 0, beginPos = 0;
			while ((num = in.read(bytes)) > 0) {
				if (result != null) {
					temp = result;
					result = new byte[beginPos + num];
					System.arraycopy(temp, 0, result, 0, temp.length);
				} else {
					result = new byte[num];
				}
				System.arraycopy(bytes, 0, result, beginPos, num);
				beginPos += num;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return result;
	}

	public synchronized static void saveToFile(Object data, String fname) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bo);
			os.writeObject(data);
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fname)));
			out.write(bo.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized static void loadToMem(byte[] result, Object obj) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(result);
			ObjectInputStream oi = new ObjectInputStream(bi);
			Map<String, Object> data = (Map<String, Object>) oi.readObject();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Field[] fields = obj.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey())) {
						field.setAccessible(!field.isAccessible());
						try {
							field.set(obj, entry.getValue());
							field.setAccessible(!field.isAccessible());
							break;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized static void bayesLoadToMem(byte[] result, Object obj) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(result);
			ObjectInputStream oi = new ObjectInputStream(bi);
			Map<String, Object> data = (Map<String, Object>) oi.readObject();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Field[] fields = obj.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey())) {
						field.setAccessible(!field.isAccessible());
						if (field.getName().equals("d")) {
							Map<String, AddOneProb> value = (Map<String, AddOneProb>) entry.getValue();
							Map<String, AddOneProb> d = new HashMap<String, AddOneProb>();
							for (Map.Entry<String, AddOneProb> entry1 : value.entrySet()) {
								d.put(entry1.getKey(), entry1.getValue());
							}
							field.set(obj, d);
						} else {
							field.set(obj, entry.getValue());
						}
						field.setAccessible(!field.isAccessible());
						break;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化BufferedReader
	 * 
	 * @param fileName
	 * @param obj
	 * @return
	 */
	public static BufferedReader readFile(String fileName, Object obj) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(obj.getClass().getClassLoader()
					.getResource(obj.getClass().getPackage().getName().replace(".", "/") + "/" + fileName).getPath())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return br;
	}

	/**
	 * 读取停止词文件
	 * 
	 * @param br
	 * @return
	 */
	public static Set<String> stopFile(BufferedReader br) {
		String line = null;
		Set<String> stop = new HashSet<String>();
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				stop.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return stop;
	}

	/**
	 * 读取拼音文件
	 * 
	 * @param br
	 * @param pinyin
	 * @return
	 */
	public static Map<String, String> pyFile(BufferedReader br, Map<String, String> pinyin) {
		String line = null;
		String[] words = null;
		try {
			while ((line = br.readLine()) != null) {
				words = line.trim().split("\\s+", 2);
				pinyin.put(words[0], words[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return pinyin;
	}

	/**
	 * 读取繁体字文件
	 * 
	 * @param br
	 * @param zh2hans
	 * @return
	 */
	public static Map<String, String> hansFile(BufferedReader br, Map<String, String> zh2hans) {
		String[] words = null;
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				words = line.trim().split("\\|");
				zh2hans.put(words[0], words.length == 1 ? "" : words[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return zh2hans;
	}

	public static List<List<WordTag>> segFile(BufferedReader br, List<List<WordTag>> wordTags) {
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				List<WordTag> wts = new ArrayList<WordTag>();
				wordTags.add(wts);
				for (String str : line.split("\\s+")) {
					if (!str.trim().equals(""))
						wts.add(new WordTag(str.split("/")[0], str.split("/")[1]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return wordTags;
	}

	public static List<String> sentimentFile(BufferedReader br, List<String> list) {
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return list;
	}

	public static List<List<WordTag>> tagFile(BufferedReader br, List<List<WordTag>> wordTags) {
		String line = null;
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBR(br);
		}
		return wordTags;
	}

	private static void closeBR(BufferedReader br) {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
