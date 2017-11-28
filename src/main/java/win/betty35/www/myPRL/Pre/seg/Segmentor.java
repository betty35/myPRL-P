package win.betty35.www.myPRL.Pre.seg;

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

import win.betty35.www.myPRL.Pre.dbUtils.DB_Raw;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.bean.Comment;

public class Segmentor {
	 public static void main(String[] searchedWords )
    {
		long start=System.currentTimeMillis();
    	searchedWords= new String[]{"空气净化器","家用"};
		//searchedWords= new String[]{"多功能","料理机"};
        Configure c=new Configure();
        DB_Raw d=new DB_Raw(c);
        String IDs=d.getSearchedWordsIDs(searchedWords);
        System.out.println(IDs);
        ArrayList<Long> PIDs=d.getPIDsByKeywords(IDs);
        //get Comments
        ArrayList<Comment> commentList=new ArrayList<Comment>();
        for(int i=0;i<PIDs.size();i++)
        {
        	commentList.addAll(d.getCommentsByPID(PIDs.get(i)));
        }
        d.close();
        //System.out.println(commentList.size());
        String output="";//commentList.size()+"\n";
        int count=0;
        
        Long pid_old=commentList.get(0).getPID();
        for(int i=0;i<commentList.size();i++)
        {
        	String t=commentList.get(i).getText();
        	if(t.length()<10) continue;
        	Long pid=commentList.get(i).getPID();
        	Long cid=commentList.get(i).getCommentID();
        	if(pid!=pid_old)
        	{
        		output=output.concat("\n"); 
        		count++;
        		pid_old=pid;
        	}
        	Result r=NlpAnalysis.parse(t);
        	//System.out.println();
        	//output=output.concat(r.toStringWithOutNature()).concat(" ");
        	
        	//System.out.println(r);
        	ArrayList<String> s=SegFilter.getMainSentence(r,cid);
        	for(int j=0;j<s.size();j++)
        	{
        		//System.out.println(s.get(j));
        		output=output.concat(s.get(j)).concat(" ");
        		//if(j!=(s.size()-1)) output=output.concat(" ");
        	}
        }
        output=count+"\n"+output;
        output=output.replaceAll(" +", " ");
        File file = new File("E:/GD/lda/air.txt");
        try {
            // 创建文件
                file.createNewFile(); 
            // 声明字符输出流
                Writer out = null; 
            // 通过子类实例化，表示可以追加
                out = new FileWriter(file,false); 
            // 写入数据
                out.write(output); 
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    	
        long end=System.currentTimeMillis();
		System.out.println("共耗时："+formatTime(end-start));
		System.out.println("平均每条："+formatTime((end-start)/commentList.size()));
    	
    	/*KeyWordComputer kwc = new KeyWordComputer(5);
        String title = "空气净化器";
        String content ="店家服务很好，发货及时，物流很快，第二天就收到货了，我们这里雾霾严重，空气净化器过滤pm2.5效果相当好，开机后室内空气由重度污染很快就会达到优良标准，可互联网连接，在任何地方都能知道家里的空气质量并加以控制，非常满意。绝对正品，推荐购买。全五星。很不错，性价比很高，一直都很新人小米智能家居的产品，让普通人体会到了智能科技的快感，真的很棒，发货很快，很高兴，用起来很方便，赞一个";
       Result r= NlpAnalysis.parse(content);
      List<Term> t=r.getTerms();
       System.out.println(t.get(0).getName());*/
              
    	
    }
	 
	 
	/* public static boolean itIsIn(String str,String[] t)
	    {//compare term nature
			if(str.equals("null")) return false;
			str=str.substring(0,1);
	    	for(int i=0;i<t.length;i++)
	    	{
	    		String k=t[i];
	    		if(str.equals(k)) return true;
	    	}
	    	return false;
	    }*/
	 
	 /*
	 * 毫秒转化
	 */
	 public static String formatTime(long ms) {
	             
	              int ss = 1000;
	              int mi = ss * 60;
	              int hh = mi * 60;
	              int dd = hh * 24;

	              long day = ms / dd;
	              long hour = (ms - day * dd) / hh;
	              long minute = (ms - day * dd - hour * hh) / mi;
	              long second = (ms - day * dd - hour * hh - minute * mi) / ss;
	              long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

	              String strDay = day < 10 ? "0" + day : "" + day; //天
	              String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
	              String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
	              String strSecond = second < 10 ? "0" + second : "" + second;//秒
	              String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
	              strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
	             
	              return strMinute + " 分钟 " + strSecond + " 秒";
	    }
}
