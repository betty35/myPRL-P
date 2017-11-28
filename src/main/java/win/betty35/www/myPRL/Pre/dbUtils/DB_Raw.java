package win.betty35.www.myPRL.Pre.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import win.betty35.www.myPRL.MultiScore.Score;
import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.dbUtils.common.DbHelper;
import win.betty35.www.myPRL.Pre.dbUtils.common.IConnectionProvider;
import win.betty35.www.myPRL.Pre.dbUtils.common.JdbcProvider;
import win.betty35.www.myPRL.bean.Comment;
import win.betty35.www.myPRL.bean.Product;


public class DB_Raw 
{
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DB_Raw(Configure con)
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
	
	
	public Product getProductByPID(Long PID)
	{
		String sql="select * from Product where PID=?";
		Product p=new Product();
		try {
			ResultSet r=dbh.query(sql, PID);
			r.next();
			
			p.setId(PID);
			p.setProductID(r.getLong("productID"));
			//Long proID,String vcharID,String source,String title,
			//double price,long sales,long reviews,String page,long shopID,String imgdir
			p.setFilename(r.getString("imgDir"));
			p.setSource(r.getString("Source"));
			p.setTitle(r.getString("Title"));
			p.setPrice(r.getDouble("Price"));
			p.setSales(r.getLong("Sales"));
			p.setPage(r.getString("page"));
			p.setShop(r.getLong("shopID")+"");
			p.setCommentList(this.getCommentsByPID(PID));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
	
	public Product getProductByPIDWithoutComment(Long PID)
	{
		String sql="select * from Product where PID=?";
		Product p=new Product();
		try {
			ResultSet r=dbh.query(sql, PID);
			r.next();
			p.setId(PID);
			p.setProductID(r.getLong("productID"));
			p.setFilename(r.getString("imgDir"));
			p.setSource(r.getString("Source"));
			p.setTitle(r.getString("Title"));
			p.setPrice(r.getDouble("Price"));
			p.setSales(r.getLong("Sales"));
			p.setPage(r.getString("page"));
			p.setShop(r.getLong("shopID")+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
	public String getOpinionsByCID(Long cid)
	{
		String sql="select * from Opinions where CID=?";
		try {
			ResultSet re=dbh.query(sql, cid);
			if(re.next())
				return re.getString("Opinion");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	
	public ArrayList<Long> getPIDsByKeywords(String search)
	{
		ArrayList<Long> li=new ArrayList<Long>();
		String sql="select * from Key_Product where keywordsID=?";
		try {
			ResultSet re=dbh.query(sql, search);
			while(re.next())
			{
				li.add(re.getLong("PID"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return li;		
	}
	
	
	public ArrayList<Comment> getCommentsByPID(Long PID)
	{
		ArrayList<Comment> li=new ArrayList<Comment>();
		String sql="select * from Comment where PID=?";
		try {
			ResultSet re=dbh.query(sql, PID);
			while(re.next())
			{
				Comment c=new Comment();
				c.setPID(PID);
				c.setCommentID(re.getLong("commentID"));
				c.setOriginalID(re.getLong("OriginalID"));
				c.setSource(re.getString("source"));
				c.setAdditional(re.getBoolean("additional"));
				c.setUpdatedDate(re.getTimestamp("time"));
				c.setText(re.getString("text"));
				li.add(c);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return li;
		
	}
	
	
	
	public long checkStatus(Long proID,String VcharID,String source) 
	{
		/**
		 * returns:
		 * -2: The product is not in database yet.
		 * -1: The product is in database and newly-updated.
		 * PID: The product is in database but need an price update.
		 */
		String sql=null;
		ResultSet re=null;
		try 
		{
			if(source.equals("Amazon")) 
			{
				sql="select * from Product where VcharID=? and source=?";
				
					re=dbh.query(sql,VcharID,source);
				
			}
			else 
			{
				sql="select * from Product where productid=? and source=?";
				re=dbh.query(sql,proID,source);
			}
		
			boolean has=re.next();
			if(!has) return -2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		Date d=new Date();
		Long pid=0l;
		try {
			d = re.getTimestamp("LastUpdated");
			pid=re.getLong("PID");
		} catch (SQLException e) {
			e.printStackTrace();
			return pid;
		}
		
		Date now=new Date();
		Calendar calendar = new GregorianCalendar();
	    calendar.setTime(d);
	    calendar.add(Calendar.DAY_OF_MONTH, 14);
	    d=calendar.getTime(); 
		if(d.after(now)) return -1;
		else return pid;
	}
	
	
	
	
	
	
	/*public void del0pic()
	{
		String sql="delete from productmeta where pic=flase";
		try {
			dbh.updatePrepareSQL(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updatePic(String filename)
	{
		String sql="update productmeta set pic=true where (filename=?)";
		try {
			dbh.updatePrepareSQL(sql, filename);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	public void close()
	{
		dbh.close();
	}

}
