package com.yc.nlp.util;

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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yc.nlp.pojo.Pre;
import com.yc.nlp.pojo.Result;
import com.yc.nlp.pojo.Tag;
import com.yc.nlp.pojo.WordTag;
import com.yc.nlp.prop.AddOneProb;
import com.yc.nlp.prop.BaseProb;
import com.yc.nlp.prop.NormalProb;

public class TnT {

	private Integer num;
	private Double l1;
	private Double l2;
	private Double l3;
	private Set<String> status;
	private BaseProb wd, eos, eosd, uni, bi, tri;
	private Map<String, Set<String>> word;
	private Map<Object, Double> trans;
	private DataOutputStream out;
	private DataInputStream in;

	public TnT() {
		this(1000);
	}

	public TnT(Integer num) {
		this.num = num;
		this.l1 = 0.0;
		this.l2 = 0.0;
		this.l3 = 0.0;
		this.status = new HashSet<String>();
		this.wd = new AddOneProb();
		this.eos = new AddOneProb();
		this.eosd = new AddOneProb();
		this.uni = new NormalProb();
		this.bi = new NormalProb();
		this.tri = new NormalProb();
		this.word = new HashMap<String, Set<String>>();
		this.trans = new HashMap<Object, Double>();
	}

	public Integer getNum() {
		return num;
	}

	public Double getL1() {
		return l1;
	}

	public Double getL2() {
		return l2;
	}

	public Double getL3() {
		return l3;
	}

	public Set<String> getStatus() {
		return status;
	}

	public BaseProb getWd() {
		return wd;
	}

	public BaseProb getEos() {
		return eos;
	}

	public BaseProb getEosd() {
		return eosd;
	}

	public BaseProb getUni() {
		return uni;
	}

	public BaseProb getBi() {
		return bi;
	}

	public BaseProb getTri() {
		return tri;
	}

	public Map<String, Set<String>> getWord() {
		return word;
	}

	public Map<Object, Double> getTrans() {
		return trans;
	}

	@SuppressWarnings("rawtypes")
	public void save(String fname) {
		Map<String, Object> data = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				Object value = field.get(this);
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
			byte[] bytes = new byte[2048 * 10000];
			byte[] result = null;
			byte[] temp = null;
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(this.getClass().getClassLoader().getResource(fname)
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
			ByteArrayInputStream bi = new ByteArrayInputStream(result);
			ObjectInputStream oi = new ObjectInputStream(bi);
			Map<String, Object> data = (Map<String, Object>) oi.readObject();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				Field[] fields = this.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getName().equals(entry.getKey())) {
						try {
							field.set(this, entry.getValue());
							break;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
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

	public double tntDiv(double v1, double v2) {
		if (v2 == 0) {
			return v2;
		}
		return v1 / v2;
	}

	public double getEos(String tag) {
		if (!eosd.exist(tag)) {
			return Math.log(1.0 / this.status.size());
		}
		return Math.log(this.eos.get(tag + "-EOS")) - Math.log(this.eosd.get(tag));
	}

	public void train(List<List<WordTag>> data) {
		Tuple<String> now = new Tuple<String>();
		now.addAll(Arrays.asList("BOS", "BOS"));
		for (List<WordTag> wtList : data) {
			this.bi.add("BOS-BOS", 1);
			this.uni.add("BOS", 2);
			for (WordTag wt : wtList) {
				now.add(wt.getTag());
				String tupleStr = now.subList(1, now.size()).toString();
				this.status.add(wt.getTag());
				this.wd.add(wt.toString(), 1);
				this.eos.add(tupleStr, 1);
				this.eosd.add(wt.getTag(), 1);
				this.uni.add(wt.getTag(), 1);
				this.bi.add(tupleStr, 1);
				this.tri.add(now.toString(), 1);
				if (!this.word.containsKey(wt.getWord())) {
					Set<String> tags = new HashSet<String>();
					this.word.put(wt.getWord(), tags);
				}
				this.word.get(wt.getWord()).add(wt.getTag());
				now.remove(0);
			}
			this.eos.add(now.get(now.size() - 1) + "-EOS", 1);
		}
		double tl1 = 0.0, tl2 = 0.0, tl3 = 0.0;
		for (String key : this.tri.samples()) {
			now = fromStr(key);
			double c3 = this.tntDiv(this.tri.get(now.toString()) - 1, this.bi.get(now.subList(0, 2).toString()) - 1);
			double c2 = this.tntDiv(this.bi.get(now.subList(1, now.size()).toString()) - 1, this.uni.get(now.get(1)) - 1);
			double c1 = this.tntDiv(this.uni.get(now.get(2)) - 1, this.uni.getSum() - 1);
			if (c3 >= c1 && c3 >= c2) {
				tl3 += this.tri.get(now.toString());
			} else if (c2 >= c1 && c2 >= c3) {
				tl2 += this.tri.get(now.toString());
			} else if (c1 >= c2 && c1 >= c3) {
				tl1 += this.tri.get(now.toString());
			}
		}
		this.l1 = tl1 / (tl1 + tl2 + tl3);
		this.l2 = tl2 / (tl1 + tl2 + tl3);
		this.l3 = tl3 / (tl1 + tl2 + tl3);
		Set<String> newStatus = new HashSet<String>();
		newStatus.addAll(status);
		newStatus.add("BOS");
		for (String s1 : newStatus) {
			for (String s2 : newStatus) {
				for (String s3 : status) {
					if (s1.equals("BOS") && s2.equals("BOS") && s3.equals("s")) {
						System.out.println("aa");
					}
					double uni = this.l1 * this.uni.frequency(s3);
					double bi = this.tntDiv(this.l2 * this.bi.get(s2 + "-" + s3), this.uni.get(s2));
					double tri = this.tntDiv(this.l3 * this.tri.get(s1 + "-" + s2 + "-" + s3), this.bi.get(s1 + "-" + s2));
					this.trans.put(s1 + "-" + s2 + "-" + s3, Math.log(uni + bi + tri));
				}
			}
		}
	}

	public List<Result> tag(List<String> data) throws Exception {
		List<Tag> tags = new ArrayList<Tag>(getNum());
		tags.add(new Tag(new Pre("BOS", "BOS"), 0.0, ""));
		Map<Pre, StageValue> stage = new HashMap<Pre, StageValue>();
		for (String ch : data) {
			stage = new HashMap<Pre, StageValue>();
			String w = ch.toString();
			Set<String> samples = status;
			if (this.word.containsKey(w)) {
				samples = this.word.get(w);
			}
			for (String s : samples) {
				double wd = Math.log(this.wd.get(s + "-" + w)) - Math.log(this.uni.get(s));
				for (Tag tag : tags) {
					double p = tag.getScore() + wd + this.trans.get(tag.getPrefix().toString() + "-" + s);
					Pre pre = new Pre(tag.getPrefix().getTwo(), s);
					if (!stage.containsKey(pre) || p > stage.get(pre).getScore()) {
						stage.put(pre, new StageValue(p, tag.getSuffix().equals("") ? s : (tag.getSuffix() + "-" + s)));
					}
				}
			}
			tags.clear();
			for (Map.Entry<Pre, StageValue> entry : stage.entrySet()) {
				tags.add(new Tag(entry.getKey(), entry.getValue().getScore(), entry.getValue().getValue()));
			}
			Collections.sort(tags, new Comparator<Tag>() {
				public int compare(Tag o1, Tag o2) {
					if (o2.getScore() == o1.getScore())
						return 0;
					return (o2.getScore() - o1.getScore() > 0 ? 1 : -1);
				}
			});
			while (tags.size() > getNum()) {
				tags = tags.subList(0, getNum());
			}
		}
		tags.clear();
		for (Map.Entry<Pre, StageValue> entry : stage.entrySet()) {
			double score = entry.getValue().getScore() + getEos(entry.getKey().getTwo());
			tags.add(new Tag(entry.getKey(), score, entry.getValue().getValue()));
		}
		Collections.sort(tags, new Comparator<Tag>() {
			public int compare(Tag o1, Tag o2) {
				if (o2.getScore() == o1.getScore())
					return 0;
				return (o2.getScore() - o1.getScore() > 0 ? 1 : -1);
			}
		});
		List<Result> results = new ArrayList<Result>();
		String[] tagArr = tags.get(0).getSuffix().split("-");
		if (tagArr.length != data.size()) {
			throw new Exception("出错了！");
		}
		for (int i = 0; i < data.size(); i++) {
			results.add(new Result(data.get(i), tagArr[i]));
		}
		return results;
	}

	final class StageValue implements Serializable {
		private static final long serialVersionUID = 871588630083195569L;
		private Double score;
		private String value;

		public StageValue(Double score, String value) {
			this.score = score;
			this.value = value;
		}

		public Double getScore() {
			return score;
		}

		public void setScore(Double score) {
			this.score = score;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static Tuple<String> fromStr(String ele) {
		Tuple<String> strs = new Tuple<String>();
		String[] strArr = ele.split("-");
		for (String str : strArr) {
			strs.add(str);
		}
		return strs;
	}

	public static void main(String[] args) {
		// TnT tnt = new TnT(1000);
		// tnt.save("seg.marshal");
		// tnt.load("seg.marshal");
		// Map<String, Integer> test = new ConcurrentHashMap<String, Integer>();
		// for (int i = 0; i < 100000000; i++) {
		// test.put(i + "", i);
		// System.out.println(i);
		// }
	}
}
