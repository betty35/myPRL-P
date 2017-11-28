package win.betty35.www.myPRL.Pre.dbUtils.common;
import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionProvider {
	public Connection getConnection(String sourceName) throws SQLException;
}
