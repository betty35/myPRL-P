package core.algorithm.lda;


public class LDA implements Runnable{
	LDAOption option = new LDAOption();
	public void setOption(String directory,String filename,String searchedWordsID)
	{
		option.dir=directory;
		option.dfile=filename;
		option.swID=searchedWordsID;
	}
	
	public void run() 
	{
		long start=System.currentTimeMillis();
		option.est = true;  /////
		///option.estc = true;
		option.inf = false;
		option.modelName = "air";
		option.niters = 1000;
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
		long end=System.currentTimeMillis();
		System.out.println(formatTime(end-start));
	}

	public static void main(String[] args) {
		//WordSimilarity.loadGlossary();
		
		LDA a=new LDA();
		a.setOption("E:/GD/lda", "air.txt", "4;5");
		a.run();
	}
	
	

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
