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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.yc.nlp.prop.BaseProb;

public class MemFile {
	
	private DataOutputStream out;
	private DataInputStream in;

	/**
	 * 将内存中的数据写到文件中
	 * @param fname
	 */
	@SuppressWarnings("rawtypes")
	public void tntSave(String fname) {
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
	
	/**
	 * 将文件内容导入到内存
	 * @param fname
	 */
	@SuppressWarnings("unchecked")
	public void tntLoad(String fname) {
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
	
	public void loadToFile(Object data,String fname){
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
}
