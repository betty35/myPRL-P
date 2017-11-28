package win.betty35.www.math.cluster;

import java.util.HashSet;

import win.betty35.www.myPRL.MultiScore.MSContainer;
import win.betty35.www.myPRL.MultiScore.Score;

public class MultiScoreDistance 
{
	public static final Integer gRate=1;
	public static final Integer geoMetric=2;
	public static final Integer cosineDistance=3;
	
	public static Double calculate(Long PID1,Long PID2,Integer[] featureIDs,Integer distanceType)
	{
		Double d=0.0;
		HashSet<Integer> f=getFeatureSets(featureIDs);
		MSContainer c1=new MSContainer(PID1);
		MSContainer c2=new MSContainer(PID2);
		Score[] s1=sortScoresByFeatureID(c1.scores);
		Score[] s2=sortScoresByFeatureID(c2.scores);
		int len1=s1.length;
		int len2=s2.length;
		int ca=0;int cb=0;//cursors
		
		while(ca>=len1||cb>=len2)
		{
			if(s1[ca].topicID==s2[cb].topicID&&f.contains(s1[ca].topicID))
			{
				//calculate distance
				double d2=0;
				if(distanceType==gRate)
				d2=Math.pow((gRate(s1[ca])-gRate(s2[cb])),2);
				else if(distanceType==geoMetric)
				d2=geometricD2(s1[ca],s2[cb]);
				d=d+d2;
				ca++;cb++;
				continue;
			}
			if(!f.contains(s1[ca].topicID))
			{
				ca++;continue;
			}
			if(!f.contains(s2[cb].topicID))
			{
				cb++;continue;
			}
			if(s1[ca].topicID>s2[cb].topicID)
			{
				cb++;continue;
			}
			if(s1[ca].topicID<s2[cb].topicID)
			{
				ca++;continue;
			}
		}
		
		return Math.sqrt(d);
	}


	public static Double calculate(Long PID1,MSContainer myProduct,Integer[] featureIDs,Integer distanceType)
	{
		Double d=0.0;
		HashSet<Integer> f=getFeatureSets(featureIDs);
		MSContainer c1=new MSContainer(PID1);
		MSContainer c2=myProduct;
		Score[] s1=sortScoresByFeatureID(c1.scores);
		Score[] s2=sortScoresByFeatureID(c2.scores);
		int len1=s1.length;
		int len2=s2.length;
		int ca=0;int cb=0;//cursors
		
		while(ca>=len1||cb>=len2)
		{
			if(s1[ca].topicID==s2[cb].topicID&&f.contains(s1[ca].topicID))
			{
				//calculate distance
				double d2=0;
				if(distanceType==gRate)
				d2=Math.pow((gRate(s1[ca])-gRate(s2[cb])),2);
				else if(distanceType==geoMetric)
				d2=geometricD2(s1[ca],s2[cb]);
				d=d+d2;
				ca++;cb++;
				continue;
			}
			if(!f.contains(s1[ca].topicID))
			{
				ca++;continue;
			}
			if(!f.contains(s2[cb].topicID))
			{
				cb++;continue;
			}
			if(s1[ca].topicID>s2[cb].topicID)
			{
				cb++;continue;
			}
			if(s1[ca].topicID<s2[cb].topicID)
			{
				ca++;continue;
			}
		}
		
		return Math.sqrt(d);
	}
	
	
	public static double gRate(Score s)
	{
		return s.good/(s.good+s.bad);
	}
	
	public static double geometricD2(Score a,Score b)
	{
		return Math.pow((a.good-b.good),2)+Math.pow((a.bad-b.bad), 2);
	}
	
	public static double cosineD(Score a, Score b)
	{
		return 1.0;
	}
	
	public static int max(int a1,int a2)
	{
		if(a1>a2) return a1;
		else return a2;
	}
	
	public static Score[] sortScoresByFeatureID(Score[] s)
	{
		Score temp=null;
		for(int i=0;i<s.length;i++)
		{
			for(int j=0;j<s.length-1-i;j++)
			{
				if(s[j].topicID>s[j+1].topicID)
				{
					temp=s[i];
					s[i]=s[j];
					s[j]=temp;
				}
			}
		}
		return s;
	}
	
	public static HashSet<Integer> getFeatureSets(Integer[] featureIDs)
	{
		HashSet<Integer> inte=new HashSet<Integer>();
		for(int i=0;i<featureIDs.length;i++)
		{
			inte.add(featureIDs[i]);
		}
		return inte;
	}
}
