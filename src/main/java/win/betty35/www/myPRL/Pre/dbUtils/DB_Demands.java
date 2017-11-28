package win.betty35.www.myPRL.Pre.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.dbUtils.common.DbHelper;
import win.betty35.www.myPRL.Pre.dbUtils.common.IConnectionProvider;
import win.betty35.www.myPRL.Pre.dbUtils.common.JdbcProvider;
import win.betty35.www.myPRL.bean.Comment;
import win.betty35.www.myPRL.bean.LDA_Term;


public class DB_Demands 
{
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DB_Demands(Configure con)
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
	
	
	public Long newDemand(String name)
	{
		Long PDID=this.getPDID(name);
		if(PDID==-1)
		{
			String sql="insert into PrimaryDemand (DemandName) values (?)";
			try {
				return dbh.insertAndReturnLongKey(sql, name);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return PDID;
	}
	
	public long getPDID(String name)
	{
		String sql="select * from PrimaryDemand where DemandName=?";
		try {
			ResultSet r=dbh.query(sql, name);
			if(r.next()) return r.getLong("DemandID");
			else 
			{
				return -1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void updateSearchDemand(Long SWID,Long PDID)
	{
		Boolean exist=false;
		String check="select * from Search_Demand where (SWID=? and PDID=?)";
		try {
			ResultSet r=dbh.query(check, SWID,PDID);
			if(r.next()) exist=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!exist)
		{
			String sql="insert into Search_Demand (SWID,PDID) values (?,?)";
			try {
				dbh.updatePrepareSQL(sql, SWID,PDID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getDemandNameByID(Long ID)
	{
		String sql="select * from PrimaryDemand where DemandID=?";
		try {
			ResultSet r=dbh.query(sql, ID);
			if(r.next()) return r.getString("DemandName");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ArrayList<String> getDemandsBySearch(Long SWID)
	{
		ArrayList<String> result=new ArrayList<String>();
		String sql="select PrimaryDemand.DemandName from Search_Demand,PrimaryDemand where (SWID=? AND Search_Demand.PDID=PrimaryDemand.DemandID)";
		try {
			ResultSet r1=dbh.query(sql, SWID);
			while(r1.next())
			result.add(r1.getString(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public HashMap<String,Double> getDemandsByTopic(int topicID)
	{
		HashMap<String,Double> re=new HashMap<String,Double>();
		String sql="select * from DemandDict where topicID=?";
		try {
			ResultSet r=dbh.query(sql, topicID);
			while(r.next())
			{
				long demandID=r.getLong("DemandID");
				String d=this.getDemandNameByID(demandID);
				double avg=r.getDouble("AvgWeight");
				re.put(d, avg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re;
	}
	
	public void close()
	{
		dbh.close();
	}
	
	public void updateTopicDemand(Long demandID,int topicID,double weight)
	{
		String check="select * from DemandDict where (DemandID=? and TopicID=?)";
		Long DDictID=null;
		int times=0;
		double avgWeight=0;
		try {
			ResultSet r1=dbh.query(check, demandID,topicID);
			if(r1.next()) 
			{
				DDictID=r1.getLong("DDictID");
				times=r1.getInt("Times");
				avgWeight=r1.getDouble("AvgWeight");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(DDictID==null)
		{
			String sql="insert into DemandDict (DemandID,TopicID,AvgWeight,times) values (?,?,?,1)";
			try {
				dbh.updatePrepareSQL(sql, demandID,topicID,weight);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			String sql="update DemandDict set AvgWeight=?,Times=? where DDictID=?";
			avgWeight=(avgWeight*times+weight)/(times+1);
			try {
				dbh.updatePrepareSQL(sql, avgWeight,times+1,DDictID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public double[][] getAvgWeights(int[] topicIDs,Long[] demandIDs)
	{
		int tl=topicIDs.length;
		int dl=demandIDs.length;
		double[][] avg=new double[tl][dl];
		for(int i=0;i<tl;i++)
		{
			for(int j=0;j<dl;j++)
			{
				avg[i][j]=this.getAvgWeight(topicIDs[i], demandIDs[j]);
			}
		}
		return avg;
	}
	
	public double getAvgWeight(int topicID,long demandID)
	{
		String sql="select * from DemandDict where (TopicID=? AND DemandID=?)";
		try {
			ResultSet r=dbh.query(sql, topicID,demandID);
			if(r.next()) {System.out.println(r.getDouble("AvgWeight"));return r.getDouble("AvgWeight");}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
