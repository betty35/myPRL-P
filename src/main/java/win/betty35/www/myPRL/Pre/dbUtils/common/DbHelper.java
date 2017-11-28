package win.betty35.www.myPRL.Pre.dbUtils.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DbHelper {
	private Connection connection;
	private PreparedStatement ps;
	private ResultSet rs;
	private String sourceName;
	private IConnectionProvider connectionProvider;
	CallableStatement cst;

	public DbHelper(IConnectionProvider connectionProvider, String sourceName) {
		this.connectionProvider = connectionProvider;
		this.sourceName = sourceName;
	}

	public Connection getConnection() throws SQLException {
		if (sourceName == null)
			throw new SQLException("Source name is null. Remember to set it.");
		int Times = 0;
		while (connection == null || connection.isClosed()) {
			try {
				closeConnection();
				connection = connectionProvider.getConnection(sourceName);
				break;
			} catch (Exception sqle) 
			{
				sqle.printStackTrace();
			} finally {
				if (Times > 5) {
					throw new SQLException("We've tried 5 times now, but still can't connect to the database.");
				}
				++Times;
			}
		}

		return connection;
	}

	public Object findObject(String sql, Object... objs) throws SQLException {
		try {
			getConnection();
			ps = connection.prepareStatement(sql);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					ps.setObject(i + 1, objs[i]);
			}
			rs = ps.executeQuery();
			if (rs.next())
				return rs.getObject(1);
			else
				return null;
		} finally {
			close();
		}
	}

	public ResultSet findById(String sql, Object id) throws Exception {
		getConnection();
		ps = connection.prepareStatement(sql,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		if (id != null) {
			ps.setObject(1, id);
		}
		return ps.executeQuery();
	}

	public ResultSet query(String sql, Object... objs) throws Exception {
		getConnection();
		ps = connection.prepareStatement(sql,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		if (objs != null) {
			for (int i = 0; i < objs.length; i++)
				ps.setObject(i + 1, objs[i]);
		}
		return ps.executeQuery();
	}

	public int insertAndReturnKey(String sql, Object... objs)
			throws SQLException {
		int countRow = 0;
		int key = 0;
		try {
			getConnection();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					ps.setObject(i + 1, objs[i]);
			}
			countRow = ps.executeUpdate();
			if (countRow > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next())
					key = rs.getInt(1);
			}
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			countRow = 0;
			connection.rollback();
			closeConnection();
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
			close();
		}
		return key;
	}
	
	
	
	public long insertAndReturnLongKey(String sql, Object... objs)
			throws SQLException {
		int countRow = 0;
		long key = 0;
		try {
			getConnection();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					ps.setObject(i + 1, objs[i]);
			}
			countRow = ps.executeUpdate();
			if (countRow > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next())
					key = rs.getLong(1);
			}
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			countRow = 0;
			connection.rollback();
			closeConnection();
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
			close();
		}
		return key;
	}


	/**
	 * @param sql 鎻掑叆mysql璇�?
	 * @param betch_data 闇�瑕佹彃鍏ョ殑鏁版嵁锛屾瘡涓�涓厓绱犳槸涓�涓緟鎻掑叆鐨勮�?
	 * @return
	 * @throws SQLException
	 */
	public int insertBatch(String sql, ArrayList<ArrayList<String>> betch_data)
			throws SQLException {
		try {
			getConnection();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql);
			
			final int data_count = betch_data.size();
			
			int num = 0;
			for(int k = 0;k < data_count;k++)
			{
				num += 1;
				
				ArrayList<String> cand_data = betch_data.get(k);
				
				for (int m = 0;m < cand_data.size();m++)
				{
					ps.setString(m+1, cand_data.get(m));
				}
				
				ps.addBatch();
				
				
				if (num % 100 == 0)
				{
					ps.executeBatch();
					num = 0;
					
				}
			}
			
			ps.executeBatch();
			
			connection.commit();
			
		} catch (SQLException e) {
			connection.rollback();
			closeConnection();
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
			close();
		}
		return betch_data.size();
	}

	public int updatePrepareSQL(String sql, Object... objs) throws SQLException {
		int countRow = 0;
		try {
			getConnection();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					ps.setObject(i + 1, objs[i]);
			}
			countRow = ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			countRow = 0;
			connection.rollback();
			closeConnection();
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
			close();
		}
		return countRow;
	}

	/**
	 * @param table_name  鏂板缓mysql琛ㄥ悕锛屽鏋滆〃�?�樺湪鍒欏垹闄ゅ師鏈夎�??
	 * @param column_name_list 鏂板缓mysql琛ㄧ殑鍚勫垪鍚�
	 * @return
	 * @throws SQLException
	 */
	public boolean createTableByColumnNameList(String table_name,
			ArrayList<String> column_name_list) throws SQLException {
		if (column_name_list.size() <= 0) {
			System.out.println("column name size is 0");
			return false;
		}

		String create_sql = "create table " + table_name + "(";
		for (int k = 0; k < column_name_list.size(); k++) {
			if (0 == k) {
				create_sql += "`" + column_name_list.get(k) + "` text";
			} else {
				create_sql += ",`" + column_name_list.get(k) + "` text";
			}
		}
		create_sql += ")";
		System.out.println("create sql:" + create_sql);

		try {
			System.out.println("create table1");
			getConnection();
			System.out.println("create table1");
			connection.setAutoCommit(false);
			System.out.println("get connection finish");
			String drop_sql = "drop table if exists " + table_name;
			ps = connection.prepareStatement(drop_sql);
			ps.executeUpdate();

			ps = connection.prepareStatement(create_sql);
			ps.executeUpdate();

			connection.commit();

			return true;
		} catch (SQLException e) {
			System.out.println(e.toString());
			closeConnection();
		}

		return false;

	}

	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
			connection = null;
		} catch (Exception e) {
		}
	}

	public void close() {
		try {
			super.finalize();
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (cst != null) {
				cst.close();
				cst = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (Throwable te) {
		}
	}
	
	public void callProcedure(String sql, Object... objs)
			throws SQLException {
		try {
			getConnection();
			connection.setAutoCommit(false);
			cst = connection.prepareCall(sql);
			if (objs != null) {
				for (int i = 0; i < objs.length; i++)
					cst.setObject(i + 1, objs[i]);
			}
			ResultSet re = cst.executeQuery();
			while(re.next()){
				//count = re.getInt(1);
				System.out.println(re.getString(1));
				System.out.println(re.getString(2));
				System.out.println(re.getString(3));
				System.out.println(re.getString(4));
				System.out.println(re.getString(5));
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			closeConnection();
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
			close();
		}
	}
	

}
