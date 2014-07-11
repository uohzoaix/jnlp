package com.yc.nlp.classification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yc.nlp.pojo.ClassifyResult;
import com.yc.nlp.prop.AddOneProb;

public class Bayes {

	private static Logger logger = LoggerFactory.getLogger(Bayes.class);

	private Map<String, AddOneProb> d;
	private Double total;
	private DataOutputStream out;
	private DataInputStream in;

	public Bayes() {
		logger.debug("initialize bayes begin...");
		d = new HashMap<String, AddOneProb>();
		total = 0.0;
		logger.debug("initialize bayes end...");
	}

	public void save(String fname) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", this.total);
		Map<String, AddOneProb> probdata = Collections.synchronizedMap(new HashMap<String, AddOneProb>());
		for (Map.Entry<String, AddOneProb> entry : this.d.entrySet()) {
			probdata.put(entry.getKey(), entry.getValue());
		}
		data.put("d", probdata);
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
	public void load(String fname) {
		try {
			byte[] bytes = new byte[1024];
			byte[] result = null;
			byte[] temp = null;
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(this.getClass().getClassLoader().getResource(fname).getPath()))));
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
			ByteArrayInputStream bi = new ByteArrayInputStream(result);
			ObjectInputStream oi = new ObjectInputStream(bi);
			Map<String, Object> data = (Map<String, Object>) oi.readObject();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Field[] fields = this.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey())) {
						if (field.getName().equals("d")) {
							Map<String, AddOneProb> value = (Map<String, AddOneProb>) entry.getValue();
							this.d = new HashMap<String, AddOneProb>();
							for (Map.Entry<String, AddOneProb> entry1 : value.entrySet()) {
								this.d.put(entry1.getKey(), entry1.getValue());
							}
						} else {
							try {
								field.set(this, entry.getValue());
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
						break;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@SuppressWarnings("unchecked")
	public void train(List<Object[]> data) {
		for (Object[] d : data) {
			String c = d[1].toString();
			if (!this.d.containsKey(c)) {
				this.d.put(c, new AddOneProb());
			}
			for (String word : (List<String>) d[0]) {
				this.d.get(c).add(word, 1);
			}
		}
		for (String key : this.d.keySet()) {
			this.total += this.d.get(key).getSum();
		}
	}

	public ClassifyResult classify(List<String> x) {
		Map<String, Double> tmp = new HashMap<String, Double>();
		for (String key : this.d.keySet()) {
			tmp.put(key, 0.0);
			for (String word : x) {
				tmp.put(key, tmp.get(key) + Math.log(this.d.get(key).getSum()) - Math.log(this.total) + Math.log(this.d.get(key).frequency(word)));
			}
		}
		String ret = "";
		double prob = 0;
		for (String key : this.d.keySet()) {
			double now = 0;
			for (String otherKey : this.d.keySet()) {
				now += Math.exp(tmp.get(otherKey) - tmp.get(key));
			}
			now = 1 / now;
			if (now > prob) {
				ret = key;
				prob = now;
			}
		}
		return new ClassifyResult(ret, prob);
	}
}
