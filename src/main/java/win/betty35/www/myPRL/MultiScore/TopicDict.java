package win.betty35.www.myPRL.MultiScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import win.betty35.www.myPRL.Pre.dbUtils.DB_Raw;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.bean.LDA_Term;
import win.betty35.www.myPRL.bean.Product;
import win.betty35.www.myPRL.Pre.dbUtils.DB_LDA;

public class TopicDict 
{
	public static ArrayList<Topic> topics;
	public static int len=0;
	
	public static int init(String[] searchedWords)
	{//return number of topics
		if(topics==null) topics=new ArrayList<Topic>();
		topics.clear();
		
		Configure c=new Configure();
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
        		
        		t.setTerm(tm.getTerm(), tm.getProb());
        	}
        	topics.add(t);
        }
        d2.close();
        return len;
	}
	
	public static int findTopicForTerms(ArrayList<String> term)
	{
		double max=0;
		int topicID=0;
		Map<Integer,Double> scoreOfID=new HashMap<Integer,Double>();
		for(int i=0;i<len;i++)
		{
			Topic t=topics.get(i);
			for(int j=0;j<term.size();j++)
			if(t.terms.containsKey(term.get(j)))
			{
				if(scoreOfID.containsKey(t.topicID))
				{
					double temp=scoreOfID.get(t.topicID);
					temp=temp+t.terms.get(term.get(j));
					scoreOfID.put(t.topicID, temp);
				}
				else scoreOfID.put(t.topicID, t.terms.get(term.get(j)));
			}
		}
		Iterator iter=scoreOfID.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Entry entry = (Map.Entry) iter.next();
			Integer topic = (Integer)entry.getKey();
			Double val = (Double)entry.getValue();
			if(val>max)
			{
				max=val;
				topicID=topic;
			}
		}
		return topicID;
	}
	
	public static void prepareProducts(ArrayList<Product> ps)
	{
		for(int i=0;i<ps.size();i++)
		{
			Product p=ps.get(i);
			p.multiScore=new Score[len];
			for(int j=0;j<len;j++)
			{
				p.multiScore[j]=new Score(topics.get(j).topicID);
			}
		}
	}
	
	public static void main(String[] args)
	{
		String[] a={"多功能","料理机"};
		int len=init(a);
		for(int i=0;i<len;i++)
		{
			Topic t=topics.get(i);
			System.out.println("");
			System.out.println(t.topicID);
			Iterator iter=t.terms.entrySet().iterator();
			while(iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
			    Object key = entry.getKey();
				Object val = entry.getValue();
				System.out.println("key:"+key+";val:"+val);
			}
			System.out.println("");
		}
	}
}
