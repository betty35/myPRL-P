package com.ansj.vec;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.ansj.vec.domain.WordEntry;

public class Word2VEC {

	public static void main(String[] args) throws IOException {
		/*File corpus=new File("F:/BaiduYunDownload/wiki_chinese_preprocessed.simplied.txt");
		Learn lean = new Learn() ;

        lean.learnFile(corpus) ;

        lean.saveModel(new File("E:/GD/result/wiki.model")) ;*/
        
        Word2VEC w2v = new Word2VEC() ;
        loadGoogleModel("E:/GD/words.bin");
        String[] w={"声音","噪音","问题","总体","响","搅","噪声","程度"};
        //w2v.loadJavaModel("E:/GD/result/result.model") ;
        System.out.println(w2v.distance("雾霾")); ;
        System.out.println(w2v.distance("操作")); ;
        System.out.println(w2v.distance("噪音")); ;
		
        
        List<String> tryit=new ArrayList<String>();
        for(int i=0;i<w.length;i++)
        	tryit.add(w[i]);
        System.out.println("试试一堆搜索：");
        System.out.println(w2v.distance(tryit));
        
        /*ArrayList<String> pick=pickout(w,0.1);
		for(int i=0;i<pick.size();i++)
		{
			System.out.println(pick.get(i));
		}*/
        //System.out.println(wordMap.size());
		/*System.out.println(getWordVec("苹果"));
		float[] w3=getWordVec("梨");
		float len=0;
		for(int i=0;i<w3.length;i++)
		{
			len+=w3[i]*w3[i];
		}
		double len2=Math.sqrt(len);
		System.out.println("length:"+len2);*/
	}

	private static HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private static int words;
	private static int size;
	private static int topNSize = 40;

	public static boolean loaded()
	{
		
		if(wordMap.size()==0) return false;
		else return true;
	}
	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public static void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}
				wordMap.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadJavaModel(String path) throws IOException {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			words = dis.readInt();
			size = dis.readInt();

			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				key = dis.readUTF();
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					value[j] /= len;
				}
				wordMap.put(key, value);
			}

		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 近义词
	 * 
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);
		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	public static Set<WordEntry> distance(List<String> words) {

		float[] center = null;
		for (String word : words) {
			center = sum(center, wordMap.get(word));
		}

		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	private static float[] sum(float[] center, float[] fs) {
		// TODO Auto-generated method stub

		if (center == null && fs == null) {
			return null;
		}

		if (fs == null) {
			return center;
		}

		if (center == null) {
			return fs;
		}

		for (int i = 0; i < fs.length; i++) {
			center[i] += fs[i];
		}

		return center;
	}

	/**
	 * 得到词向量
	 * 
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 * 
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 * 
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes,"UTF-8"));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1,"UTF-8"));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

	public static float[] getWordVec(String word)
	{
		return wordMap.get(word);
	}
	
	public static float distanceBetween(String word1,String word2)
	{
		//System.out.println("w1:"+word1+";w2:"+word2);
		float[] w1=getWordVec(word1);
		float[] w2=getWordVec(word2);
		if(w1==null||w2==null) return 0;
		float sum=0;
		for(int i=0;i<w1.length;i++)sum+=w1[i]*w2[i];
		return sum;
	}
	
	
	public static ArrayList<String> pickout(String[] words, double shreshhold)
	{
		double[] disAvg=distanceAvg(words);
		ArrayList<String> out=new ArrayList<String>();
		for(int i=0;i<words.length;i++)
		{
			if(disAvg[i]<shreshhold)
				out.add(words[i]);
		}
		return out;
	}
	
	public static double[] distanceAvg(String[] words)
	{
		double[] disAvg=new double[words.length];
		for(int i=0;i<words.length;i++)
		{
			double sum=0;
			String w=words[i];
			for(int j=0;j<words.length;j++)
			{
				String w2=words[j];
				if(i!=j)sum=sum+distanceBetween(w,w2);
			}
			disAvg[i]=sum/(words.length-1);
		}
		return disAvg;
	}
	
	
	 public static String getEncoding(String str) {  
	        String encode = "GB2312";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s = encode;  
	                return s;  
	            }  
	        } catch (Exception exception) {  
	        }  
	        encode = "ISO-8859-1";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s1 = encode;  
	                return s1;  
	            }  
	        } catch (Exception exception1) {  
	        }  
	        encode = "UTF-8";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s2 = encode;  
	                return s2;  
	            }  
	        } catch (Exception exception2) {  
	        }  
	        encode = "GBK";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s3 = encode;  
	                return s3;  
	            }  
	        } catch (Exception exception3) {  
	        }  
	        return "";  
	    }  
	 
}
