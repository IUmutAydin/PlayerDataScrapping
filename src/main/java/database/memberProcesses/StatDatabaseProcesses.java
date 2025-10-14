package database.memberProcesses;

import java.sql.Types;

import members.stat.PlayerMatch;
import members.stat.StatRecord;
import members.stat.XGRecord;

public class StatDatabaseProcesses extends MemberDatabaseProcesses {

	/*
	 * public static int getStatTypeCount() { String statement =
	 * "SELECT count(stat_typeID) FROM stat_type";
	 *
	 * return fetchSingleData(statement, null, wrapper(rs -> rs.getInt(1))); }
	 *
	 * public static void insertStatType(String statType) { String statement =
	 * "INSERT INTO stat_type VALUES(?, ?)";
	 *
	 * executeUpdate(statement, ps -> { ps.setInt(1, getStatTypeCount() + 1);
	 * ps.setString(2, statType); }); }
	 *
	 * public static int getStatTypeID(String statType) { String statement =
	 * "SELECT stat_typeID FROM stat_type WHERE stat_type_name = ?";
	 *
	 * return fetchSingleData(statement, ps -> ps.setString(1, statType), wrapper(rs
	 * -> rs.getInt(1))); }
	 *
	 * public static int getStatRecordTypeCount() { String statement =
	 * "SELECT count(stat_typeID) FROM stat_record_type";
	 *
	 * return fetchSingleData(statement, null, wrapper(rs -> rs.getInt(1))); }
	 *
	 * public static void insertStatRecordType(String statType, String
	 * statRecordType) { String statement =
	 * "INSERT INTO stat_record_type VALUES(?, ?, ?)";
	 *
	 * executeUpdate(statement, ps -> { ps.setInt(1, getStatRecordTypeCount() + 1);
	 * ps.setInt(2, getStatTypeID(statType)); ps.setString(3, statRecordType); }); }
	 */

	public static void insertPlayerMatch(PlayerMatch pm) {
		String statement = "INSERT INTO player_match VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

		executeUpdate(statement, ps -> {
			ps.setInt(1, pm.getPlayerID());
			ps.setInt(2, pm.getMatchID());
			ps.setInt(3, pm.getPlayedFor());
			ps.setInt(4, pm.getPlayedAgainst());
			ps.setInt(5, pm.getStartSecond());
			ps.setInt(6, pm.getStartHalf());
			ps.setInt(7, pm.getSubstituteOffSecond());
			ps.setInt(8, pm.getSubstituteOffHalf());
		});
	}

	public static void removePlayerMatch(int matchID) {
		String statement = "DELETE FROM player_match WHERE matchID = ?";

		removeXGRecordPatternType(matchID);
		executeUpdate(statement, ps -> ps.setInt(1, matchID));
	}

	public static void insertStatRecord(StatRecord sr) {
		String statement = "INSERT INTO stat_record VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

		executeUpdate(statement, ps -> {
			ps.setInt(1, sr.getStatRecordType());
			ps.setInt(2, sr.getPlayerID());
			ps.setInt(3, sr.getMatchID());
			isNumericDataNull(ps, 4, sr.getStartX());
			isNumericDataNull(ps, 5, sr.getStartY());
			isNumericDataNull(ps, 6, sr.getEndX());
			isNumericDataNull(ps, 7, sr.getEndY());
			ps.setInt(8, sr.getSecond());
			ps.setInt(9, sr.getHalf());
		});
	}

	public static void insertStatRecord1(StatRecord sr) {
		String statement = "INSERT INTO stat_record1 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

		if (!isStatRecordExist(sr))
			executeUpdate(statement, ps -> {
				ps.setString(1, sr.getStatRecordID());
				ps.setInt(2, sr.getPlayerID());
				ps.setInt(3, sr.getMatchID());
				isNumericDataNull(ps, 4, sr.getStartX());
				isNumericDataNull(ps, 5, sr.getStartY());
				isNumericDataNull(ps, 6, sr.getEndX());
				isNumericDataNull(ps, 7, sr.getEndY());
				ps.setInt(8, sr.getSecond());
				ps.setInt(9, sr.getHalf());
			});

		insertStatRecordType(sr);
	}

	public static void insertStatRecordType(StatRecord sr) {
		String statement = "INSERT INTO stat_record_stat_record_type VALUES(?, ?)";

		executeUpdate(statement, ps -> {
			ps.setString(1, sr.getStatRecordID());
			ps.setInt(2, sr.getStatRecordType());
		});
	}

	public static boolean isStatRecordExist(StatRecord sr) {
		String statement = "SELECT * FROM stat_record1 WHERE matchID = ? AND stat_recordID = ?";

		return exists(statement, ps -> {
			ps.setInt(1, sr.getMatchID());
			ps.setString(2, sr.getStatRecordID());
		}, rs -> rs.next());
	}

	public static void removeStatRecord(int matchID) {
		String statement = "DELETE FROM stat_record WHERE matchID = ?";

		executeUpdate(statement, ps -> ps.setInt(1, matchID));
	}

	public static void insertXGRecord(XGRecord xgRecord) {
		String statement = "INSERT INTO xg_record VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Integer xgRecordID = getXGRecordID(xgRecord.getMatchID(), xgRecord.getSecond());

		if (xgRecordID == null) {
			executeUpdate(statement, ps -> {
				ps.setInt(1, xgRecord.getPlayerID());
				ps.setInt(2, xgRecord.getMatchID());
				ps.setInt(3, xgRecord.getOutcomeID());
				ps.setInt(4, xgRecord.getHitTypeID());
				ps.setDouble(5, xgRecord.getStartX());
				ps.setDouble(6, xgRecord.getStartY());
				ps.setNull(7, Types.NUMERIC);
				ps.setNull(8, Types.NUMERIC);
				ps.setInt(9, xgRecord.getSecond());
				ps.setInt(10, xgRecord.getHalf());
				ps.setDouble(11, xgRecord.getXgValue());
			});

			xgRecordID = getXGRecordID(xgRecord.getMatchID(), xgRecord.getSecond());
		}

		insertPatternTypeOfXGRecord(xgRecordID, xgRecord.getPatternTypeID());
	}

	public static void removeXGRecord(int matchID) {
		String statement = "DELETE FROM xg_record WHERE matchID = ?";

		removeXGRecordPatternType(matchID);
		executeUpdate(statement, ps -> ps.setInt(1, matchID));
	}

	public static void removeXGRecordPatternType(int matchID) {
		String statement = "DELETE FROM xg_record_pattern_type WHERE xg_recordID IN(SELECT xg_recordID FROM xg_record WHERE matchID = ?)";

		executeUpdate(statement, ps -> ps.setInt(1, matchID));
	}

	public static void insertPatternTypeOfXGRecord(int xgRecordID, int patternTypeID) {
		String statement = "INSERT INTO xg_record_pattern_type VALUES(?, ?)";

		executeUpdate(statement, ps -> {
			ps.setInt(1, xgRecordID);
			ps.setInt(2, patternTypeID);
		});
	}

	public static Integer getXGRecordID(int matchID, int second) {
		String statement = "SELECT xg_recordID FROM xg_record WHERE matchID = ? AND second = ?";

		return fetchSingleData(statement, ps -> {
			ps.setInt(1, matchID);
			ps.setInt(2, second);
		}, wrapper(rs -> rs.getInt(1)));
	}

	public static Integer getStatRecordTypeID(String statRecordType) {
		String statement = "SELECT stat_record_typeID FROM stat_record_type WHERE stat_record_type_name = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, statRecordType), wrapper(rs -> rs.getInt(1)));
	}

	public static int getOutcomeTypeID(String outcomeType) {
		String statement = "SELECT outcomeID FROM outcome WHERE outcomeDesc = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, outcomeType), wrapper(rs -> rs.getInt(1)));
	}

	public static int getPatternTypeID(String patternType) {
		String statement = "SELECT pattern_typeID FROM pattern_type WHERE patternDesc = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, patternType), wrapper(rs -> rs.getInt(1)));
	}

	public static int getHitTypeID(String hitType) {
		String statement = "SELECT hit_typeID FROM hit_type WHERE hitDesc = ?";

		return fetchSingleData(statement, ps -> ps.setString(1, hitType), wrapper(rs -> rs.getInt(1)));
	}
}
