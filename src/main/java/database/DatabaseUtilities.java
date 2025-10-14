package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtilities {

	public static Connection conn;

	static {
		try {
			conn = ConnectionManager.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static PreparedStatement createPreparedStatement(String statement) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ps;
	}

	public static void executePreparedStatement(PreparedStatement ps) {
		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet getResultSet(PreparedStatement ps) {
		ResultSet rs = null;

		try {
			rs = ps.getResultSet();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}
}
