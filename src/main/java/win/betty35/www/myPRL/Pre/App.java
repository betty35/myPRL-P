package win.betty35.www.myPRL.Pre;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import win.betty35.www.math.regression.Stepwise;
import win.betty35.www.myPRL.MultiScore.Calculator;
import win.betty35.www.myPRL.MultiScore.Topic;
import win.betty35.www.myPRL.MultiScore.TopicDict;
import win.betty35.www.myPRL.Pre.dbUtils.DB_Raw;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.dbUtils.DB_MultiScore;
import win.betty35.www.myPRL.Pre.dbUtils.DB_LDA;
import win.betty35.www.myPRL.Pre.seg.SegFilter;
import win.betty35.www.myPRL.bean.Comment;
import win.betty35.www.myPRL.bean.LDA_Term;
import win.betty35.www.myPRL.bean.Product;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static void updateTopics(int topicID,Topic[] topicChange)
	{
		Configure c=new Configure();
	}
	
	
	public static void main(String[] args)
	{

		Configure c=new Configure();
		String[] a={"空气净化器","家用"};//{"多功能","料理机"};
		ArrayList<Product> ps=getProductsBySearchedWords(a,300);
		Calculator.init();
		//int features=TopicDict.init(a);
		//System.out.println(features);
		TopicDict.prepareProducts(ps);
		/*System.out.println("finished preparation");
		System.out.println("Start calculations");
		DB_MultiScore db=new DB_MultiScore(c);
		for(int i=185;i<ps.size();i++)
		{
			Product p=ps.get(i);
			Calculator.calculate(p);
			
			for(int j=0;j<features;j++)
			{
				db.updateProductScores(p.getId(),p.multiScore[j].topicID,p.multiScore[j].good, p.multiScore[j].bad);
			}
			
			System.out.println();
			System.out.println("Product "+(i+1)+" :");
			for(int j=0;j<features;j++)
				System.out.println(">>Feature "+j+": positive:"+p.multiScore[j].good+"; negative:"+p.multiScore[j].bad);
		}
		db.close();
		System.out.println("MultiScore's Calculation is finished");*/
		DB_LDA d1=new DB_LDA(c);
		DB_MultiScore d2=new DB_MultiScore(c);
		String searched=d2.getSearchedWordsIDs(a);
		ArrayList<Integer> topicIDs=d1.findTopicIDBySearch(searched);
		d1.close();
		d2.getMultiDScore(ps, topicIDs);
		d2.close();
		int features=topicIDs.size();
		int xBesidesMultiScore=0;
		double []y=new double[ps.size()];
		double [][]x=new double[ps.size()][features*2+xBesidesMultiScore+1];//*2+1];
		for(int i=0;i<ps.size();i++)
		{
			Product p=ps.get(i);
			y[i]=p.getSales();
			for(int j=0;j<features;j++)
			{
				/*double total=(p.multiScore[j].good+p.multiScore[j].bad);
				if(Math.abs(total)>0.0000001)
				x[i][j]=p.multiScore[j].good/(p.multiScore[j].good+p.multiScore[j].bad);
				else x[i][j]=0;*/
				x[i][j*2]=p.multiScore[j].good;
				x[i][j*2+1]=p.multiScore[j].bad;
				System.out.println(x[i][j]);
			}
			//x[i][features*2]=p.getPrice();
			x[i][features*2]=p.getSales();
			//x[i][features]=p.getPrice();
		}		
		
		Stepwise regression=new Stepwise();
		
		double[][] yy=new double[][]{{7,26,6,60,78.5},{1,29,15,52,74.3},
			{11,56,8,20,104.3},
			{11,31,8,47,87.6},
			{7,52,6,33,95.9},
			{11,55,9,22,109.2},
			{3,71,17,6,102.7},
			{1,31,22,44,72.5},
			{2,54,18,22,93.1},
			{21,47,4,26,115.9},
			{1,40,23,34,83.8},
			{11,66,9,12,113.3},
			{10,68,8,12,109.4}};
		
		regression.setData(x);
		try {for(int i=0;i<100;i++)
			regression.newStep();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* private static void print(double[] v) {  
	        // TODO Auto-generated method stub  
	        for(int i=0;i<v.length;i++){  
	            System.out.print(v[i]+ " ");  
	        }  
	        System.out.println();  
	    }  */
	 
	public static String getProductsJson(String[] searchedWords,int maxProductLen,int maxCommentLen)
	{
		ArrayList<Product> pl=getProductsBySearchedWords(searchedWords,maxProductLen);
		return ProductList2Json(pl,maxCommentLen);
	}
	
	
	public static ArrayList<Product> getProductsBySearchedWords(String[] searchedWords,int maxLen)
	{
        Configure c=new Configure();
        DB_Raw d=new DB_Raw(c);
        String IDs=d.getSearchedWordsIDs(searchedWords);
        //System.out.println(IDs);
        ArrayList<Long> PIDs=d.getPIDsByKeywords(IDs);
        //get Comments
        ArrayList<Product> products=new ArrayList<Product>();
        int len=PIDs.size();
        if(len>maxLen)len=maxLen;
        for(int i=0;i<len;i++)
        {
        	products.add(d.getProductByPID(PIDs.get(i)));
        }
        d.close();
        return products;
	}
	
	public static String ProductList2Json(ArrayList<Product> products,int maxComments)
	{
		JSONArray ps = new JSONArray();
        
		int len=products.size();
		for(int i=0;i<len;i++)
		{
			Product p=products.get(i);
			JSONObject product = new JSONObject();
			JSONArray cs=new JSONArray();//comments
			
			product.put("id",p.getId());
			product.put("title", p.getTitle());
			product.put("price", p.getPrice());
			product.put("sales", p.getSales());
			product.put("image",p.getFilename());
			product.put("source", p.getSource());
			product.put("page", p.getPage());
			
			ArrayList<Comment> cl=p.getCommentList();
			int cLen=cl.size();
			int startMax=cLen>maxComments?cLen-maxComments:0;
			int start=(int) Math.floor(Math.random()*startMax);
			if(cLen>maxComments) cLen=maxComments;
			for(int m=start;m<(start+cLen);m++)
			{
				JSONObject c1=new JSONObject();//comment
				Comment c=cl.get(m);
				String text=c.getText();
				if(text.length()<20) 
				{
					if((start+cLen+1)<=cl.size())//if there are more comments that can be used
					cLen++;
					continue;
				}
				Result r=ToAnalysis.parse(text);
		    	/*List<Term> ts=r.getTerms();
		    	JSONArray ts1=new JSONArray();//Terms of a comment
		    	for(int n=0;n<ts.size();n++)
		    	{
		    		Term tt=ts.get(n);
		    		JSONObject t1=new JSONObject();
		    		t1.put("t", tt.getName());
		    		t1.put("n", tt.getNatureStr());
		    		ts1.add(t1);
		    	}*/
				c1.put("t", r.toString());
		    	c1.put("id", c.getCommentID());
		    	c1.put("oid", c.getOriginalID());
		    	//c1.put("c", ts1);
		    	
				cs.add(c1);
			}
			product.put("comments", cs);
			ps.add(product);
		}
		
		//System.out.println(ps.toJSONString());
		//return ps.toJSONString(JSONStyle.MAX_COMPRESS);
		return ps.toJSONString();
		//Result r=ToAnalysis.parse(t);
    	//Term t1=r.get(0);
	}
	
    
    public static String toStringWithSpace(Result r)
    {
    	return r.toStringWithOutNature().replaceAll(","," ");
    }
    
    public static boolean reserve(String nature)
    {
    	/*String[] t={"d","p","b","c","null","r","e","y","o"};
    	if(itIsIn(nature,t))
    		return false;
    	else if(nature.charAt(0)=='w') return false;
    	else
    	return true;*/
    	 if(nature.charAt(0)=='n'||nature.charAt(0)=='a') return true;
    	else
    	return false;
    }
    
    
    public static boolean itIsIn(String str,String[] t)
    {
    	for(int i=0;i<t.length;i++)
    	{
    		String k=t[i];
    		if(str.equals(k)) return true;
    	}
    	return false;
    }
    
    
}
