package database.memberProcesses;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import members.match.Match;

public class MatchDatabaseProcesses extends MemberDatabaseProcesses {

	public static void updateMatchURLs(String matchURL, int completed) {
		String statement = "UPDATE match_helper SET completed = ? WHERE match_url = ?";

		executeUpdate(statement, ps -> {
			ps.setInt(1, completed);
			ps.setString(2, matchURL);
		});
	}

	public static String getMatchURL(int competitionID, int completed) {
		String statement = "SELECT * FROM match_helper WHERE competitionID = ? AND completed = ?";

		return fetchSingleData(statement, ps -> {
			ps.setInt(1, competitionID);
			ps.setInt(2, completed);
		}, wrapper(rs -> rs.getString(1)));
	}

	public static List<String> getMatchURLs(int competitionID, int completed) {
		String statement = "SELECT * FROM match_helper WHERE competitionID = ? AND completed = ?";

		return fetchMultipleData(statement, ps -> {
			ps.setInt(1, competitionID);
			ps.setInt(2, completed);
		}, wrapper(rs -> rs.getString(1)), ArrayList::new);
	}

	public static void insertConditionally(String url, int competitionID) {
		if (!exist(url))
			insertMatchURL(url, competitionID);
	}

	public static void insertMatchURL(String url, int competitionID) {
		String statement = "INSERT INTO match_helper VALUES(?, ?, 0)";

		executeUpdate(statement, ps -> {
			ps.setString(1, url);
			ps.setInt(2, competitionID);
		});
	}

	private static boolean exist(String url) {
		String statement = "SELECT * FROM match_helper WHERE match_url = ?";

		return exists(statement, ps -> ps.setString(1, url), rs -> rs.next());
	}

	public static void insertConditionally(Match match) {
		if (!exists(match.getHomeTeamID(), match.getAwayTeamID(), match.getMatchDate()))
			insert(match);
	}

	public static void insert(Match match) {
		String statement = "INSERT INTO match VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		executeUpdate(statement, ps -> {
			isNumericDataNull(ps, 1, match.getHomeTeamID());
			isNumericDataNull(ps, 2, match.getAwayTeamID());
			ps.setDate(3, match.getMatchDate());
			isNumericDataNull(ps, 4, match.getCompetitionID());
			ps.setInt(5, 24);
			ps.setInt(6, 25);
			ps.setInt(7, isSecondTerm(match.getAwayTeamID(), match.getHomeTeamID(), match.getCompetitionID()) ? 2 : 1);
			ps.setString(8, match.getVenue());
			isNumericDataNull(ps, 9, match.getAttendance());
			ps.setString(10, match.getReferee());
			isNumericDataNull(ps, 11, match.getHomeScore(), -1);
			isNumericDataNull(ps, 12, match.getAwayScore(), -1);
		});
	}

	private static boolean isSecondTerm(int homeTeamID, int awayTeamID, int competitionID) {
		String statement = "SELECT * FROM match WHERE home_teamID = ? AND away_teamID = ? AND competitionID = ?";

		return exists(statement, ps -> {
			ps.setInt(1, homeTeamID);
			ps.setInt(2, awayTeamID);
			ps.setInt(3, competitionID);
		}, rs -> rs.next());
	}

	public static boolean exists(int homeTeamID, int awayTeamID, Date matchDate) {
		String statement = "SELECT * FROM match WHERE home_teamID = ? AND away_teamID = ? AND match_date = ?";

		return exists(statement, ps -> {
			ps.setInt(1, homeTeamID);
			ps.setInt(2, awayTeamID);
			ps.setDate(3, matchDate);
		}, rs -> rs.next());
	}

	public static Integer getID(int homeTeamID, int awayTeamID, int competitionID, int season) {
		String statement = "SELECT matchID FROM match WHERE home_teamID = ? AND away_teamID = ? AND competitionID = ? AND season1 = ?";

		return fetchSingleData(statement, ps -> {
			ps.setInt(1, homeTeamID);
			ps.setInt(2, awayTeamID);
			ps.setInt(3, competitionID);
			ps.setInt(4, season);
		}, wrapper(rs -> rs.getInt(1)));
	}

	public static void updateAttendance(int matchID, int attendance) {
		if (isAttendanceNull(matchID)) {
			String statement = "UPDATE match SET attendance = ? WHERE matchID = ?";

			executeUpdate(statement, ps -> {
				ps.setInt(1, attendance);
				ps.setInt(2, matchID);
			});
		}
	}

	public static boolean isAttendanceNull(int matchID) {
		String statement = "SELECT attendance FROM match WHERE matchID = ?";

		return isDataNull(statement, ps -> ps.setInt(1, matchID), rs -> rs.getInt(1));
	}

	public static void updateReferee(int matchID, String referee) {
		if (isRefereeNull(matchID)) {
			String statement = "UPDATE match SET referee = ? WHERE matchID = ?";

			executeUpdate(statement, ps -> {
				ps.setString(1, referee);
				ps.setInt(2, matchID);
			});
		}
	}

	public static boolean isRefereeNull(int matchID) {
		String statement = "SELECT referee FROM match WHERE matchID = ?";

		return isDataNull(statement, ps -> ps.setInt(1, matchID), rs -> rs.getString(1));
	}

	public static void updateHomeScore(int matchID, int homeScore) {
		if (isHomeScoreNull(matchID)) {
			String statement = "UPDATE match SET home_score = ? WHERE matchID = ?";

			executeUpdate(statement, ps -> {
				ps.setInt(1, homeScore);
				ps.setInt(2, matchID);
			});
		}
	}

	public static boolean isHomeScoreNull(int matchID) {
		String statement = "SELECT home_score FROM match WHERE matchID = ?";

		return isDataNull(statement, ps -> {
			ps.setInt(1, matchID);
		}, rs -> rs.getInt(1));
	}

	public static void updateAwayScore(int matchID, int awayScore) {
		if (isAwayScoreNull(matchID)) {
			String statement = "UPDATE match SET away_score = ? WHERE matchID = ?";

			executeUpdate(statement, ps -> {
				ps.setInt(1, awayScore);
				ps.setInt(2, matchID);
			});
		}
	}

	public static boolean isAwayScoreNull(int matchID) {
		String statement = "SELECT away_score FROM match WHERE matchID = ?";

		return isDataNull(statement, ps -> {
			ps.setInt(1, matchID);
		}, rs -> rs.getInt(1));
	}

	public static void main(String[] args) {
		updateMatchURLs(
				"https://theanalyst.com/2023/07/opta-football-match-centre/?competitionId=1r097lpxe0xn03ihb7wi98kao&seasonId=b25u56idqlgo8s1rahhltqd5g&matchId=a04ym4782kku9ps4fwrs2w5jo",
				0);
		updateMatchURLs(
				"https://theanalyst.com/2023/07/opta-football-match-centre/?competitionId=1r097lpxe0xn03ihb7wi98kao&seasonId=b25u56idqlgo8s1rahhltqd5g&matchId=a1pp0j40dkl32ltn9zjrkgsus",
				0);
		StatDatabaseProcesses.removePlayerMatch(1157);
		StatDatabaseProcesses.removeStatRecord(1157);
	}
}
