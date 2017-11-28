package win.betty35.www.math.cluster;

import java.util.ArrayList;

import win.betty35.www.myPRL.MultiScore.MSContainer;
import win.betty35.www.myPRL.bean.Product;

public class MultiScoreUtils 
{
	public static ArrayList<Product> sort(ArrayList<Product> p,MSContainer myproduct,Integer[] featureIDs,int sortby)
	{
		double[] d=new double[p.size()];//distance
		for(int i=0;i<p.size();i++)
		{
			d[i]=MultiScoreDistance.calculate(p.get(i).getId(), myproduct, featureIDs,MultiScoreDistance.gRate);
		}
		
		for(int i=0;i<p.size();i++)
		{
			for(int j=0;j<p.size()-1-i;j++)
			{
				if(d[j]>d[j+1])
				{
					Product temp=p.get(j);
					p.remove(j);
					p.add(j+1, temp);
				}
			}
		}
		return p;
	}
}
