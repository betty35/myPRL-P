package win.betty35.www.myPRL.Pre.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import win.betty35.www.myPRL.Pre.dbUtils.common.Configure;
import win.betty35.www.myPRL.Pre.dbUtils.common.DbHelper;
import win.betty35.www.myPRL.Pre.dbUtils.common.IConnectionProvider;
import win.betty35.www.myPRL.Pre.dbUtils.common.JdbcProvider;
import win.betty35.www.myPRL.bean.Comment;
import win.betty35.www.myPRL.bean.LDA_Term;


public class DB_LDA 
{
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DB_LDA(Configure con)
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
	
	
	public int insertTerm(int id,String term)
	{
		String sql="select * from LDA_Dict where id=?";
		try {
			ResultSet r=dbh.query(sql,id);
			if(r.next()) return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		sql="insert into LDA_Dict (id,term) values (?,?)";
		try {
			return dbh.insertAndReturnKey(sql, id,term);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;//if failed
	}
	
	public int addTerm(String term)
	{
		String sql="select * from LDA_Dict where term=?";
		try {
			ResultSet r=dbh.query(sql,term);
			if(r.next()) return r.getInt("id");
			else
			{
				sql="select * from lda_dict ORDER BY id desc limit 1";
				r=dbh.query(sql);
				r.next();int id=r.getInt("id")+1;
				sql="insert into LDA_Dict (id,term) values (?,?)";
				return dbh.updatePrepareSQL(sql, id,term);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getIdByTerm(String term)
	{
		String sql="select * from LDA_Dict where term=?";
		try {
			ResultSet r=dbh.query(sql, term);
			if(r.next())
			return r.getInt("id");
			else
				return this.addTerm(term);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;//if the term is not in database yet
		}
	}
	
	public String getTermById(int id)
	{
		String sql="select * from LDA_Dict where id=?";
		try {
			ResultSet r=dbh.findById(sql, id);
			if(r.next())
			return r.getString("term");
			else 
			{
				
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean initDict(Map<String,Integer> word2id, Map<Integer,String> id2word)
	{
		String sql="select * from LDA_Dict";
		try {
			ResultSet r=dbh.query(sql);
			if(r.next()==false) return false;
			do
			{
				int id=r.getInt("id");
				String term=r.getString("term");
				word2id.put(term, id);
				id2word.put(id, term);
			}while(r.next());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public Integer addTopic(String searchedWords)
	{
		String sql="insert into LDA_Topics (searchedwords) values (?)";
		try {
			return dbh.insertAndReturnKey(sql, searchedWords);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Integer> findTopicIDBySearch(String searchedWords)
	{
		String sql="select id from LDA_Topics where searchedwords=?";
		ArrayList<Integer> ids=new ArrayList<Integer>();
		try {
			ResultSet re=dbh.query(sql, searchedWords);
			while(re.next())
			{
				ids.add(re.getInt("id"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	}
	
	public void updateTopicBySearchedWords(String searchedWords,int AmountOfTopics)
	{
		ArrayList<Integer> ids=this.findTopicIDBySearch(searchedWords);
		if(ids.size()<AmountOfTopics)
		{
			int lack=AmountOfTopics-ids.size();
			for(int i=0;i<lack;i++)
			this.addTopic(searchedWords);
		}
	}
	
	public Long addTermToTopic(int term, int topic,double prob)
	{
		String sql="insert into LDA_TopicTerm (topicID,termID,p) Values (?,?,?)";
		try {
			return dbh.insertAndReturnLongKey(sql, topic,term,prob);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void updateTopicTerm(int topicID,String term,boolean add)
	{
		int termID=this.getIdByTerm(term);
		String check="select * from LDA_TopicTerm where (topicID=? and termID=?)";
		boolean isIn=false;
		double prob=0;
		try {
			ResultSet r1=dbh.query(check, topicID,termID);
			if(r1.next()) 
			{
				isIn=true;
				prob=r1.getDouble("p");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql;
		if(isIn)
		{
			if(add)
			{
				sql="update LDA_TopicTerm set p=? where (topicID=? and termID=?)";
				try {
					dbh.updatePrepareSQL(sql, prob*1.2,topicID,termID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else 
			{
				sql="update LDA_TopicTerm set p=? where (topicID=? and termID=?)";
				try {
					dbh.updatePrepareSQL(sql, prob*0.8,topicID,termID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			if(add) 
			{
				sql="insert into LDA_TopicTerm (topicID,termID,p) Values (?,?,?)";
				try {
					dbh.insertAndReturnLongKey(sql, topicID,termID,(this.getHighestProb(topicID)+this.getLowestProb(topicID))/2.0);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else {}//do nothing
		}
	}
	
	public double getHighestProb(int topicID)
	{
		String sql="select * from lda_topicterm where topicID=? ORDER BY p desc";
		try {
			ResultSet r=dbh.query(sql, topicID);
			r.next();
			return r.getDouble("p");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public double getLowestProb(int topicID)
	{
		String sql="select * from lda_topicterm where topicID=? ORDER BY p asc";
		try {
			ResultSet r=dbh.query(sql, topicID);
			r.next();
			return r.getDouble("p");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public ArrayList<LDA_Term> getTermsByTopicID(int topicID)
	{
		ArrayList<LDA_Term> l=new ArrayList<LDA_Term>();
		String sql="select * from LDA_TopicTerm where topicID=?";
		try {
			ResultSet re=dbh.query(sql, topicID);
			while(re.next())
			{
				LDA_Term t=new LDA_Term();
				t.setTerm(this.getTermById(re.getInt("termID")));
				t.setProb(re.getDouble("p"));
				if(t.getProb()>0.003)
				l.add(t);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}
	
	public void replaceTermInTopic(int topic,String out,String in)
	{
		int outID=getIdByTerm(out);
		int inID=getIdByTerm(in);
		String update="update LDA_TopicTerm set termID=? where (topicID=? and termID=?)";
		try {
			dbh.updatePrepareSQL(update, inID,topic,outID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		dbh.close();
	}
	
	

}
