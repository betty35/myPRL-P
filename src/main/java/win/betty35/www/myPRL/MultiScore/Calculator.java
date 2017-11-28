package win.betty35.www.myPRL.MultiScore;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import win.betty35.www.myPRL.Pre.dbUtils.DB_Raw;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.seg.SegFilter;
import win.betty35.www.myPRL.bean.Comment;
import win.betty35.www.myPRL.bean.Product;

/**
 * 
 * @author betty352008@yeah.net
 * 
 * ('v') Calculate scores for topics(features) of the product
 */
public class Calculator 
{
	public static void init()
	{
		SentiDict.init();
		System.out.println("positive words:"+SentiDict.negative.size());
	}
	
	public static void calculate(Product p)
	{
		ArrayList<Comment> cl=p.getCommentList();
		int[] topicCount=new int[p.multiScore.length];
		for(int i=0;i<cl.size();i++)
		{
			Comment c=cl.get(i);
			Long cid=c.getCommentID();
			DB_Raw db=new DB_Raw(new Configure());
			String opinions=db.getOpinionsByCID(cid);
			db.close();
			/*Result r=ToAnalysis.parse(c.getText());
			List<List<Term>> tll=SegFilter.filterMainSentence(r);*/
			if(opinions==null||opinions=="") continue;
			String[] op=opinions.split(";");
			List<List<Term>> tll=new ArrayList<List<Term>>();
			Result r=null;
			for(int j=0;j<op.length;j++)
			{
				r=NlpAnalysis.parse(op[j]);
				//System.out.println(r);
				tll.add(r.getTerms());
			}
			//System.out.println("filtered:"+tll.size());
			for(int j=0;j<tll.size();j++)
			{
				ArrayList<String> terms=new ArrayList<String>();
				List<Term> tl=tll.get(j);
				double score=0;
				int topicID=0;
				int intensity=1;
				for(int m=0;m<tl.size();m++)
				{
					Term t=tl.get(m);
					terms.add(t.getName());
					if(t.getNatureStr().substring(0,1).equals("a"))
					{
						if(SentiDict.isNegative(t.getName()))
						{score=-1;}
						else if(SentiDict.isPossitive(t.getName()))
						{score=1;}
					}
					if(t.getNatureStr().substring(0,1).equals("d"))
					{
						intensity=SentiDict.getIntensity(t.getName());
					}
				}
				topicID=TopicDict.findTopicForTerms(terms);
				score=score/intensity;
				if(score>0) 
				{
					for(int iter=0;iter<p.multiScore.length;iter++)
						if(p.multiScore[iter].topicID==topicID)
						{
							p.multiScore[iter].good+=score;
							topicCount[iter]++;
						}
				}
				else if(score<0) 
				{
					for(int iter=0;iter<p.multiScore.length;iter++)
						if(p.multiScore[iter].topicID==topicID)
						{
							p.multiScore[iter].bad-=score;
							topicCount[iter]++;
						}
				}
			}
		}
		for(int i=0;i<p.multiScore.length;i++)
		{
			if(topicCount[i]!=0)
			{
				p.multiScore[i].good=p.multiScore[i].good*1.00/topicCount[i];
				p.multiScore[i].bad=p.multiScore[i].bad*1.00/topicCount[i];
			}
		}
	}
	
}
