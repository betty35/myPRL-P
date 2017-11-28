package win.betty35.www.myPRL.Pre.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import win.betty35.www.myPRL.MultiScore.Score;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.dbUtils.common.DbHelper;
import win.betty35.www.myPRL.Pre.dbUtils.common.IConnectionProvider;
import win.betty35.www.myPRL.Pre.dbUtils.common.JdbcProvider;
import win.betty35.www.myPRL.bean.Product;

public class DB_MultiScore {
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DB_MultiScore(Configure con)
	{
		try{
			connectionProvider = (IConnectionProvider) new JdbcProvider("com.mysql.jdbc.Driver",
					"jdbc:mysql://" + con.getDb_ip(), con.getDb_user_name(),
					con.getDb_user_passwd());
			dbh = new DbHelper(connectionProvider,con.getDb_name());
		}catch (Exception e) {
			e.printStackTrace();
		} 
	
	}
	
	public ArrayList<Product> getMultiDScore(ArrayList<Product> pl,ArrayList<Integer> featureIds)
	{
		String sql="select * from MultiDScore where (PID=? and FeatureID=?)";
		int topics=featureIds.size();
		//String count="select COUNT(DISTINCT featureID) topics from multidscore where PID=?";
		for(int i=0;i<pl.size();i++)
		{
			Product p=pl.get(i);
			try {
				p.multiScore=new Score[topics];
				for(int j=0;j<topics;j++)
				{
					ResultSet r=dbh.query(sql, p.getId(),featureIds.get(j));
					r.next();
					p.multiScore[j]=new Score();
					p.multiScore[j].topicID=r.getInt("FeatureID");
					p.multiScore[j].good=r.getDouble("positive");
					p.multiScore[j].bad=r.getDouble("negative");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pl;
	}
	
	
	public Score[] getMultiScoreByPID(Long PID)
	{
		String c="select count(*) from multidscore where PID=?";
		String sql="select * from MultiDScore where PID=?";
		ResultSet co;
		Score[] result=null;
		int count=0;
		try {
			co = dbh.query(c, PID);
			co.next();
			count=co.getInt(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(count!=0)
		{ 
			result=new Score[count];
			int cursor=0;
			try {
				ResultSet re=dbh.query(sql, PID);
				while(re.next())
				{
					result[cursor]=new Score();
					result[cursor].good=re.getDouble("positive");
					result[cursor].bad=re.getDouble("negative");
					result[cursor].topicID=re.getInt("FeatureID");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void updateProductScores(Long PID,int topicID,double positive,double negative)
	{
		String sql="select * from MultiDScore where (PID=? and FeatureID=?)";
		Long MDSID=null;
		try {
			ResultSet r=dbh.query(sql, PID,topicID);
			while(r.next())
			{
				MDSID=r.getLong("MDSID");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(MDSID==null)
		{	
			sql="insert into MultiDScore (PID,FeatureID,positive,negative) values (?,?,?,?)";
			try {
				dbh.insertAndReturnLongKey(sql, PID,topicID,positive,negative);
			} catch (SQLException e) {e.printStackTrace();System.out.println("MultiScore insert failed");}
		}
		else 
		{
			sql="update MultiDScore set positive=?,negative=? where MDSID=?";
			try {
				dbh.updatePrepareSQL(sql, positive,negative,MDSID);
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
	
	public String getSearchedWordsIDs(String[] search)
	{
		ArrayList<Long> li=new ArrayList<Long>();
		String sql="select * from Keyword where keyword=?";
		for(int i=0;i<search.length;i++)
		{
			try {
				ResultSet re=dbh.query(sql, search[i]);
				if(re.next()) li.add(re.getLong("KeyID")); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(li);
		
		String r="";
		for(int i=0;i<li.size();i++)
		{
			r=r.concat(li.get(i).toString());
			if(i!=(li.size()-1)) r=r.concat(";");
		}
		
		return r;
	}
	
	public void close()
	{
		dbh.close();
	}

}
