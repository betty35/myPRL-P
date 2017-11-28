package win.betty35.www.myPRL.Pre.seg;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;

import win.betty35.www.myPRL.Pre.dbUtils.DB_Pre;

public class SegFilter 
{
	public SegFilter(){init();}
	public static ArrayList<String[]> template=new ArrayList<String[]>();
	
	public static void init()
	{
		String[] a1={"n","a"};
		String[] a2={"n","d","a"};
		String[] a3={"a","n"};
		String[] a4={"a","u","n"};
		String[] a5={"v","a"};
		String[] a6={"v","d","n"};
		String[] a7={"n","d","v"};
		String[] a8={"v","d","v"};
		String[] a9={"v","n","a"};
		String[] a10={"d","a","a"};
		String[] a11={"d","a"};
		String[] a12={"v","n","d","a"};
		String[] a13={"v","d","a"};
		template.add(a12);
		template.add(a13);
		template.add(a1);
		template.add(a2);
		template.add(a3);
		template.add(a4);
		template.add(a5);
		template.add(a6);
		template.add(a7);
		template.add(a8);
		template.add(a9);
		template.add(a10);
		template.add(a11);
	}
	
	public static ArrayList<String> getMainSentence(Result r,Long commentID)
	{
		init();
		ArrayList<String> k=new ArrayList<String>();
		String opinions="";
		List<Term> all=r.getTerms();
		List<List<Term>> segs=sentenceSeg(all);
		
		for(int m=0;m<segs.size();m++)
		{
			List<Term> ts=segs.get(m);
			int index=0;
			do{
				index=findIni(ts,index);
				if(index<0||ts.size()-index<2) break;
				int len=match(ts,index);
				if(len>0)//matches
				{	
					String re="";
					String[] pickout={"n","v"};
					for(int i=0;i<len;i++)
					{
						Term t=ts.get(index);
						//if(!itIsIn(t.getNatureStr(),pickout))
							//{index++; continue;}//pick out only verbs and nouns
						String term=t.getName();//cleanSHIandYOU(t.getName());
						if(!term.equals("")&&term!=null)
							opinions=opinions.concat(term);
						if(itIsIn(t.getNatureStr(),pickout))
						{	
							if(!term.equals("")&&term!=null)
								re=re.concat(term).concat(" ");
						}
						index++;
					}
					re=cleanMultiBlanks(re," ");
					k.add(re);
					opinions=opinions.concat(";");
				}
				else index++;
			}
			while(index<ts.size());
		}
		
		System.out.println(opinions);
		DB_Pre db=new DB_Pre();
		db.insertOpinion(commentID, opinions);
		db.close();
		return k;
	}
	
	
	public static List<List<Term>> filterMainSentence(Result r)
	{
		init();
		List<List<Term>> k=new ArrayList<List<Term>>();
		List<Term> all=r.getTerms();
		List<List<Term>> segs=sentenceSeg(all);//Set of sentences
		
		for(int m=0;m<segs.size();m++)
		{
			List<Term> ts=segs.get(m);	
			int index=0;
			do{
				index=findIni(ts,index);	
				if(index<0||(ts.size()-index)<2) break;
				int len=match(ts,index);
				if(len>0)//matches
				{	
					List<Term> re=new ArrayList<Term>();
					for(int i=0;i<len;i++)
					{
						Term t=ts.get(index);
						String term=cleanSHIandYOU(t.getName());
						if(term.equals("")||term==null){index++;continue;}
						re.add(t);
					}
					if(!re.isEmpty())
					k.add(re);
				}
				else index++;
			}
			while(index<ts.size());
		}
		return k;
	}
	
	public static String cleanMultiBlanks(String str,String replace)
	{
		return str.replaceAll("\\s\\s+",replace);
	}
	
	public static int match(List<Term> l, int index)
	{
		boolean flag=false;
		int s=template.size();
		for(int i=0;i<s;i++)
		{   
			Term p=l.get(index);
			String[] a=template.get(i);
			int z=a.length;//template word size
			
			if(z>(l.size()-index)) continue;//if number of the rest terms is less than needed
			
			for(int j=0;j<z;j++)
			{
				p=l.get(index+j);
				if(p.getNatureStr().equals("null")) return 0;
				String na=p.getNatureStr().substring(0,1);
				String c=a[j];
				if(!na.equals(c)) break;
				if(j==(z-1)) {flag=true;break;}
				
			}
			if(flag) {return z;}//matches n words
		}
		return 0;
	}
	
	public static int findIni(List<Term> l,int index)
	{
		if(l.size()==0) return -1;
		Term t=l.get(index);
		String[] n= {"n","a","v","d"};
		do{
			if(itIsIn(t.getNatureStr(),n))
			{return index;}
			else 
			{
				index++;
				if(index>=l.size()) return -1;
				t=l.get(index);
			}
		}while(true);
	}
	
	public static boolean itIsIn(String str,String[] t)
    {//compare term nature
		if(str.equals("null")) return false;
		str=str.substring(0,1);
    	for(int i=0;i<t.length;i++)
    	{
    		String k=t[i];
    		if(str.equals(k)) return true;
    	}
    	return false;
    }
	
	
	public static List<List<Term>> sentenceSeg(List<Term> t)
	{
		ArrayList<List<Term>> l=new ArrayList<List<Term>>(); 
		List<Term> piece=new ArrayList<Term>();
		for(int index=0;index<t.size();index++)
		{
			Term a=t.get(index);
			if(a.getNatureStr().substring(0,1).equals("w"))
			{
				if(piece.size()>0) l.add(piece);
				if(index<(t.size()-1))piece=new ArrayList<Term>();
			}
			else
			{
				piece.add(a);
			}
		}
		return l;
	}
	
	
	public static String getBegin(String str)
	{
		return str.substring(0,1);
	}
	
	public static String getTail(String str)
	{
		return str.substring(str.length()-1);
	}
	
	public static String cleanSHIandYOU(String str)
	{
		return str.replaceAll("是|有", "");
	}
}

