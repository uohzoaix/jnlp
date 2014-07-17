package com.yc.nlp.util;

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
import com.yc.nlp.pojo.StageValue;
import com.yc.nlp.pojo.Tag;
import com.yc.nlp.pojo.WordTag;
import com.yc.nlp.prob.AddOneProb;
import com.yc.nlp.prob.BaseProb;
import com.yc.nlp.prob.NormalProb;

public class TnT {

	private Integer num;
	private Double l1;
	private Double l2;
	private Double l3;
	private Set<String> status;
	private BaseProb wd, eos, eosd, uni, bi, tri;
	private Map<String, Set<String>> word;
	private Map<Object, Double> trans;

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

	/**
	 * 将内存中的数据写到文件中
	 * 
	 * @param fname
	 */
	public void save(String fname) {
		MemFile.loadFromMem(fname, this);
	}

	/**
	 * 将文件内容导入到内存
	 * 
	 * @param fname
	 */
	public void load(String fname) {
		try {
			byte[] result = MemFile.loadFromFile(fname, this);
			if (result != null) {
				MemFile.loadToMem(result, this);
				return;
			}
			throw new Exception("TnT读取" + fname + "文件出错！");
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * 训练样本，将每个字的词性进行存储
	 * 
	 * @param data
	 */
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
			now = Tuple.fromStr(key);
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

	/**
	 * 获取每个字最有可能的一种词性
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public List<Result> tag(List<String> data) throws Exception {
		List<Tag> tags = new ArrayList<Tag>(getNum());
		tags.add(new Tag(new Pre("BOS", "BOS"), 0.0, ""));
		Map<Pre, StageValue> stage = new HashMap<Pre, StageValue>();
		for (String ch : data) {
			stage = new HashMap<Pre, StageValue>();
			Set<String> samples = status;
			if (this.word.containsKey(ch)) {
				samples = this.word.get(ch);
			}
			for (String s : samples) {
				double wd = Math.log(this.wd.get(s + "-" + ch)) - Math.log(this.uni.get(s));
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

	public static void main(String[] args) {
		TnT tnt = new TnT(1000);
		tnt.save("seg.marshal");
		tnt.load("seg.marshal");

	}
}
