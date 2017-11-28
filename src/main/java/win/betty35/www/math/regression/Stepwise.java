/**
 * @author betty352008@yeah.net
 *  Stepwise Regression
 *  Data-rows: x0,x1,x2,x3....,xn,y
 * @reference http://blog.csdn.net/lindayu0408/article/details/51172996
 */

package win.betty35.www.math.regression;
import java.util.ArrayList;

public class Stepwise {

	
	
	int rowNum =0;//samples
    int colNum =0;//variables
    double[][] source;
    double[] mean;
    double[] dev;//deviation
    double[][] sp;//
    double[][] r;//correlation
    public ArrayList<Integer> choose=new ArrayList<Integer>();//被选中的自变量
	int steps;
    
    public Stepwise()
	{
	    steps=0;
	}
    
    public boolean setData(double[][] d)
    {
    	if(d==null||d.length==0) return false;
    	else
    	{
    		this.source=d;
    		rowNum=d.length;
    		colNum=d[0].length;
    		mean=new double[colNum];
    	    dev=new double[colNum];//每列方差
    	    sp=new double[colNum][colNum];//离差阵
    	    r=new double[colNum][colNum];//相关系数矩阵
    		return true;
    	}
    }
    
	public double[] newStep() throws Exception 
	{	
		if(steps==0){
	    //计算均值
		for(int i=0;i<colNum;i++)
	    {
	    	double[] listdouble = getDataByColumn(source, i);
	    	double sum=0;
	    	for(int j=0;j<listdouble.length;j++)
	    		sum+=listdouble[j];
	    	mean[i]=sum/listdouble.length;
	    }
	    
	    //计算标准差
	    for(int i=0;i<colNum;i++)
	    {
	    	double[] listdouble = getDataByColumn(source, i);
	    	double sum=0;
	    	for(int j=0;j<listdouble.length;j++)
	    		sum+=Math.pow((listdouble[j]-mean[i]), 2);
	    	dev[i]=(double) Math.sqrt(sum/listdouble.length);
	    }
	    
	    //计算离差阵sp
	    for(int i=0;i<colNum;i++)
	    {
	    	for(int j=0;j<=i;j++)
	    	{
	    		sp[i][j]=0;
	    		for(int k=0;k<rowNum;k++)
	    			sp[i][j]+=(source[k][i]-mean[i])*(source[k][j]-mean[j]);
	    		sp[j][i]=sp[i][j];		
	    	}
	    }
		
	    //计算相关系数阵
	    for(int i=0;i<colNum;i++)
	    {
	    	for(int j=0;j<=i;j++)
	    	{
	    		r[i][j]=(double) (sp[i][j]/Math.sqrt(sp[i][i]*sp[j][j]));
	    		r[j][i]=r[i][j];
	    	}
	    }
		}
	   /* for(int i=0;i<colNum;i++)
	    {
	    	for(int j=0;j<colNum;j++)
	    	{
	    		System.out.print(r[i][j]+"\t");	
	    	}
	    	System.out.println();
	    }
	    System.out.println();
	    System.out.println();*/
	    
	    //确定F值标准
	    double F_in=1.8;//3.280;
	    double F_out=1.8;//3.280;
	    
	    //计算偏回归平方和
	    for(int k=0;k<colNum-1;k++)
	    {
		    double[] u = new double[colNum-1];
		    for(int i=0;i<colNum-1;i++)
		    {
		    	u[i]=r[colNum-1][i]*r[colNum-1][i]/r[i][i];
		    }
		    //判断是否剔除
		    double Q=r[colNum-1][colNum-1];//剩余平方和
		    int min_index=getMinIndex(u,choose);
    		double pF_out=u[min_index]/(Q/(rowNum-choose.size()-1));
	    	//System.out.println("pF_out:"+pF_out);
    		if(!(Math.abs(pF_out)>F_out))
	    	{
	    		if(choose.contains(min_index))
	    		choose.remove((Object)min_index);
    			r=trans(r,min_index,colNum);
			    //break;
    		}

		    for(Integer in:choose){
	    		int ii=in;
	    		u[ii]=0;
		    }
		    
		    //System.out.println("here");
		    int index=getMaxIndex(u);
		    double pF=u[index]/((r[colNum-1][colNum-1]-u[index])/((rowNum)-choose.size()-1));
		    //System.out.println("pF:"+pF+" ;F_in"+F_in);
		    //判断是否引入
		    if(pF>F_in){
		    	if(!choose.contains(index))
		    	choose.add(index);
		    	r=trans(r,index,colNum);
		    }
		    //System.out.println();
	    }
	    //System.out.println();
	    //建立最优回归方程
	    double[] b=new double[choose.size()];
	    int bi=0;
	    for(Integer in:choose){
    		int ii=in;
    		b[bi]=r[ii][colNum-1]*dev[colNum-1]/dev[ii];
    		bi++;
    	}
	    double b0=mean[colNum-1];
	    for(int i=0;i<b.length;i++){
	    	b0-=b[i]*mean[choose.get(i)];
	    }
	    
	    steps++;
	    String ans="step:"+steps+"; y="+b0+"+";
	    for(int i=0;i<b.length;i++){
	    	int iii=choose.get(i)+1;
	    	if(i<b.length-1)
	    		ans+=b[i]+"x"+iii+"+";
	    	else
	    		ans+=b[i]+"x"+iii;
	    }
	    
	    System.out.println(ans);
	    //复相关系数
	    double rr=(double) Math.sqrt(1-r[colNum-1][colNum-1]);
	    System.out.println("复相关系数: "+rr);
	    //回归方程估计标准误差
	    double ssy=0;
	    for(int i=0;i<rowNum;i++)
	    	ssy+=(source[i][colNum-1]-mean[colNum-1])*(source[i][colNum-1]-mean[colNum-1]);
	    double d=(double) Math.sqrt(r[colNum-1][colNum-1]*ssy/rowNum-choose.size()-1);
	    System.out.println("回归方程估计标准误差: "+d);
	    
	    double[] c=new double[b.length+1];
	    for(int i=0;i<b.length;i++)
	    	c[i]=b[i];
	    c[b.length]=b0;
	    return c;
	}
	
	 public static double[] getDataByColumn(double[][] matrix,int column)
	 {
	        int rowlength = matrix.length;
	        int colLength = matrix[0].length;
	        if(column>=colLength) return null;
	        
	        double[] col = new double[rowlength];
	        for(int i=0;i<rowlength;i++)
	            col[i]=(matrix[i][column]);
	        return col;
	 }
	 
	 public static int getMaxIndex(double[] pu)
	 {
		 int index=0;
		 double max=0;
		 for(int i=0;i<pu.length;i++)
		 {	 
			 if(Math.abs(pu[i])>=max)
			 {
				 index=i;
				 max=Math.abs(pu[i]);
			 }
		 }
		return index;
	 }
	 
	 public static int getMinIndex(double[] pu,ArrayList<Integer> pchoose){
		 int index=0;
		 double min=1;
		 for(Integer in:pchoose){
			 int ii=in;
			 if(Math.abs(pu[ii])<=min){
				 index=ii;
				 min=Math.abs(pu[ii]);
			 }
		 }
			 
		return index;
		 
	 }
	 //消去变换
	 public static double[][] trans(double[][] pr,int pindex,int pcol){
		 double[][] r2=new double[pcol][pcol];
		 for(int i=0;i<pcol;i++)
	    		for(int j=0;j<pcol;j++){
	    			if(i==pindex&&j==pindex)
	    				r2[i][j]=1/pr[i][j];
	    			else if(i==pindex&&j!=pindex)
	    				r2[i][j]=pr[i][j]/pr[i][i];
	    			else if(i!=pindex&&j==pindex)
	    				r2[i][j]=-pr[i][j]/pr[j][j];
	    			else{
	    				r2[i][j]=pr[i][j]-pr[i][pindex]*pr[pindex][j]/pr[pindex][pindex];
	    			}
	    		}
		return r2;
	 }
}
