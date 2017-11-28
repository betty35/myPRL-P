package win.betty35.www.myPRL.Pre;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

import win.betty35.www.myPRL.MultiScore.Topic;
import win.betty35.www.myPRL.Pre.dbUtils.DB_LDA;
import win.betty35.www.myPRL.Pre.dbUtils.DB_Raw;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.bean.LDA_Term;

public class W2V_LDA 
{
	public static void main(String[] args)
	{
		String[] a={"空气净化器","家用"};//{"多功能","料理机"};
		correct(a);
	}
	
	public static void correct(String[] searchedWords)
	{
		ArrayList<Topic> topics=new ArrayList<Topic>();
		int len=0;
		
		Configure c=new Configure();
		String[] sw=searchedWords;
		
		//init topics of the searched words
        DB_Raw d=new DB_Raw(c);
        String IDs=d.getSearchedWordsIDs(searchedWords);
        d.close();
        DB_LDA d2=new DB_LDA(c);
        ArrayList<Integer> topicIDs=d2.findTopicIDBySearch(IDs);
        len=topicIDs.size();
        for(int i=0;i<topicIDs.size();i++)
        {
        	int topicID=topicIDs.get(i);
        	ArrayList<LDA_Term> terms=d2.getTermsByTopicID(topicID);
        	Topic t=new Topic();
        	t.setID(topicID);
        	for(int j=0;j<terms.size();j++)
        	{
        		LDA_Term tm=terms.get(j);
        		if(tm.getTerm()!=null&&tm.getTerm()!="")
        		t.setTerm(tm.getTerm(), tm.getProb());
        	}
        	topics.add(t);
        }
        d2.close();
		
        
        //correction begins
        
        for(int i=0;i<topics.size();i++)
        {
        	System.out.println("");
        	System.out.println("");
        	System.out.println("");
        	
        	Topic t=topics.get(i);
        	int tid=t.topicID;
        	HashMap<String,Double> terms=t.terms;
        	
        	int times=terms.size()/5;
        	for(int n=0;n<times;n++)
        	{
        		Iterator iter=terms.entrySet().iterator();
	        	String[] tt=new String[terms.size()];
	        	Double[] pp=new Double[terms.size()];
	        	ArrayList<String> terms2=new ArrayList<String>();
	        	int k=0;
	        	while (iter.hasNext()) 
	    		{
	    			Entry entry = (Map.Entry) iter.next();
	    			String tName = (String)entry.getKey();
	    			Double tP = (Double)entry.getValue();
	    			tt[k]=tName;
	    			pp[k]=tP;
	    			terms2.add(tName);
	    			k++;
	    		}
	        	String out=outOne(tt,pp);
	        	terms2.remove(out);
	        	String in=findReplace(terms2,out);
	        	double pTemp=terms.get(out);
	        	System.out.println("本轮："+getAllTerms(tt));
	        	System.out.println("出队："+out+"; 入队："+in);
	        	terms.remove(out);
	        	terms.put(in, pTemp);
	        	DB_LDA db=new DB_LDA(c);
	        	db.replaceTermInTopic(tid, out, in);
        	}
        }
        
	}
	
	public static String outOne(String[] words,Double[] p)
	{
		if(!Word2VEC.loaded())//init Word2Vec
			try {Word2VEC.loadGoogleModel("E:/GD/words.bin");}
	        catch (IOException e) {e.printStackTrace();}
		double[] dis=Word2VEC.distanceAvg(words);
		int index=0;
		double max=0;
		for(int i=0;i<dis.length;i++)
		{
			dis[i]=dis[i]/p[i];
			if(dis[i]>max) {max=dis[i];index=i;}
		}
		return words[index];
	}
	
	public static String findReplace(ArrayList<String> terms,String out)
	{
		Set<WordEntry> t=Word2VEC.distance(terms);
		Iterator<WordEntry> iter=t.iterator();
		while (iter.hasNext()) {  
			  WordEntry w = iter.next();  
			  String n=w.name;
			  if((n.length()>=2)&&(!n.equals(out))&&(!terms.contains(n)))
				  return n;
			}  
		return null;
	}
	
	public static String getAllTerms(String[] t)
	{
		String re="";
		for(int i=0;i<t.length;i++)
			re=re.concat(t[i]).concat(",");
		return re;
	}
}
