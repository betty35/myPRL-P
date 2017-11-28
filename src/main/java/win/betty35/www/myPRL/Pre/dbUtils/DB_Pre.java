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


public class DB_Pre 
{
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DB_Pre()
	{
		Configure con=new Configure();
		try{
			connectionProvider = (IConnectionProvider) new JdbcProvider("com.mysql.jdbc.Driver",
					"jdbc:mysql://" + con.getDb_ip(), con.getDb_user_name(),
					con.getDb_user_passwd());
			dbh = new DbHelper(connectionProvider,con.getDb_name());
		}catch (Exception e) {
			e.printStackTrace();
		} 
	
	}
	
	
	
	public int getIdByTerm(String term)
	{
		String sql="select * from LDA_Dict where term=?";
		try {
			ResultSet r=dbh.query(sql, term);
			r.next();
			return r.getInt("id");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;//if the term is not in database yet
		}
	}
	
	
	
	public Long insertOpinion(Long commentID, String Opinions)
	{
		String check="select * from Opinions where CID=?";
		String sql="insert into Opinions (CID,Opinion) values (?,?)";
		try {
			ResultSet r1=dbh.query(check, commentID);
			if(!r1.next())
			{
				return dbh.insertAndReturnLongKey(sql, commentID,Opinions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close()
	{
		dbh.close();
	}

}
