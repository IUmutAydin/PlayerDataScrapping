package database.memberProcesses;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.zaxxer.hikari.HikariDataSource;

import database.ConnectionManager;

public abstract class MemberDatabaseProcesses {

	private static HikariDataSource dataSource = ConnectionManager.getDataSource();

	protected static int executeUpdate(String statement, PreparedStatementSetter setter) {
		int rowNumber = 0;

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rowNumber;
	}

	protected static void executeQuerry(String statement, PreparedStatementSetter setter, ResultSetHandler handler) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);

			try (ResultSet rs = ps.executeQuery()) {
				handler.handle(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static ResultSet executeQuerry(String statement, PreparedStatementSetter setter) {
		try {
			Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(statement);
			if (setter != null)
				setter.setParameters(ps);

			return ps.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected static <T> T fetchSingleData(String statement, PreparedStatementSetter setter,
			Function<ResultSet, T> mapper) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapper.apply(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected static <T, C extends Collection<T>> C fetchMultipleData(String statement, PreparedStatementSetter setter,
			Function<ResultSet, T> mapper, Supplier<C> supplier) {
		C collection = supplier.get();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					collection.add(mapper.apply(rs));
			}

			return collection;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected static boolean exists(String statement, PreparedStatementSetter setter, ExistenceOfMember controller) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);

			try (ResultSet rs = ps.executeQuery()) {
				return controller.exists(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	protected static boolean isDataNull(String statement, PreparedStatementSetter setter, ResultSetHandler handler) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
			if (setter != null)
				setter.setParameters(ps);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					handler.handle(rs);

					return rs.wasNull();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	protected static Connection getConnection() {
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	@FunctionalInterface
	protected static interface PreparedStatementSetter {
		void setParameters(PreparedStatement ps) throws SQLException;
	}

	@FunctionalInterface
	protected static interface ResultSetHandler {
		void handle(ResultSet rs) throws SQLException;
	}

	@FunctionalInterface
	protected static interface ExistenceOfMember {
		boolean exists(ResultSet rs) throws SQLException;
	}

	@FunctionalInterface
	protected static interface ResultSetDataMapper<T, V> {
		V apply(T t) throws SQLException;
	}

	@FunctionalInterface
	protected static interface NullityOfData {
		boolean isDataNull(ResultSet rs) throws SQLException;
	}

	protected static <T, V> Function<T, V> wrapper(ResultSetDataMapper<T, V> mapper) {
		return t -> {
			try {
				return mapper.apply(t);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		};
	}

	protected static void isNumericDataNull(PreparedStatement ps, int index, Integer data) throws SQLException {
		if (data > 0)
			ps.setInt(index, data);
		else
			ps.setNull(index, Types.NUMERIC);
	}

	protected static void isNumericDataNull(PreparedStatement ps, int index, Integer data, int bound)
			throws SQLException {
		if (data > bound)
			ps.setInt(index, data);
		else
			ps.setNull(index, Types.NUMERIC);
	}

	protected static void isNumericDataNull(PreparedStatement ps, int index, Double data) throws SQLException {
		if (data > 0)
			ps.setDouble(index, data);
		else
			ps.setNull(index, Types.NUMERIC);
	}

	protected static void isNumericDataNull(PreparedStatement ps, int index, Integer data, Predicate<Integer> condition)
			throws SQLException {
		if (condition.test(data))
			ps.setInt(index, data.intValue());
		else
			ps.setNull(index, Types.NUMERIC);
	}

	protected static void isNumericDataNull(PreparedStatement ps, int index, Double data, Predicate<Double> condition)
			throws SQLException {
		if (condition.test(data))
			ps.setDouble(index, data);
		else
			ps.setNull(index, Types.NUMERIC);
	}

	@FunctionalInterface
	public static interface DataAdder<T> {
		void addData(int columnIndex, T data) throws SQLException;
	}

	@FunctionalInterface
	public static interface DataGetter<T> {
		T getData(String columnLabel) throws SQLException;
	}

	public static class Column<T> {
		private String columnLabel;
		private DataAdder<T> dataAdder;
		private DataGetter<T> dataGetter;

		public Column(String columnLabel, DataAdder<T> dataAdder, DataGetter<T> dataGetter) {
			this.columnLabel = columnLabel;
			this.dataAdder = dataAdder;
			this.dataGetter = dataGetter;
		}

		public String getColumnLabel() {
			return columnLabel;
		}

		public void addData(int columnIndex, T data) throws SQLException {
			dataAdder.addData(columnIndex, data);
		}

		public T getData() throws SQLException {
			return dataGetter.getData(columnLabel);
		}
	}

	public static class Data<T extends Comparable<?>> {
		private T data;

		public Data(T data) {
			this.data = data;
		}

		public void addData(PreparedStatement ps, int parameterIndex) throws SQLException {
			if (data instanceof Integer)
				ps.setInt(parameterIndex, ((Integer) data).intValue());
			else if (data instanceof String)
				ps.setString(parameterIndex, ((String) data).strip());
			else if (data instanceof Double)
				ps.setDouble(parameterIndex, ((Double) data).doubleValue());
			else if (data instanceof LocalDate)
				ps.setDate(parameterIndex, Date.valueOf((LocalDate) data));
		}

		public static <V> V getData(ResultSet rs, String columnLabel, Class<V> type) throws SQLException {
			Object data;

			if (type == String.class)
				data = rs.getString(columnLabel);
			else if (type == Integer.class) {
				int intValue = rs.getInt(columnLabel);
				data = getData(rs, intValue);
			} else if (type == Double.class) {
				double doubleValue = rs.getDouble(columnLabel);
				data = getData(rs, doubleValue);
			} else if (type == LocalDate.class) {
				Date date = rs.getDate(columnLabel);
				data = getData(rs, date).toLocalDate();
			} else
				data = rs.getObject(columnLabel);

			return type.cast(data);
		}

		private static <V> V getData(ResultSet rs, V data) throws SQLException {
			if (!rs.wasNull())
				return data;
			else
				return null;
		}
	}

}
