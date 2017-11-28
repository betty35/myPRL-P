package win.betty35.www.myPRL.MultiScore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Topic 
{
	public int topicID;
	public HashMap<String,Double> terms;
	
	public Topic()
	{
		terms=new HashMap<String,Double>();
	}
	
	public void setID(int ID)
	{
		topicID=ID;
	}
	
	public void setTerm(String term,double prob)
	{
		terms.put(term, prob);
	}
	
	public String getAllKeys()
	{
		String re="";
		for (String key : terms.keySet()) 
		{  
			re=re.concat(key).concat(";");
		} 
		re=re.substring(0,re.length()-1);
		return re;
	}
}
