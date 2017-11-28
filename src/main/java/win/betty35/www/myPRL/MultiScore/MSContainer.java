package win.betty35.www.myPRL.MultiScore;

import win.betty35.www.myPRL.Pre.dbUtils.DB_MultiScore;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;

/**
 * 
 * @author betty352008@yeah.net
 * 
 * MultiScore container of a Product
 *
 */
public class MSContainer 
{
	public Score[] scores;
	public Long PID;
	
	public MSContainer(Long PID)
	{
		Configure c=new Configure();
		DB_MultiScore l=new DB_MultiScore(c);
		scores=l.getMultiScoreByPID(PID);
		l.close();
	}
	
	public MSContainer()
	{
		
	}
}
