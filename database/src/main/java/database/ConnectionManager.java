package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionManager {

	private static String JDBC_URL = "jdbc:oracle:thin:@//localhost:1521/orclpdb";
	private static String USER = "PLAYERDB";
	private static String PASSWORD = "1234";

	private static HikariDataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(JDBC_URL);
		config.setUsername(USER);
		config.setPassword(PASSWORD);
		config.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		config.setMaximumPoolSize(20);

		dataSource = new HikariDataSource(config);
	}

	public static HikariDataSource getDataSource() {
		return dataSource;
	}
}
